package net.smartwishlist.smartwishlistapp;

import android.os.Bundle;

import com.google.android.gms.gcm.GcmListenerService;

public class AppGcmListenerService extends GcmListenerService {

    @Override
    public void onMessageReceived(String from, Bundle data) {
        if (data != null && !data.isEmpty()) {
            String messageType = data.getString("type");
            if (messageType != null) {
                switch (messageType) {
                    case "price-alert":
                        AppPreferences preferences = new AppPreferences(getApplicationContext());
                        if (preferences.getNotificationEnabled()) {
                            String clientId = data.getString("client-id");
                            if (clientId != null) {
                                if (clientId.equals(preferences.getClientId())) {
                                    AppLogging.logDebug("Unexpected client ID. Received=" + clientId +
                                            " Expected=" + preferences.getClientId());
                                }
                                ApiService.FetchAppNotificationsTask task =
                                        new ApiService.FetchAppNotificationsTask(this);
                                task.execute();
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
}
