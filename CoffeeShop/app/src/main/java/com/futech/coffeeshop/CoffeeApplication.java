package com.futech.coffeeshop;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.futech.coffeeshop.utils.CoffeeShopNotification;
import com.futech.coffeeshop.utils.Internet;
import com.futech.coffeeshop.utils.local_database.ImageLocalDatabase;

import java.util.Locale;

public class CoffeeApplication extends Application {

    @SuppressLint("StaticFieldLeak")
    private static Context context;
    private static RequestQueue requestQueue;

    public void onCreate() {
        super.onCreate();

        setAppLocale();

        CoffeeApplication.context = getApplicationContext();
        CoffeeApplication.requestQueue = Volley.newRequestQueue(getApplicationContext());
        CoffeeShopNotification.createNotificationChannels(getApplicationContext());

        if (Internet.isConnected(getAppContext())) {
            ImageLocalDatabase db = new ImageLocalDatabase(getAppContext());
            db.deleteUnusedImages(1);
        }
    }

    private void setAppLocale() {
        Resources resources = getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        config.setLocale(new Locale("fa".toLowerCase()));
        resources.updateConfiguration(config, dm);
    }

    public CoffeeApplication() {
        super();
    }

    public static Context getAppContext() {
        return CoffeeApplication.context;
    }

    public static RequestQueue getRequestQueue() {
        return requestQueue;
    }
}