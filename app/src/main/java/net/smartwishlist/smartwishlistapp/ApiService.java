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

import java.io.IOException;
import java.util.Random;

public class ApiService {

    private static final int MAX_ATTEMPTS = 5;
    private static final int BACKOFF_MILLI_SECONDS = 2000;
    private static final Random random = new Random();

    public static boolean isConnected(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    private static Smartwishlist getApiServiceHandle(Context context) {
        Smartwishlist.Builder builder = new Smartwishlist.Builder(AppConstants.HTTP_TRANSPORT,
                AppConstants.JSON_FACTORY, null);
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
            long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
            for (int i = 1; i <= MAX_ATTEMPTS; i++) {
                if (!isConnected()) break;
                if (isCancelled()) break;

                try {
                    return tryInBackground(params);
                } catch (IOException e) {
                    if (i == MAX_ATTEMPTS) {
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
            checkId.setIsApp(true);
            AppLogging.logDebug("CheckClientIdTask.tryInBackground: URL=[" + checkId.buildHttpRequestUrl().toString() + "]");
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

    public static class RegisterGcmDeviceTask
            extends ApiTaskWithExponentialBackOff<String, Boolean> {

        public RegisterGcmDeviceTask(Context context) {
            super(context);
        }

        @Override
        protected Boolean tryInBackground(String... strings) throws IOException {
            AppPreferences preferences = new AppPreferences(getContext());
            String clientId = preferences.getClientId();
            String token = preferences.getToken();
            double timestamp = ApiSignature.getTimestamp();
            String registrationId = strings[0];
            String signature = ApiSignature.generateRequestSignature(
                    token, registrationId, timestamp);
            SmartWishListRegisterGcmDeviceParameters parameters =
                    new SmartWishListRegisterGcmDeviceParameters();
            parameters.setRegistrationId(registrationId);
            Smartwishlist.AppNotifications.Register request =
                    getService().appNotifications().register(clientId, timestamp, signature,
                            parameters);
            request.setIsApp(true);
            request.execute();
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            AppPreferences preferences = new AppPreferences(getContext());
            if (aBoolean == null) {
                preferences.setGcmTokenSent(false);
                Toast toast = Toast.makeText(getContext(),
                        R.string.gcm_registration_failed,
                        Toast.LENGTH_LONG);
                toast.show();
            } else {
                preferences.setGcmTokenSent(aBoolean);
            }
        }
    }

    public static abstract class ListAppNotificationsTask
            extends ApiTaskWithExponentialBackOff<Void, SmartWishListAppNotificationData> {

        protected ListAppNotificationsTask(Context context) {
            super(context);
        }

        @Override
        protected SmartWishListAppNotificationData tryInBackground(Void... voids)
                throws IOException {
            AppPreferences preferences = new AppPreferences(getContext());
            String clientId = preferences.getClientId();
            String token = preferences.getToken();
            if (clientId == null || token == null) {
                return null;
            }
            double timestamp = ApiSignature.getTimestamp();
            String signature = ApiSignature.generateRequestSignature(
                    token, "", timestamp);
            Smartwishlist.AppNotifications.List request = getService().appNotifications().list(
                    clientId, timestamp, signature);
            request.setIsApp(true);
            return request.execute();
        }
    }
}
