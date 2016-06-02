package net.smartwishlist.smartwishlistapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.widget.Toast;

import com.appspot.smart_wish_list.smartwishlist.Smartwishlist;
import com.appspot.smart_wish_list.smartwishlist.model.SmartWishListAppNotificationData;
import com.appspot.smart_wish_list.smartwishlist.model.SmartWishListCheckResult;

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

    private static class FetchAppNotifications {

        private final Context context;
        private final Smartwishlist service;

        public FetchAppNotifications(Context context) {
            this.context = context;
            this.service = ApiService.getApiServiceHandle(context);
        }

        public SmartWishListAppNotificationData getAppNotifications()
                throws IOException {
            AppPreferences preferences = new AppPreferences(context);
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
                AppLogging.logError("FetchAppNotificationsTask: no results");
                return;
            } else if (data.getErrorCode() != AppConstants.NO_ERRORS) {
                AppLogging.logError("FetchAppNotificationsTask: error code: " +
                        data.getErrorCode());
                return;
            }

            StoreNotificationsTask task = new StoreNotificationsTask(context);
            task.execute(data);

            AppNotification appNotification = new AppNotification(context.getApplicationContext());
            appNotification.show(data);
        }
    }

    public static class FetchAppNotificationsSyncTask
            extends SyncTaskWithExponentialBackoff {

        private FetchAppNotifications implementation;

        public FetchAppNotificationsSyncTask(Context context) {
            this.implementation = new FetchAppNotifications(context);
        }

        @Override
        protected void tryDoing() throws IOException {
            SmartWishListAppNotificationData data = implementation.getAppNotifications();
            implementation.showNotifications(data);
        }

        @Override
        protected void handleFailure(Exception e) {
            AppLogging.logException(e);
        }
    }

    public static class FetchAppNotificationsTask
            extends ApiTaskWithExponentialBackOff<Void, SmartWishListAppNotificationData> {

        private FetchAppNotifications implementation;

        public FetchAppNotificationsTask(Context context) {
            super(context.getApplicationContext());
            this.implementation = new FetchAppNotifications(context);
        }

        @Override
        protected SmartWishListAppNotificationData tryInBackground(Void... voids)
                throws IOException {
            return implementation.getAppNotifications();
        }

        @Override
        protected void onPostExecute(
                SmartWishListAppNotificationData smartWishListAppNotificationData) {
            implementation.showNotifications(smartWishListAppNotificationData);
        }
    }

    public static class StoreNotificationsTask
            extends AsyncTask<SmartWishListAppNotificationData, Void, Void> {

        private final Context context;

        public StoreNotificationsTask(Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(
                SmartWishListAppNotificationData... smartWishListAppNotificationData) {
            Context context = this.context.getApplicationContext();
            AppStorage appStorage = AppStorage.getInstance(context);
            AppPreferences preferences = new AppPreferences(context);
            appStorage.deleteAllOldNotifications(preferences.getLastViewedNotifications());
            appStorage.insertNotifications(smartWishListAppNotificationData[0].getTriggers());
            return null;
        }
    }
}
