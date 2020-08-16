package com.futech.coffeeshop.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.futech.coffeeshop.CoffeeApplication;
import com.futech.coffeeshop.utils.local_database.ImageLocalDatabase;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

public class HttpHelper {

    private RequestQueue requestQueue;
    private String address;
    private static final String TAG = "HttpHelperLog";

    public HttpHelper(String address) {
        requestQueue = CoffeeApplication.getRequestQueue();
        this.address = address;
    }

    public void response(final Map<String, String> params, final HttpListener apiListener) {
        response(Request.Method.GET, params, apiListener);
    }

    public void response(int method, final Map<String, String> postParams, final Map<String, String> getParams, final HttpListener apiListener) {
        try {
            address += HttpHelper.paramsMapToString(getParams);
            StringRequest request = new StringRequest(method, address, apiListener::onResponse, error -> {
                if (error instanceof NoConnectionError)
                    response(method, postParams, getParams, apiListener);
                else apiListener.onError(error);
            })
            {
                @Override
                protected Map<String, String> getParams() {
                    return postParams;
                }
            };
            requestQueue.add(request);
            requestQueue.start();
        } catch (OutOfMemoryError memoryError) {
            Log.d(TAG, "response: no Enough memory ram, fix it, it's not very good:\nError Message: " + memoryError.getMessage() + "\nLanguage Error" + memoryError.getLocalizedMessage());
        }
    }

    public void response(int method, final Map<String, String> params, final HttpListener apiListener) {

        try {
            if (method == Request.Method.GET) address += paramsMapToString(params);

            StringRequest request = new StringRequest(method, address, apiListener::onResponse, error -> {
                if (error instanceof NoConnectionError) {
                    response(method, params, apiListener);
                }else {
                    apiListener.onError(error);
                }
            })
            {
                @Override
                protected Map<String, String> getParams() {
                    return params;
                }
            };
            requestQueue.add(request);
            requestQueue.start();
        } catch (OutOfMemoryError memoryError) {
            Log.d(TAG, "response: no Enough memory ram, fix it, it's not very good:\nError Message: " + memoryError.getMessage() + "\nLanguage Error" + memoryError.getLocalizedMessage());
        }
    }

    public static void loadImage(Context context, String url, final ImageView imageView, Bitmap defaultImage, int width, int height, String name) {
        ImageLocalDatabase db = new ImageLocalDatabase(context);
        db.loadImage(CoffeeApplication.getRequestQueue(), url, imageView, defaultImage, width, height, name, null);
    }

    public static void loadImage(Context context, String url, final ImageView imageView, Bitmap defaultImage, int width, int height, String name, HttpImageDownloaderListener listener) {
        ImageLocalDatabase db = new ImageLocalDatabase(context);
        db.loadImage(Volley.
                newRequestQueue(context), url, imageView, defaultImage, width, height, name, listener);
    }


    private static String paramsMapToString(Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        try {
            for (Map.Entry<String, String> param : params.entrySet()) {
                if (!sb.toString().equals("")) {
                    sb.append('&');

                }else {
                    sb.append('?');
                }
                sb.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                sb.append('=');
                sb.append(URLEncoder.encode(param.getValue(), "UTF-8"));
            }
        } catch (UnsupportedEncodingException ignored) {
        }
        return String.valueOf(sb);
    }

    public interface HttpImageDownloaderListener {
        void onDownload(boolean isOnline);

        void onError(String errorMessage);
    }

    public interface HttpListener {
        void onResponse(String response);

        void onError(VolleyError error);
    }
}
