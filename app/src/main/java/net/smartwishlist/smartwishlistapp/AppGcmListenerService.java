package net.smartwishlist.smartwishlistapp;

import android.os.AsyncTask;
import android.os.Bundle;

import com.appspot.smart_wish_list.smartwishlist.model.SmartWishListAppNotificationData;
import com.google.android.gms.gcm.GcmListenerService;

public class AppGcmListenerService extends GcmListenerService {

    @Override
    public void onMessageReceived(String from, Bundle data) {
        if (data != null && !data.isEmpty()) {
            String messageType = data.getString("type");
            if (messageType != null) {
                switch (messageType) {
                    case "price-alert":
                        AppPreferences preferences = new AppPreferences(this);
                        if (preferences.getNotificationEnabled()) {
                            String clientId = data.getString("client-id");
                            if (clientId != null && clientId.equals(preferences.getClientId())) {
                                FetchAppNotificationsTask task = new FetchAppNotificationsTask();
                                task.execute();
                            } else {
                                AppLogging.logError("Unexpected client ID. Received=" + clientId +
                                        " Expected=" + preferences.getClientId());
                            }
                        }
                        break;
                    default:
                        AppLogging.logError("Unexpected GCM message from: " + from +
                                " type: " + messageType);
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
        protected Void doInBackground(SmartWishListAppNotificationData... smartWishListAppNotificationData) {
            AppStorage appStorage = new AppStorage(getApplicationContext());
            appStorage.deleteAllOldNotifications(preferences.getLastViewedNotifications());
            appStorage.insertNotifications(smartWishListAppNotificationData[0].getTriggers());
            return null;
        }
    }
}
