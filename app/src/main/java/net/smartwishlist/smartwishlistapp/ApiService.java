package net.smartwishlist.smartwishlistapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.widget.Toast;

import com.appspot.smart_wish_list.smartwishlist.Smartwishlist;
import com.appspot.smart_wish_list.smartwishlist.model.SmartWishListAppNotificationData;
import com.appspot.smart_wish_list.smartwishlist.model.SmartWishListCheckResult;
import com.appspot.smart_wish_list.smartwishlist.model.SmartWishListRegisterGcmDeviceParameters;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.IOException;

public class ApiService {

    public static boolean isConnected(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    public static Smartwishlist getApiServiceHandle(Context context) {
        Smartwishlist.Builder builder = new Smartwishlist.Builder(AppConstants.HTTP_TRANSPORT,
                AppConstants.getJsonFactory(), null);
        if (BuildConfig.DEBUG) {
            builder.setRootUrl(BuildConfig.LOCAL_API_URL);
        }
        String versionName = AppConstants.Version.getAppVersionName(context);
        builder.setApplicationName(AppConstants.APP_NAME + " v" + versionName);
        return builder.build();
    }

    public static abstract class ApiTaskWithExponentialBackOff<Params, Result>
            extends AsyncTask<Params, Void, Result> {

        private final Context context;
        private final Smartwishlist service;

        protected ApiTaskWithExponentialBackOff(Context context) {
            this.context = context;
            this.service = ApiService.getApiServiceHandle(context);
        }

        protected Context getContext() {
            return context;
        }

        protected Smartwishlist getService() {
            return service;
        }

        @SafeVarargs
        @Override
        protected final Result doInBackground(Params... params) {
            long backoff = SyncTaskWithExponentialBackoff.BACKOFF_MILLI_SECONDS +
                    SyncTaskWithExponentialBackoff.random.nextInt(1000);
            for (int i = 1; i <= SyncTaskWithExponentialBackoff.MAX_ATTEMPTS; i++) {
                if (!isConnected()) break;
                if (isCancelled()) break;

                try {
                    return tryInBackground(params);
                } catch (IOException e) {
                    if (i == SyncTaskWithExponentialBackoff.MAX_ATTEMPTS) {
                        AppLogging.logException(e);
                        break;
                    }
                    try {
                        Thread.sleep(backoff);
                    } catch (InterruptedException e1) {
                        Thread.currentThread().interrupt();
                    }
                    backoff *= 2;
                }
            }
            return null;
        }

        @SuppressWarnings("unchecked")
        protected abstract Result tryInBackground(Params... params) throws IOException;

        protected boolean isConnected() {
            return ApiService.isConnected(context);
        }
    }

    public static class CheckClientIdTask
            extends ApiTaskWithExponentialBackOff<String, SmartWishListCheckResult> {

        public CheckClientIdTask(Context context) {
            super(context);
        }

        protected SmartWishListCheckResult tryInBackground(String... strings)
                throws IOException {
            Smartwishlist.Client.CheckId checkId = getService().client().checkId(strings[0]);
            checkId.setIsApp(Boolean.TRUE);
            AppLogging.logDebug("CheckClientIdTask.tryInBackground: URL=[" +
                    checkId.buildHttpRequestUrl().toString() + "]");
            return checkId.execute();
        }

        @Override
        protected void onPostExecute(SmartWishListCheckResult smartWishListCheckResult) {
            if (smartWishListCheckResult != null) {
                Boolean isValid = smartWishListCheckResult.getValid();
                if (isValid) {
                    return;
                }
            }
            AppLogging.logError("Invalid client ID");
            AppPreferences preferences = new AppPreferences(getContext());
            preferences.resetAll();
            Toast toast = Toast.makeText(getContext(),
                    R.string.invalid_client_id,
                    Toast.LENGTH_LONG);
            toast.show();
        }
    }

    private static class AppNotificationsHelper {

        private final Context applicationContext;
        private final AppPreferences preferences;
        private final Smartwishlist service;

        public AppNotificationsHelper(Context context) {
            this.applicationContext = context.getApplicationContext();
            this.preferences = new AppPreferences(applicationContext);
            this.service = ApiService.getApiServiceHandle(context);
        }

        public SmartWishListAppNotificationData getAppNotifications()
                throws IOException {
            String clientId = preferences.getClientId();
            String token = preferences.getToken();
            if (clientId == null || token == null) {
                return null;
            }
            double timestamp = ApiSignature.getTimestamp();
            String signature = ApiSignature.generateRequestSignature(
                    token, "", timestamp);
            Smartwishlist.AppNotifications.List request = service.appNotifications().list(
                    clientId, timestamp, signature);
            request.setIsApp(Boolean.TRUE);
            return request.execute();
        }

        public void showNotifications(SmartWishListAppNotificationData data) {
            if (data == null) {
                AppLogging.logError("AppNotificationsHelper: no results");
                return;
            } else if (data.getErrorCode() != AppConstants.NO_ERRORS) {
                AppLogging.logError("AppNotificationsHelper: error code: " +
                        data.getErrorCode());
                return;
            }

            AppNotification appNotification = new AppNotification(applicationContext);
            appNotification.show(data);
        }

        public void storeNotifications(
                SmartWishListAppNotificationData smartWishListAppNotificationData) {
            AppStorage appStorage = AppStorage.getInstance(applicationContext);
            appStorage.deleteAllOldNotifications(preferences.getLastViewedNotifications());
            appStorage.insertNotifications(smartWishListAppNotificationData.getTriggers());
        }
    }

    public static class FetchAppNotificationsSyncTask
            extends SyncTaskWithExponentialBackoff {

        private final AppNotificationsHelper helper;

        public FetchAppNotificationsSyncTask(Context context) {
            helper = new AppNotificationsHelper(context);
        }

        @Override
        protected void tryDoing() throws IOException {
            SmartWishListAppNotificationData data = helper.getAppNotifications();
            helper.showNotifications(data);
            helper.storeNotifications(data);
        }

        @Override
        protected void handleFailure(Exception e) {
            AppLogging.logException(e);
        }
    }

    public static class FetchAppNotificationsTask
            extends ApiTaskWithExponentialBackOff<Void, SmartWishListAppNotificationData> {

        private final AppNotificationsHelper helper;

        public FetchAppNotificationsTask(Context context) {
            super(context.getApplicationContext());
            helper = new AppNotificationsHelper(context);
        }

        @Override
        protected SmartWishListAppNotificationData tryInBackground(Void... voids)
                throws IOException {
            SmartWishListAppNotificationData data = helper.getAppNotifications();

            StoreNotificationsTask task = new StoreNotificationsTask(helper);
            task.execute(data);

            return data;
        }

        @Override
        protected void onPostExecute(
                SmartWishListAppNotificationData smartWishListAppNotificationData) {
            helper.showNotifications(smartWishListAppNotificationData);
        }
    }

    private static class StoreNotificationsTask
            extends AsyncTask<SmartWishListAppNotificationData, Void, Void> {

        private final AppNotificationsHelper helper;

        public StoreNotificationsTask(AppNotificationsHelper helper) {
            this.helper = helper;
        }

        @Override
        protected Void doInBackground(
                SmartWishListAppNotificationData... smartWishListAppNotificationData) {
            helper.storeNotifications(smartWishListAppNotificationData[0]);
            return null;
        }
    }

    public static class GetTokenAndSendToServerSyncTask
            extends SyncTaskWithExponentialBackoff {

        private final Context context;
        private final AppPreferences preferences;

        public GetTokenAndSendToServerSyncTask(Context context) {
            this.context = context;
            preferences = new AppPreferences(context);
        }

        @Override
        protected void tryDoing() throws IOException {
            getGcmTokenAndSendToServer();
        }

        @Override
        protected void handleFailure(Exception e) {
            preferences.setGcmTokenSent(false);
            AppLogging.logException(e);
        }

        private void getGcmTokenAndSendToServer() throws IOException {
            String registrationId = FirebaseInstanceId.getInstance().getToken();
            if (registrationId != null) {
                String clientId = preferences.getClientId();
                if (clientId == null) {
                    return;
                }

                preferences.setGcmTokenSent(true);
                String token = preferences.getToken();
                double timestamp = ApiSignature.getTimestamp();
                String signature = ApiSignature.generateRequestSignature(
                        token, registrationId, timestamp);
                SmartWishListRegisterGcmDeviceParameters parameters =
                        new SmartWishListRegisterGcmDeviceParameters();
                parameters.setRegistrationId(registrationId);
                Smartwishlist.AppNotifications.Register request =
                        getApiServiceHandle(context).appNotifications().register(
                                clientId, timestamp, signature, parameters);
                request.setIsApp(Boolean.TRUE);
                request.execute();
            }
        }
    }

    public static class GetTokenAndSendToServerAsyncTask
            extends AsyncTask<Void, Void, Void> {

        private final Context context;

        public GetTokenAndSendToServerAsyncTask(Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            GetTokenAndSendToServerSyncTask task = new GetTokenAndSendToServerSyncTask(context);
            task.doSynchronized();
            return null;
        }
    }

    public static class DeleteTokenFromServerSyncTask
        extends SyncTaskWithExponentialBackoff{

        private final String oldClientId;
        private final Context context;
        private final AppPreferences preferences;

        public DeleteTokenFromServerSyncTask(String oldClientId, Context context) {
            this.oldClientId = oldClientId;
            this.context = context;
            preferences = new AppPreferences(context);
        }

        @Override
        protected void tryDoing() throws IOException {
            String registrationId = FirebaseInstanceId.getInstance().getToken();
            if (registrationId != null && oldClientId != null) {
                String token = preferences.getToken();
                double timestamp = ApiSignature.getTimestamp();
                String signature = ApiSignature.generateRequestSignature(
                        token, registrationId, timestamp);
                SmartWishListRegisterGcmDeviceParameters parameters =
                        new SmartWishListRegisterGcmDeviceParameters();
                parameters.setRegistrationId(registrationId);
                Smartwishlist.AppNotifications.Delete request =
                        getApiServiceHandle(context).appNotifications().delete(
                                oldClientId, timestamp, signature, parameters);
                request.setIsApp(Boolean.TRUE);
                request.execute();
            }
            preferences.resetAll();
        }

        @Override
        protected void handleFailure(Exception e) {
            AppLogging.logException(e);
        }
    }

    public static class DeleteTokenFromServerAsyncTask
            extends AsyncTask<Void, Void, Void> {

        private final String oldClientId;
        private final Context context;

        public DeleteTokenFromServerAsyncTask(String oldClientId, Context context) {
            this.oldClientId = oldClientId;
            this.context = context;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            DeleteTokenFromServerSyncTask task = new DeleteTokenFromServerSyncTask(oldClientId,
                    context);
            task.doSynchronized();
            return null;
        }
    }
}
