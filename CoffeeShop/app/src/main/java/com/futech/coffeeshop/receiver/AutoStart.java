package com.futech.coffeeshop.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.futech.coffeeshop.service.NotificationService;

public class AutoStart extends BroadcastReceiver {

    public static final String TAG = "AutoStartLog";

    @Override
    public void onReceive(Context context, Intent args) {
        context.stopService(new Intent(context, NotificationService.class));
    }
}
