package com.futech.coffeeshop.utils.local_database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import androidx.annotation.Nullable;

import com.android.volley.VolleyError;
import com.futech.coffeeshop.R;
import com.futech.coffeeshop.obj.order.OrderApi;
import com.futech.coffeeshop.obj.order.OrderData;
import com.futech.coffeeshop.utils.ApiControl;
import com.futech.coffeeshop.utils.DatabaseHelper;
import com.futech.coffeeshop.utils.ErrorTranslator;
import com.futech.coffeeshop.utils.RegisterControl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.futech.coffeeshop.obj.order.OrderData.ACCEPT_KEY;
import static com.futech.coffeeshop.obj.order.OrderData.ID_DB_KEY;
import static com.futech.coffeeshop.obj.order.OrderData.ID_KEY;
import static com.futech.coffeeshop.obj.order.OrderData.MESSAGE_KEY;
import static com.futech.coffeeshop.obj.order.OrderData.TEXT_STATUS_KEY;
import static com.futech.coffeeshop.obj.order.OrderData.UID_KEY;

public class OrderLocalDatabase extends DatabaseHelper<OrderData> {

    private static final String DB_NAME = "order_db";
    private static final int DB_VERSION = 2;
    private static final String TABLE_NAME = "order";
    private final String API_ADDRESS;
    private final String TAG = "OrderDb";

    public OrderLocalDatabase(@Nullable Context context) {
        super(context, DB_NAME, DB_VERSION, TABLE_NAME);
        API_ADDRESS = getContext().getString(R.string.api_address) + "order.php";
    }

    public void addOrder(String message, int addressId, String discountCode, final OrderListener listener) {
        ApiControl<OrderApi> api = getOrderApi();
        api.addParameter("msg", message);
        api.addParameter("user_id", String.valueOf(RegisterControl.getRegisterData(getContext()).getId()));
        api.addParameter("address", String.valueOf(addressId));
        api.addParameter("action", "add");
        api.addParameter("discount", discountCode);
        api.response(new ApiControl.ApiListener<OrderApi>() {
            @Override
            public void onResponse(OrderApi api) {
                if (api.getData().getStatus().equals("success")) listener.onListener();
                else listener.onError(api.getData().getMsg());
            }

            @Override
            public void onError(VolleyError error) {
                listener.onError(ErrorTranslator.getErrorMessage(error));
            }
        });
    }

    public void selectDeliveredItems(final SelectItemsListener listener) {
        Map<String, String> map = new HashMap<>();
        map.put("action", "select_incomplete");
        map.put("user_id", String.valueOf(RegisterControl.getRegisterData(getContext()).getId()));
        selectOrders(map, listener);
    }

    public void selectHistory(final SelectItemsListener listener) {
        Map<String, String> map = new HashMap<>();
        map.put("action", "select_history");
        map.put("user_id", String.valueOf(RegisterControl.getRegisterData(getContext()).getId()));
        selectOrders(map, listener);
    }

    private void selectOrders(Map<String, String> parameters, final SelectItemsListener listener) {
        ApiControl<OrderApi> api = getOrderApi();
        api.addParameters(parameters);
        api.response(new ApiControl.ApiListener<OrderApi>() {
            @Override
            public void onResponse(OrderApi api) {
                if (api.getData().getStatus().equals("error"))
                    listener.onError(api.getData().getMsg());
                else {
                    listener.onSelect(api.getData().getItems());
                }
            }

            @Override
            public void onError(VolleyError error) {
                listener.onError(ErrorTranslator.getErrorMessage(error));
            }
        });
    }

    private ApiControl<OrderApi> getOrderApi() {
        return new ApiControl<>(getContext(), API_ADDRESS, OrderApi.class);
    }

    public void markAsView(int id) {
        ApiControl<OrderApi> api = getOrderApi();
        api.addParameter("action", "read");
        api.addParameter("user_id", String.valueOf(RegisterControl.getRegisterData(getContext()).getId()));
        api.addParameter("id", String.valueOf(id));
        api.response(new ApiControl.ApiListener<OrderApi>() {
            @Override
            public void onResponse(OrderApi api) {
                if (api.getData().getStatus().equals("error"))
                    Log.i(TAG, "onResponse: " + api.getData().getMsg());
            }

            @Override
            public void onError(VolleyError error) {
                Log.i(TAG, "onError: " + ErrorTranslator.getErrorMessage(error));
            }
        });
    }

    @Override
    protected List<OrderData> convertCursorToList(Cursor cursor) {
        List<OrderData> orderList = new ArrayList<>();
        while (cursor.moveToNext()) {
            OrderData order = new OrderData();
            order.setId(cursor.getInt(cursor.getColumnIndexOrThrow(ID_KEY)));
            order.setDbId(cursor.getInt(cursor.getColumnIndexOrThrow(OrderData.ID_DB_KEY)));
            order.setMessage(cursor.getString(cursor.getColumnIndexOrThrow(OrderData.MESSAGE_KEY)));
            order.setUid(cursor.getInt(cursor.getColumnIndexOrThrow(OrderData.UID_KEY)));
            orderList.add(order);
        }

        return orderList;
    }

    @Override
    protected ContentValues convertToContentValues(OrderData value) {
        return value.convertToContentValues();
    }

    @Override
    protected String createTableQuery() {
        return "CREATE TABLE " + TABLE_NAME + " (" + ID_DB_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT, " + ID_KEY + " INTEGER, " + ACCEPT_KEY + "INTEGER, " + MESSAGE_KEY + " TEXT, " + TEXT_STATUS_KEY + " TEXT, " + UID_KEY + " INTEGER " + ")";
    }

    public interface OrderListener {
        void onListener();

        void onError(String error);
    }

    public interface SelectItemsListener {
        void onSelect(OrderData[] orders);

        void onError(String msg);
    }
}
