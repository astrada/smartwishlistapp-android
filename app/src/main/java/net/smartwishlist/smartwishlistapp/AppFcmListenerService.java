package net.smartwishlist.smartwishlistapp;

import android.content.Context;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class AppFcmListenerService extends FirebaseMessagingService {

    @Override
    public void onCreate() {
        AppInitialization appInitialization = new AppInitialization(this);
        appInitialization.initializeApp();
        super.onCreate();
    }

    @Override
    public void onMessageReceived(RemoteMessage message) {
        try {
            Context context = getApplicationContext();
            AppPreferences preferences = new AppPreferences(context);
            if (preferences.getNotificationEnabled()) {
                ApiService.FetchAppNotificationsSyncTask task =
                        new ApiService.FetchAppNotificationsSyncTask(context);
                task.doSynchronized();
            }
        } catch (Exception e) {
            AppLogging.logError("onMessageReceived: " + message.toString());
            AppLogging.logException(e);
        }
    }
}
