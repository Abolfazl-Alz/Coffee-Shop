package com.futech.coffeeshop.utils;

import android.content.Context;
import android.util.Log;

import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.futech.coffeeshop.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class ApiControl<T> {

    private final Map<String, String> postParams;
    private final Map<String, String> getParams;
    private final HttpHelper httpHelper;
    private Type type;
    private final String TAG = "ApiControl";
    private Context context;

    public ApiControl(Context context, String address, Type type) {
        this.type = type;
        httpHelper = new HttpHelper(address);
        this.context = context;
        postParams = new HashMap<>();
        getParams = new HashMap<>();
        addParameter("token", context.getString(R.string.api_token));
        addParameter("uid", String.valueOf(RegisterControl.getCurrentUserUid(context)));
    }

    public ApiControl<T> addParameter(String key, String value) {
        this.postParams.put(key, value);
        return this;
    }

    public ApiControl<T> addParameter(int method, String key, String value) {
        if (method == Request.Method.GET) this.getParams.put(key, value);
        else addParameter(key, value);
        return this;
    }

    public void addParameters(Map<String, String> params) {
        addParameters(Request.Method.POST, params);
    }

    public ApiControl<T> addParameters(int method, Map<String, String> params) {
        if (method == Request.Method.POST) this.postParams.putAll(params);
        if (method == Request.Method.GET) this.getParams.putAll(params);
        return this;
    }

    public void response(final ApiListener<T> listener) {
        httpHelper.response(Request.Method.POST, this.postParams, this.getParams, new HttpHelper.HttpListener() {
            @Override
            public void onResponse(String response) {
                try {
                    Gson gson = new GsonBuilder().setDateFormat(context.getString(R.string.date_db_format)).create();
                    T t = gson.fromJson(response, type);
                    listener.onResponse(t);
                } catch (JsonSyntaxException ex) {
                    Log.d(TAG, "onResponse: " + response);
                    Log.d(TAG, "onResponse: JsonIOException: " + ex.getMessage());
                    Log.d(TAG, "onResponse: can't parsing json, because syntax of json response is incorrect");
                } catch (JsonParseException ex) {
                    Log.d(TAG, "onResponse: " + response);
                    Log.d(TAG, "onResponse: onError: " + ex.getMessage());
                    listener.onError(new ParseError());
                }
            }

            @Override
            public void onError(VolleyError error) {
                listener.onError(error);
            }
        });
    }

    public interface ApiListener<T> {
        void onResponse(T api);

        void onError(VolleyError error);
    }

}
