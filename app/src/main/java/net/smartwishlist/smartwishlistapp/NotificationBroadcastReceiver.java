package net.smartwishlist.smartwishlistapp;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class NotificationBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        AppPreferences preferences = new AppPreferences(context);
        preferences.setPendingMessages(0);
        try {
            context.unregisterReceiver(this);
        } catch (IllegalArgumentException e) {
            // Ignore exception if broadcast receiver was not registered programmatically
        }
        if (intent.getAction().equals(AppNotification.NOTIFICATION_CLICKED_ACTION)) {
            startNotificationActivity(context);
        } else if (intent.getAction().equals(AppNotification.NOTIFICATION_BUY_ACTION)) {
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(AppNotification.NOTIFICATION_ID);
            String url = intent.getStringExtra(AppNotification.URL_EXTRA);
            if (url != null) {
                Intent buyIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(url));
                buyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(buyIntent);
                String productId = intent.getStringExtra(AppNotification.PRODUCT_ID_EXTRA);
                if (productId != null) {
                    AppStorage.DeleteTriggerDataTask task =
                            new AppStorage.DeleteTriggerDataTask(context);
                    task.execute(productId);
                }
            } else {
                startNotificationActivity(context);
            }
        }
    }

    private static void startNotificationActivity(Context context) {
        Intent notificationIntent = new Intent(context, NotificationActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        notificationIntent.putExtra(NotificationActivity.REFRESH_EXTRA, true);
        context.startActivity(notificationIntent);
    }
}
