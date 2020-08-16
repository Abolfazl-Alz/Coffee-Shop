package com.futech.coffeeshop.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.VolleyError;
import com.futech.coffeeshop.R;
import com.futech.coffeeshop.obj.register.RegisterApi;
import com.futech.coffeeshop.obj.register.RegisterData;

import java.util.Map;

public class RegisterControl {

    private final Context context;
    private final String API_ADDRESS;
    private SharedPreferences pref;
    private static final String PREF_NAME = "register";

    private static String getApiAddress(Context context) {
        return context.getString(R.string.api_address) + "register.php";
    }

    public  RegisterControl(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        API_ADDRESS = getApiAddress(context);
    }

    public void login(String phoneNumber, String password, final RegisterRequestResultListener listener) {
        ApiControl<RegisterApi> api = new ApiControl<>(context, API_ADDRESS, RegisterApi.class);
        api.addParameter("action", "sign_in");
        api.addParameter("phone_number", phoneNumber);
        api.addParameter("password", password);

        api.response(new ApiControl.ApiListener<RegisterApi>() {
            @Override
            public void onResponse(RegisterApi api) {
                if (api.getData().getStatus().equals("success")) {
                    listener.success();
                    setRegister(api.getData().getUser());
                }else listener.error(api.getData().getMsg(), api.getData().getCode());
            }

            @Override
            public void onError(VolleyError error) {
                listener.error(ErrorTranslator.getErrorMessage(error), ErrorTranslator.getErrorCode(error));
            }
        });
    }

    public void createAccount(String phoneNumber, String password, final RegisterRequestResultListener listener) {
        ApiControl<RegisterApi> api = new ApiControl<>(context, API_ADDRESS, RegisterApi.class);
        api.addParameter("action", "sign_up");
        api.addParameter("phone_number", phoneNumber);
        api.addParameter("password", password);

        api.response(new ApiControl.ApiListener<RegisterApi>() {
            @Override
            public void onResponse(RegisterApi api) {
                if (api.getData().getStatus().equals("success")) listener.success();
                else listener.error(api.getData().getMsg(), api.getData().getCode());
            }

            @Override
            public void onError(VolleyError error) {
                listener.error(ErrorTranslator.getErrorMessage(error), ErrorTranslator.getErrorCode(error));
            }
        });
    }

    private void setRegister(RegisterData data) {
        setRegister(pref, data);
    }

    public void logout() {
        pref.edit().clear().apply();
    }

    public boolean isAdmin() {
        return isAdmin(pref);
    }

    public RegisterData getRegisterData() {
        return getRegisterData(context);
    }

    public void updateInformation(Map<String, String> information, final RegisterRequestResultListener listener) {
        ApiControl<RegisterApi> api = new ApiControl<>(context, API_ADDRESS, RegisterApi.class);
        information.put("action", "update_information");
        api.addParameters(information);

        api.response(new ApiControl.ApiListener<RegisterApi>() {
            @Override
            public void onResponse(RegisterApi api) {
                if (api.getData().getStatus().equals("success")) {
                    listener.success();
                    setRegister(api.getData().getUser());
                }else listener.error(api.getData().getStatus(), api.getData().getCode());
            }

            @Override
            public void onError(VolleyError error) {
                listener.error(ErrorTranslator.getErrorMessage(error), ErrorTranslator.getErrorCode(error));
            }
        });
    }

    private static RegisterData getRegisterData(SharedPreferences pref) {
        RegisterData data = new RegisterData();
        data.setId(pref.getInt(RegisterData.ID_KEY, -1));
        data.setEmailAddress(pref.getString(RegisterData.EMAIL_KEY, ""));
        data.setLanguage(pref.getString(RegisterData.LANGUAGE_KEY, "En"));
        data.setPhoneNumber(pref.getString(RegisterData.PHONE_NUMBER_KEY, ""));
        data.setLastname(pref.getString(RegisterData.LAST_NAME_KEY, ""));
        data.setFirstname(pref.getString(RegisterData.FIRST_NAME_KEY, ""));
        data.setAdmin(pref.getBoolean(RegisterData.ADMIN_KEY, false));
        return data;
    }

    public static void UpdateData(final Context context, final RegisterRequestResultListener listener) {
        ApiControl<RegisterApi> api = new ApiControl<>(context, getApiAddress(context), RegisterApi.class);
        api.addParameter("action", "select");
        api.addParameter("uid", String.valueOf(getRegisterData(context).getId()));
        api.response(new ApiControl.ApiListener<RegisterApi>() {
            @Override
            public void onResponse(RegisterApi api) {
                if (api.getData().getStatus().equals("success")) {
                    setRegister(context, api.getData().getUser());
                    listener.success();
                    return;
                }
                listener.error(api.getData().getMsg(), api.getData().getCode());
            }

            @Override
            public void onError(VolleyError error) {
                listener.error(ErrorTranslator.getErrorMessage(error), ErrorTranslator.getErrorCode(error));
            }
        });
    }

    public static RegisterData getRegisterData(Context context) {
        return getRegisterData(context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE));
    }

    private static void setRegister(SharedPreferences pref, RegisterData data) {
        final SharedPreferences.Editor edit = pref.edit();
        edit.putInt(RegisterData.ID_KEY, data.getId());
        edit.putBoolean(RegisterData.ADMIN_KEY, data.isAdmin());
        edit.putString(RegisterData.EMAIL_KEY, data.getEmailAddress());
        edit.putString(RegisterData.FIRST_NAME_KEY, data.getFirstname());
        edit.putString(RegisterData.LAST_NAME_KEY, data.getLastname());
        edit.putString(RegisterData.LANGUAGE_KEY, data.getLanguage());
        edit.putString(RegisterData.PHONE_NUMBER_KEY, data.getPhoneNumber());
        edit.apply();
    }

    private static void setRegister(Context context, RegisterData data) {
        setRegister(context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE), data);
    }

    private static boolean isAdmin(SharedPreferences pref) {
        if (pref.contains(RegisterData.ADMIN_KEY)) {
            return pref.getBoolean(RegisterData.ADMIN_KEY, false);
        }
        return false;
    }

    public static boolean isAdmin(Context context) {
        return isAdmin(context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE));
    }

    private static boolean isLogin(SharedPreferences pref) {
        return pref.getInt(RegisterData.ID_KEY, -1) > 0;
    }

    public static boolean isLogin(Context context) {
        return isLogin(context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE));
    }

    public static int getCurrentUserUid(Context context) {
        return getRegisterData(context).getId();
    }

    public interface RegisterRequestResultListener {
        void success();

        void error(String error, int errorCode);
    }
}
