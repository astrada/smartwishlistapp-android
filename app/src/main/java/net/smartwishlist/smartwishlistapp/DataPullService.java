package net.smartwishlist.smartwishlistapp;

import android.app.IntentService;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.appspot.smart_wish_list.smartwishlist.Smartwishlist;
import com.appspot.smart_wish_list.smartwishlist.model.SmartWishListAppNotificationData;
import com.appspot.smart_wish_list.smartwishlist.model.SmartWishListAppNotificationParameters;

import java.io.IOException;
import java.util.Locale;

public class DataPullService extends IntentService {

    public DataPullService() {
        super("DataPullService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            AppPreferences preferences = new AppPreferences(getApplicationContext());
            FetchAppNotificationsTask task = new FetchAppNotificationsTask(preferences);
            double lastPoll = preferences.getLastServerPoll();
            task.execute(lastPoll);
        }
    }

    private class FetchAppNotificationsTask extends AsyncTask<Double, Void, SmartWishListAppNotificationData> {
        private AppPreferences preferences;
        private Smartwishlist service;
        private double timestamp;

        public FetchAppNotificationsTask(AppPreferences preferences) {
            this.preferences = preferences;
            this.service = AppConstants.getApiServiceHandle();
        }

        @Override
        protected SmartWishListAppNotificationData doInBackground(Double... doubles) {
            SmartWishListAppNotificationData result = null;
            try {
                String clientId = preferences.getClientId();
                String token = preferences.getToken();
                timestamp = System.currentTimeMillis() / 1000.0;
                //double since = doubles[0];
                double since = 0.0;
                String signature = ApiSignature.generateRequestSignature(
                        token, String.format(Locale.US, "%.3f", since), timestamp);
                SmartWishListAppNotificationParameters parameters = new SmartWishListAppNotificationParameters();
                parameters.setSince(since);
                Smartwishlist.AppNotifications.List request = service.appNotifications().list(
                        clientId, timestamp, signature, parameters);
                request.setIsApp(true);
                result = request.execute();
            } catch (IOException e) {
                Log.d(AppConstants.LOG_TAG, e.getMessage(), e);
            }
            return result;
        }

        @Override
        protected void onPostExecute(SmartWishListAppNotificationData smartWishListAppNotificationData) {
            if (smartWishListAppNotificationData == null
                    || smartWishListAppNotificationData.getErrorCode() != AppConstants.NO_ERRORS) {
                Log.d(AppConstants.LOG_TAG, "Error!");
                return;
            }

            preferences.setLastServerPoll(timestamp);

            AppStorage appStorage = new AppStorage(getApplicationContext());
            appStorage.deleteAllPastNotifications();
            appStorage.insertNotifications(smartWishListAppNotificationData.getTriggers());

            AppNotification appNotification = new AppNotification(getApplicationContext());
            appNotification.show(smartWishListAppNotificationData);
        }
    }

}
