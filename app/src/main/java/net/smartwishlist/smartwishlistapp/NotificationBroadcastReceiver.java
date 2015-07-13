package net.smartwishlist.smartwishlistapp;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class NotificationBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(AppNotification.NOTIFICATION_CLICKED_ACTION)) {
            Intent detailIntent = new Intent(context, NotificationActivity.class);
            detailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(detailIntent);
        } else if (intent.getAction().equals(AppNotification.NOTIFICATION_BUY_ACTION)) {
            Intent buyIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(intent.getStringExtra(AppNotification.URL_EXTRA)));
            buyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(buyIntent);
            String productId = intent.getStringExtra(AppNotification.PRODUCT_ID_EXTRA);
            if (productId != null) {
                AppStorage.DeleteTriggerDataTask task =
                        new AppStorage.DeleteTriggerDataTask(context);
                task.execute(productId);
            }
        }
        AppPreferences preferences = new AppPreferences(context);
        preferences.setPendingMessages(0);
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(AppNotification.NOTIFICATION_ID);
        try {
            context.unregisterReceiver(this);
        } catch (IllegalArgumentException e) {
            // Ignore exception if broadcast receiver was not registered programmatically
        }
    }
}
