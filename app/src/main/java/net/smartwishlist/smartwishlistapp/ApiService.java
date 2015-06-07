package net.smartwishlist.smartwishlistapp;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.appspot.smart_wish_list.smartwishlist.Smartwishlist;
import com.appspot.smart_wish_list.smartwishlist.model.SmartWishListAppNotificationData;
import com.appspot.smart_wish_list.smartwishlist.model.SmartWishListCheckResult;
import com.appspot.smart_wish_list.smartwishlist.model.SmartWishListRegisterGcmDeviceParameters;
import com.appspot.smart_wish_list.smartwishlist.model.SmartWishListUserData;

import java.io.IOException;
import java.util.Locale;

public class ApiService {

    private static Smartwishlist getApiServiceHandle(Context context) {
        Smartwishlist.Builder smartwishlist = new Smartwishlist.Builder(AppConstants.HTTP_TRANSPORT,
                AppConstants.JSON_FACTORY, null);

        if (BuildConfig.DEBUG) {
            smartwishlist.setRootUrl(BuildConfig.LOCAL_API_URL);
        }
        String versionName = AppConstants.Version.getAppVersionName(context);
        smartwishlist.setApplicationName(AppConstants.APP_NAME + " v" + versionName);
        return smartwishlist.build();
    }

    public static abstract class ListAppNotificationsTask
            extends AsyncTask<Void, Void, SmartWishListAppNotificationData> {

        private final AppPreferences preferences;
        private final Smartwishlist service;
        private double timestamp;

        protected ListAppNotificationsTask(Context context) {
            this.preferences = new AppPreferences(context);
            this.service = ApiService.getApiServiceHandle(context);
        }

        protected AppPreferences getPreferences() {
            return preferences;
        }

        @Override
        protected SmartWishListAppNotificationData doInBackground(Void... voids) {
            SmartWishListAppNotificationData result = null;
            try {
                String clientId = preferences.getClientId();
                String token = preferences.getToken();
                if (clientId == null || token == null) {
                    return null;
                }
                timestamp = ApiSignature.getTimestamp();
                String signature = ApiSignature.generateRequestSignature(
                        token, "", timestamp);
                Smartwishlist.AppNotifications.List request = service.appNotifications().list(
                        clientId, timestamp, signature);
                request.setIsApp(true);
                result = request.execute();
                preferences.setLastServerPoll(timestamp);
            } catch (IOException e) {
                Log.d(AppConstants.LOG_TAG, e.getMessage(), e);
            }
            return result;
        }
    }

    public static class RegisterGcmDeviceTask extends AsyncTask<String, Void, Void> {

        private final AppPreferences preferences;
        private final Smartwishlist service;

        public RegisterGcmDeviceTask(Context context) {
            this.preferences = new AppPreferences(context);
            this.service = ApiService.getApiServiceHandle(context);
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
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
                        service.appNotifications().register(clientId, timestamp, signature,
                                parameters);
                request.setIsApp(true);
                request.execute();
            } catch (IOException e) {
                Log.d(AppConstants.LOG_TAG, e.getMessage(), e);
            }
            return null;
        }
    }

    public static class CheckClientIdTask extends AsyncTask<String, Void, SmartWishListCheckResult> {

        private final Smartwishlist service;

        public CheckClientIdTask(Context context) {
            this.service = ApiService.getApiServiceHandle(context);
        }

        protected SmartWishListCheckResult doInBackground(String... strings) {
            SmartWishListCheckResult result = null;
            try {
                Smartwishlist.Client.CheckId checkId = service.client().checkId(strings[0]);
                checkId.setIsApp(true);
                result = checkId.execute();
            } catch (IOException e) {
                Log.d(AppConstants.LOG_TAG, e.getMessage(), e);
            }
            return result;
        }

        @Override
        protected void onPostExecute(SmartWishListCheckResult smartWishListCheckResult) {
            Boolean isValid = smartWishListCheckResult.getValid();
            if (!isValid) {
                // TODO
                Log.d(AppConstants.LOG_TAG, "Invalid clientID");
            }
        }
    }
}
