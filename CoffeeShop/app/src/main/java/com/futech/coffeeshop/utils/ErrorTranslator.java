package com.futech.coffeeshop.utils;

import android.content.Context;

import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.futech.coffeeshop.CoffeeApplication;
import com.futech.coffeeshop.R;

public class ErrorTranslator {

    private static final int SERVER_TIMEOUT = 100;
    private static final int SERVER_ERROR = 101;
    private static final int NO_CONNECTION_ERROR = 102;
    private static final int PARSE_ERROR = 103;
    private static final int NETWORK_ERROR = 104;
    private static final int INVALID_ERROR = 105;

    public static String getErrorMessage(VolleyError error) {
        return getErrorMessage(CoffeeApplication.getAppContext(), error);
    }

    public static String getErrorMessage(Context context, VolleyError error) {
        final int errorCode = getErrorCode(error);
        return getErrorMessage(context, errorCode);
    }

    private static String getErrorMessage(Context context, int errorCode) {
        return context.getString(getErrorMessageResource(errorCode));
    }

    static int getErrorCode(VolleyError error) {
        if (error instanceof TimeoutError) {
            return SERVER_TIMEOUT;
        }else if (error instanceof ServerError) {
            return SERVER_ERROR;
        }else if (error instanceof NoConnectionError) {
            return NO_CONNECTION_ERROR;
        }else if (error instanceof ParseError) {
            return PARSE_ERROR;
        }else if (error instanceof NetworkError) {
            return NETWORK_ERROR;
        }else {
            return INVALID_ERROR;
        }
    }

    public static int getErrorCodeByMessage(String message) {
        return getErrorCodeByMessage(CoffeeApplication.getAppContext(), message);
    }

    private static int getErrorCodeByMessage(Context context, String message) {
        int[] codes = {SERVER_TIMEOUT, SERVER_ERROR, NO_CONNECTION_ERROR, PARSE_ERROR, NETWORK_ERROR, INVALID_ERROR};

        for (int code : codes) {
            if (getErrorMessage(context, code).equals(message)) {
                return code;
            }
        }
        return -1;
    }

    public static int getErrorMessageResource(int errorCode) {
        switch (errorCode) {
            case SERVER_TIMEOUT:
                return R.string.volley_error_timeout;
            case SERVER_ERROR:
                return R.string.volley_error_server;
            case NO_CONNECTION_ERROR:
                return R.string.volley_error_connection;
            case PARSE_ERROR:
                return R.string.volley_error_parse;
            case NETWORK_ERROR:
                return R.string.volley_error_network;
            default:
                return R.string.volley_error_invalid;
        }
    }
}
