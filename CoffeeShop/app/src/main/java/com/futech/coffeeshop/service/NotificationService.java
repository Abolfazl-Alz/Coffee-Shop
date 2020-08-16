package com.futech.coffeeshop.service;

import android.app.ActivityManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.futech.coffeeshop.R;
import com.futech.coffeeshop.obj.notification.NotificationData;
import com.futech.coffeeshop.receiver.RestartBroadcastReceiver;
import com.futech.coffeeshop.ui.main_activity.MainActivity;
import com.futech.coffeeshop.utils.CoffeeShopNotification;
import com.futech.coffeeshop.utils.HttpHelper;
import com.futech.coffeeshop.utils.NotificationDatabase;
import com.futech.coffeeshop.utils.RegisterControl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class NotificationService extends Service {

    private Timer timer = new Timer();
    private CoffeeShopNotification coffeeNotify;
    private Intent intent;

    public void startSystem(Context context) {
        if (isMyServiceRunning(context)) return;
        intent = new Intent(context, NotificationService.class);
        context.startService(intent);
    }

    public void stopSystem(Context context) {
        if (intent != null && isMyServiceRunning(context)) context.stopService(intent);
    }

    private static boolean isMyServiceRunning(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (manager == null) return false;
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
            if (NotificationService.class.getName().equals(service.service.getClassName()))
                return true;
        return false;
    }

    public NotificationService() {
        super();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        sendBroadcast(rootIntent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        coffeeNotify = new CoffeeShopNotification(this.getApplicationContext());
        final int period = (int) Math.pow(10, 3);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (RegisterControl.isLogin(NotificationService.this)) {
                    userService();
                    if (RegisterControl.isAdmin(NotificationService.this)) {
                        adminService();
                    }
                }
            }
        }, 0, period);

        return START_STICKY;
    }

    private boolean adminRequestSent = false;

    private int lastCount = 0;

    private void adminService() {
        if (!adminRequestSent) {
            String apiAddress = getString(R.string.api_address) + "order.php";
            HttpHelper http = new HttpHelper(apiAddress);
            HashMap<String, String> params = new HashMap<>();
            params.put("token", getString(R.string.api_token));
            params.put("action", "select_unread_count");
            RegisterControl register = new RegisterControl(this);
            params.put("user_id", String.valueOf(register.getRegisterData().getId()));
            http.response(Request.Method.POST, params, new HttpHelper.HttpListener() {
                @Override
                public void onResponse(String response) {
                    try {
                        adminRequestSent = false;
                        try {
                            int count = Integer.parseInt(response);
                            if (count <= 0 || lastCount == count) {
                                if (count == 0) lastCount = count;
                                return;
                            }
                            String title = getString(R.string.new_order);
                            String text;
                            text = getResources().getQuantityString(R.plurals.unread_orders_count, count, count);

                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.putExtra("request", MainActivity.ADMIN_ORDER_REQUEST);
                            PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);

                            coffeeNotify.sendAdminOrderNotification(title, text, 1, contentIntent);

                            lastCount = count;
                        } catch (NumberFormatException ex) {
                            //nothing
                        }
                        adminRequestSent = true;
                    } catch (Exception ex) {
                        if (userRequestSent) userRequestSent = false;
                    }
                }

                @Override
                public void onError(VolleyError error) {
                    adminRequestSent = false;
                }
            });
        }

    }

    private boolean userRequestSent = false;
    private List<Integer> idList = new ArrayList<>();

    private void userService() {
        try {
            if (!userRequestSent) {
                userRequestSent = true;
                NotificationDatabase db = new NotificationDatabase(this);
                db.selectAll(new NotificationDatabase.SelectNotificationListener() {
                    @Override
                    public void onSelect(NotificationData[] notificationData) {
                        userRequestSent = false;
                        for (NotificationData data : notificationData) {
                            if (idList.contains(data.id)) return;
                            coffeeNotify.sendOrderStatusNotification(data.text, data.id);
                            idList.add(data.id);
                        }
                    }

                    @Override
                    public void onError(String error) {
                        userRequestSent = false;
                    }
                });
            }
        } catch (Exception ex) {
            if (userRequestSent) userRequestSent = false;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent broadcastIntent = new Intent(this, RestartBroadcastReceiver.class);
        sendBroadcast(broadcastIntent);
        stopTimer();
    }

    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
}
