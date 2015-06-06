package net.smartwishlist.smartwishlistapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.appspot.smart_wish_list.smartwishlist.model.SmartWishListAppNotificationData;
import com.google.android.gms.gcm.GcmListenerService;

public class DataPullService extends GcmListenerService {

    private static final String TAG = "DataPullService";

    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Message: " + message);

        FetchAppNotificationsTask task = new FetchAppNotificationsTask();
        task.execute();
    }

    private class FetchAppNotificationsTask
            extends ApiService.ListAppNotificationsTask {

        public FetchAppNotificationsTask() {
            super(DataPullService.this);
        }

        @Override
        protected void onPostExecute(SmartWishListAppNotificationData smartWishListAppNotificationData) {
            if (smartWishListAppNotificationData == null
                    || smartWishListAppNotificationData.getErrorCode() != AppConstants.NO_ERRORS) {
                Log.d(AppConstants.LOG_TAG, "Error!");
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
