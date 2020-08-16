package com.futech.coffeeshop.utils;

import android.content.Context;

import androidx.annotation.IntDef;

import com.android.volley.VolleyError;
import com.futech.coffeeshop.CoffeeApplication;
import com.futech.coffeeshop.R;
import com.futech.coffeeshop.obj.notification.NotificationApi;
import com.futech.coffeeshop.obj.notification.NotificationData;

import java.lang.annotation.Retention;

import static com.futech.coffeeshop.obj.notification.NotificationData.NotificationStatus.ORDER_ACCEPT_STATUS;
import static com.futech.coffeeshop.obj.notification.NotificationData.NotificationStatus.ORDER_ARRIVED_STATUS;
import static com.futech.coffeeshop.obj.notification.NotificationData.NotificationStatus.ORDER_CANCEL_STATUS;
import static com.futech.coffeeshop.obj.notification.NotificationData.NotificationStatus.ORDER_DEFAULT_STATUS;
import static com.futech.coffeeshop.obj.notification.NotificationData.NotificationStatus.ORDER_DELIVER_STATUS;
import static com.futech.coffeeshop.obj.notification.NotificationData.NotificationStatus.ORDER_READY_STATUS;
import static com.futech.coffeeshop.obj.notification.NotificationData.NotificationStatus.ORDER_SENT_STATUS;
import static java.lang.annotation.RetentionPolicy.SOURCE;

public class NotificationDatabase {

    private static String getApiAddress(Context context) {
        return context.getString(R.string.api_address) + "notification.php";
    }

    private static String API_ADDRESS;
    private final Context context;
    private final String TAG = "NotificationControl";

    public NotificationDatabase(Context context) {
        API_ADDRESS = getApiAddress(context);
        this.context = context;
    }

    public static NotificationDatabase newInstance() {
        return new NotificationDatabase(CoffeeApplication.getAppContext());
    }

    public void selectAll(final SelectNotificationListener listener) {
        ApiControl<NotificationApi> api = new ApiControl<>(context, API_ADDRESS, NotificationApi.class);
        api.addParameter("action", "select_all");
        api.addParameter("uid", String.valueOf(RegisterControl.getCurrentUserUid(context)));
        api.response(new ApiControl.ApiListener<NotificationApi>() {
            @Override
            public void onResponse(NotificationApi api) {
                NotificationData[] dest = new NotificationData[api.getData().getItems().length];
                System.arraycopy(api.getData().getItems(), 0, dest, 0, dest.length);
                listener.onSelect(dest);
            }

            @Override
            public void onError(VolleyError error) {
                listener.onError(ErrorTranslator.getErrorMessage(error));
            }
        });
    }

    @Retention(SOURCE)
    @IntDef({ORDER_DEFAULT_STATUS, ORDER_ACCEPT_STATUS, ORDER_ARRIVED_STATUS, ORDER_CANCEL_STATUS, ORDER_DELIVER_STATUS, ORDER_READY_STATUS, ORDER_SENT_STATUS})
    @interface StatusMode {}

    public void sendOrderStatusChanging(int toId, @StatusMode
            int status, int orderId, final NotificationListener listener) {
        ApiControl<NotificationApi> api = new ApiControl<>(context, API_ADDRESS, NotificationApi.class);
        api.addParameter("action", "add_order");
        api.addParameter("to_id", String.valueOf(toId));
        api.addParameter("status", String.valueOf(status));
        api.addParameter("order", String.valueOf(orderId));
        api.addParameter("uid", String.valueOf(RegisterControl.getCurrentUserUid(context)));
        api.response(new ApiControl.ApiListener<NotificationApi>() {
            @Override
            public void onResponse(NotificationApi api) {
                listener.onReceive();
            }

            @Override
            public void onError(VolleyError error) {
                listener.onError(ErrorTranslator.getErrorMessage(error));
            }
        });
    }

    public interface SelectNotificationListener {
        void onSelect(NotificationData[] notificationData);

        void onError(String error);
    }

    public interface NotificationListener {
        void onReceive();

        void onError(String error);
    }

}
