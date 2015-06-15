package net.smartwishlist.smartwishlistapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.appspot.smart_wish_list.smartwishlist.model.SmartWishListAppNotificationData;
import com.appspot.smart_wish_list.smartwishlist.model.SmartWishListItemData;
import com.appspot.smart_wish_list.smartwishlist.model.SmartWishListNotificationTriggerData;

import java.util.List;

public class AppNotification {

    private static final int NOTIFICATION_ID = 1;
    private static final int MAX_MESSAGES = 4;

    private static final String NOTIFICATION_CLICKED_ACTION = "NOTIFICATION_CLICKED";
    private static final String NOTIFICATION_DELETED_ACTION = "NOTIFICATION_DELETED";
    private static final String NOTIFICATION_BUY_ACTION = "NOTIFICATION_BUY";
    private static final String URL_EXTRA = "url";

    private final Context context;
    private final NotificationManager notificationManager;
    private final AppPreferences preferences;

    public AppNotification(Context context) {
        this.context = context;
        notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        preferences = new AppPreferences(context);
    }

    public void show(SmartWishListAppNotificationData smartWishListAppNotificationData) {
        NotificationCompat.Builder builder = createBuilder(smartWishListAppNotificationData);
        if (builder != null) {
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }
    }

    @Nullable
    private NotificationCompat.Builder createBuilder(
            SmartWishListAppNotificationData smartWishListAppNotificationData) {
        List<SmartWishListNotificationTriggerData> triggers =
                smartWishListAppNotificationData.getTriggers();
        if (triggers == null || triggers.size() == 0) {
            return null;
        }
        Resources resources = context.getResources();
        int messageCount = preferences.getPendingMessages() + triggers.size();
        String firstMessage = createMessage(triggers.get(0));
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_stat_notify)
                .setLargeIcon(BitmapFactory.decodeResource(resources,
                        R.mipmap.ic_launcher))
                .setColor(AppConstants.LOGO_COLOR)
                .setContentIntent(getContentIntent())
                .setDeleteIntent(getDeleteIntent())
                .setAutoCancel(true);
        if (messageCount == 1) {
            builder.setContentTitle(resources.getString(R.string.price_alert))
                    .setContentText(firstMessage);
            if (triggers.size() == 1) {
                SmartWishListNotificationTriggerData data = triggers.get(0);
                if (data != null) {
                    String url = data.getItem().getProductUrl();
                    if (url != null) {
                        builder.addAction(R.drawable.ic_add_shopping_cart_black_24dp,
                                resources.getString(R.string.buy), getBuyIntent(url));
                    }
                }
            }
        } else {
            builder.setNumber(messageCount)
                    .setContentTitle(resources.getQuantityString(R.plurals.price_alerts,
                            messageCount,
                            messageCount))
                    .setContentText(firstMessage + ",...");
            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
            inboxStyle.setBigContentTitle(resources.getString(R.string.price_alerts_title));
            inboxStyle.addLine(firstMessage);
            int last = Math.min(triggers.size(), MAX_MESSAGES);
            for (int i = 1; i < last; i++) {
                inboxStyle.addLine(createMessage(triggers.get(i)));
            }
            if (messageCount > last) {
                int more_alerts = messageCount - last;
                inboxStyle.setSummaryText(resources.getQuantityString(R.plurals.more_alerts,
                        more_alerts,
                        more_alerts));
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

    private PendingIntent getBuyIntent(String url) {
        Intent intent = new Intent(NOTIFICATION_BUY_ACTION);
        intent.putExtra(URL_EXTRA, url);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        BroadcastReceiver broadcastReceiver = new NotificationBroadcastReceiver();
        context.registerReceiver(broadcastReceiver, new IntentFilter(NOTIFICATION_BUY_ACTION));
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
                Intent detailIntent = new Intent(context, NotificationActivity.class);
                detailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(detailIntent);
            } else if (intent.getAction().equals(NOTIFICATION_BUY_ACTION)) {
                Intent buyIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(intent.getStringExtra(URL_EXTRA)));
                buyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(buyIntent);
            }
            preferences.setPendingMessages(0);
            notificationManager.cancel(NOTIFICATION_ID);
            context.unregisterReceiver(this);
        }
    }
}
