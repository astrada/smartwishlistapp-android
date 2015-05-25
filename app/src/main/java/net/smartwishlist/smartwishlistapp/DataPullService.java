package net.smartwishlist.smartwishlistapp;

import android.app.IntentService;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.appspot.smart_wish_list.smartwishlist.Smartwishlist;
import com.appspot.smart_wish_list.smartwishlist.model.SmartWishListAppNotificationData;
import com.appspot.smart_wish_list.smartwishlist.model.SmartWishListAppNotificationParameters;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.util.Locale;

public class DataPullService extends IntentService {

    public DataPullService() {
        super("DataPullService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            Bundle extras = intent.getExtras();
            GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
            String messageType = gcm.getMessageType(intent);

            // message type=gcm
            // data=Bundle[{
            //   from=380143756816,
            //   type=price-alert,
            //   android.support.content.wakelockid=1,
            //   collapse_key=do_not_collapse}]
            if (messageType != null && extras != null) {
                if (!extras.isEmpty()) {
                    Log.d(AppConstants.LOG_TAG, String.format("Received GCM notification: " +
                            "message type=%s, " +
                            "data=%s", messageType, extras.toString()));
                }
                switch (messageType) {
                    case GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR:
                        // TODO
                        Log.d(AppConstants.LOG_TAG, "Error!");
                        break;
                    case GoogleCloudMessaging.MESSAGE_TYPE_DELETED:
                        // TODO
                        Log.d(AppConstants.LOG_TAG, "Deleted messages on server!");
                        break;
                    case GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE:
                        poll(intent);
                        break;
                }
            } else {
                poll(intent);
            }
        }
    }

    private void poll(Intent intent) {
        AppPreferences preferences = new AppPreferences(this);
        FetchAppNotificationsTask task = new FetchAppNotificationsTask(preferences,
                intent);
        double lastPoll = preferences.getLastServerPoll();
        task.execute(lastPoll);
    }

    private class FetchAppNotificationsTask extends AsyncTask<Double, Void, SmartWishListAppNotificationData> {

        private final AppPreferences preferences;
        private final Intent intent;
        private final Smartwishlist service;
        private double timestamp;

        public FetchAppNotificationsTask(AppPreferences preferences, Intent intent) {
            this.preferences = preferences;
            this.intent = intent;
            this.service = AppConstants.getApiServiceHandle(DataPullService.this);
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
                SmartWishListAppNotificationParameters parameters =
                        new SmartWishListAppNotificationParameters();
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

            GcmBroadcastReceiver.completeWakefulIntent(intent);
        }
    }
}
