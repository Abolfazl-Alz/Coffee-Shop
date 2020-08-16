package com.futech.coffeeshop.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.futech.coffeeshop.R;

public class CoffeeShopNotification {

    private static final String CHANNEL_ADMIN_ORDER_ID = "adminOrderId";
    private static final String CHANNEL_USER_ORDER_ID = "userOrderId";
    private static final String CHANNEL_MESSAGE_ID = "messageId";
    private NotificationManagerCompat notificationManager;
    private final Context context;

    public CoffeeShopNotification(Context context) {
        this.notificationManager = NotificationManagerCompat.from(context);
        this.context = context;
    }

    public static void createNotificationChannels(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel adminOrder = new NotificationChannel(CHANNEL_ADMIN_ORDER_ID, "Admin Order", NotificationManager.IMPORTANCE_HIGH);
            adminOrder.setDescription("Notification to admin for new order from a user");

            NotificationChannel userOrder = new NotificationChannel(CHANNEL_USER_ORDER_ID, "User Order", NotificationManager.IMPORTANCE_DEFAULT);

            NotificationChannel messageNotification = new NotificationChannel(CHANNEL_MESSAGE_ID, "Messaging", NotificationManager.IMPORTANCE_DEFAULT);
            userOrder.setDescription("make a notification when message");

            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager == null) return;
            manager.createNotificationChannel(adminOrder);
            manager.createNotificationChannel(userOrder);
            manager.createNotificationChannel(messageNotification);
        }
    }

    public void sendAdminOrderNotification(String title, String text, int id, PendingIntent contentIntent) {
        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ADMIN_ORDER_ID)
                .setSmallIcon(R.drawable.ic_cart)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setContentIntent(contentIntent)
                .build();

        notificationManager.notify(id, notification);
    }

    public void sendOrderStatusNotification(String orderStatus, int id) {
        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ADMIN_ORDER_ID)
                .setSmallIcon(R.drawable.ic_cart)
                .setContentTitle(context.getString(R.string.order_status_changed))
                .setContentText(orderStatus)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();

        notificationManager.notify(id, notification);
    }

}

