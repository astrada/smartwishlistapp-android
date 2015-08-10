package net.smartwishlist.smartwishlistapp;

import android.app.Notification;
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

import com.appspot.smart_wish_list.smartwishlist.model.SmartWishListAppNotificationData;
import com.appspot.smart_wish_list.smartwishlist.model.SmartWishListItemData;
import com.appspot.smart_wish_list.smartwishlist.model.SmartWishListNotificationTriggerData;

import java.util.List;

public class AppNotification {

    public static final int NOTIFICATION_ID = 1;
    private static final int MAX_MESSAGES = 4;

    public static final String NOTIFICATION_CLICKED_ACTION = "NOTIFICATION_CLICKED";
    public static final String NOTIFICATION_DELETED_ACTION = "NOTIFICATION_DELETED";
    public static final String NOTIFICATION_BUY_ACTION = "NOTIFICATION_BUY";
    public static final String URL_EXTRA = "url";
    public static final String PRODUCT_ID_EXTRA = "productId";

    private final Context context;

    public AppNotification(Context context) {
        this.context = context;
    }

    public void show(SmartWishListAppNotificationData smartWishListAppNotificationData) {
        NotificationCompat.Builder builder = createBuilder(smartWishListAppNotificationData);
        if (builder != null) {
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
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
        AppPreferences preferences = new AppPreferences(context);
        int messageCount = preferences.getPendingMessages() + triggers.size();
        String firstMessage = createMessage(triggers.get(0));
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_stat_notify)
                .setLargeIcon(BitmapFactory.decodeResource(resources,
                        R.mipmap.ic_launcher))
                .setColor(AppConstants.LOGO_COLOR)
                .setContentIntent(getContentIntent())
                .setDeleteIntent(getDeleteIntent())
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true);
        if (messageCount == 1) {
            builder.setContentTitle(resources.getString(R.string.price_alert))
                    .setContentText(firstMessage);
            if (triggers.size() == 1) {
                SmartWishListNotificationTriggerData data = triggers.get(0);
                if (data != null) {
                    String url = data.getItem().getProductUrl();
                    String productId = data.getItem().getRegion() +
                            data.getItem().getAsin();
                    if (url != null) {
                        builder.addAction(R.drawable.ic_add_shopping_cart_black_24dp,
                                resources.getString(R.string.buy), getBuyIntent(url, productId));
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
                PendingIntent.FLAG_UPDATE_CURRENT);
        BroadcastReceiver broadcastReceiver = new NotificationBroadcastReceiver();
        context.registerReceiver(broadcastReceiver, new IntentFilter(NOTIFICATION_DELETED_ACTION));
        return pendingIntent;
    }

    private PendingIntent getBuyIntent(String url, String productId) {
        Intent intent = new Intent(NOTIFICATION_BUY_ACTION);
        intent.putExtra(URL_EXTRA, url);
        intent.putExtra(PRODUCT_ID_EXTRA, productId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
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
}
