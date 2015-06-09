package net.smartwishlist.smartwishlistapp;

import android.os.AsyncTask;
import android.os.Bundle;

import com.appspot.smart_wish_list.smartwishlist.model.SmartWishListAppNotificationData;
import com.google.android.gms.gcm.GcmListenerService;

public class AppGcmListenerService extends GcmListenerService {

    @Override
    public void onMessageReceived(String from, Bundle data) {
        if (data != null && !data.isEmpty()) {
            String message = data.getString("type");
            if (message != null) {
                switch (message) {
                    case "price-alert":
                        AppPreferences preferences = new AppPreferences(this);
                        if (preferences.getNotificationEnabled()) {
                            FetchAppNotificationsTask task = new FetchAppNotificationsTask();
                            task.execute();
                        }
                        break;
                    default:
                        AppLogging.logError("Unexpected GCM message type: " + message);
                }
            } else {
                AppLogging.logError("Unexpected GCM message from: " + from +
                        " data: " + data.toString());
            }
        } else {
            AppLogging.logError("Unexpected GCM message from: " + from);
        }
    }

    private class FetchAppNotificationsTask
            extends ApiService.ListAppNotificationsTask {

        public FetchAppNotificationsTask() {
            super(AppGcmListenerService.this);
        }

        @Override
        protected void onPostExecute(SmartWishListAppNotificationData smartWishListAppNotificationData) {
            if (smartWishListAppNotificationData == null) {
                AppLogging.logError("FetchAppNotificationsTask: no results");
                return;
            } else if (smartWishListAppNotificationData.getErrorCode() != AppConstants.NO_ERRORS) {
                AppLogging.logError("FetchAppNotificationsTask: error code: " +
                        smartWishListAppNotificationData.getErrorCode());
                return;
            }

            StoreNotificationsTask task = new StoreNotificationsTask(getPreferences());
            task.execute(smartWishListAppNotificationData);

            AppNotification appNotification = new AppNotification(getApplicationContext());
            appNotification.show(smartWishListAppNotificationData);
        }
    }

    private class StoreNotificationsTask extends AsyncTask<SmartWishListAppNotificationData, Void, Void> {

        private final AppPreferences preferences;

        public StoreNotificationsTask(AppPreferences preferences) {
            this.preferences = preferences;
        }

        @Override
        protected Void doInBackground(SmartWishListAppNotificationData... smartWishListAppNotificationDatas) {
            AppStorage appStorage = new AppStorage(getApplicationContext());
            appStorage.deleteAllOldNotifications(preferences.getLastViewedNotifications());
            appStorage.insertNotifications(smartWishListAppNotificationDatas[0].getTriggers());
            return null;
        }
    }
}
