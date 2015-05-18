package net.smartwishlist.smartwishlistapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.appspot.smart_wish_list.smartwishlist.model.SmartWishListAppNotificationData;
import com.appspot.smart_wish_list.smartwishlist.model.SmartWishListItemData;
import com.appspot.smart_wish_list.smartwishlist.model.SmartWishListNotificationTriggerData;

import java.util.List;

public class AppNotification {

    private static final int NOTIFICATION_ID = 1;
    private static final int MAX_MESSAGES = 4;

    private static final String NOTIFICATION_CLICKED_ACTION = "NOTIFICATION_CLICKED";
    private static final String NOTIFICATION_DELETED_ACTION = "NOTIFICATION_DELETED";

    private final Context context;
    private final NotificationManager notificationManager;
    private final Resources resources;
    private final AppPreferences preferences;

    public AppNotification(Context context) {
        this.context = context;
        notificationManager =
                (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        resources = context.getResources();
        preferences = new AppPreferences(context);
    }

    public void show(SmartWishListAppNotificationData smartWishListAppNotificationData) {
        NotificationCompat.Builder builder = createBuilder(smartWishListAppNotificationData);
        if (builder != null) {
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }
    }

    @Nullable
    private NotificationCompat.Builder createBuilder(SmartWishListAppNotificationData smartWishListAppNotificationData) {
        List<SmartWishListNotificationTriggerData> triggers =
                smartWishListAppNotificationData.getTriggers();
        if (triggers.size() == 0) {
            return null;
        }
        int messageCount = preferences.getPendingMessages() + triggers.size();
        String firstMessage = createMessage(triggers.get(0));
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_stat_notify)
                .setLargeIcon(BitmapFactory.decodeResource(resources,
                        R.mipmap.ic_launcher))
                .setColor(AppConstants.LOGO_COLOR)
                .setContentIntent(getContentIntent())
                .setDeleteIntent(getDeleteIntent());
        if (messageCount == 1) {
            builder.setContentTitle("Price alert")
                    .setContentText(firstMessage);
        } else {
            builder.setNumber(messageCount)
                    .setContentTitle(String.format("%d price alerts", messageCount))
                    .setContentText(firstMessage + ",...");
            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
            inboxStyle.setBigContentTitle("Price alerts:");
            inboxStyle.addLine(firstMessage);
            int last = Math.min(triggers.size(), MAX_MESSAGES);
            for (int i = 1; i < last; i++) {
                inboxStyle.addLine(createMessage(triggers.get(i)));
            }
            if (messageCount > last) {
                inboxStyle.setSummaryText(String.format("+%d more",
                        messageCount - last));
            }
            builder.setStyle(inboxStyle);
        }
        preferences.setPendingMessages(messageCount);
        return builder;
    }

    private PendingIntent getContentIntent() {
        Intent intent = new Intent(NOTIFICATION_CLICKED_ACTION);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        BroadcastReceiver broadcastReceiver = new NotificationBroadcastReceiver();
        context.registerReceiver(broadcastReceiver, new IntentFilter(NOTIFICATION_CLICKED_ACTION));
        return pendingIntent;
    }

    private PendingIntent getDeleteIntent() {
        Intent intent = new Intent(NOTIFICATION_DELETED_ACTION);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        BroadcastReceiver broadcastReceiver = new NotificationBroadcastReceiver();
        context.registerReceiver(broadcastReceiver, new IntentFilter(NOTIFICATION_DELETED_ACTION));
        return pendingIntent;
    }

    @Nullable
    private static String createMessage(SmartWishListNotificationTriggerData data) {
        SmartWishListItemData item = data.getItem();
        if (item != null) {
            return String.format("%s - %s",
                    item.getFormattedPrice(),
                    item.getTitle());
        } else {
            return null;
        }
    }

    private class NotificationBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(NOTIFICATION_CLICKED_ACTION)) {
                Intent resultIntent = new Intent(context, NotificationActivity.class);
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                stackBuilder.addParentStack(NotificationActivity.class);
                stackBuilder.addNextIntent(resultIntent);
                stackBuilder.startActivities();
            }
            preferences.setPendingMessages(0);
            notificationManager.cancel(NOTIFICATION_ID);
            context.unregisterReceiver(this);
        }
    }
}
