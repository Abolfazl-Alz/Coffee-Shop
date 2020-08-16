package com.futech.coffeeshop.utils.local_database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.android.volley.VolleyError;
import com.futech.coffeeshop.R;
import com.futech.coffeeshop.obj.cart.CartApi;
import com.futech.coffeeshop.obj.cart.CartData;
import com.futech.coffeeshop.utils.ApiControl;
import com.futech.coffeeshop.utils.DatabaseHelper;
import com.futech.coffeeshop.utils.ErrorTranslator;
import com.futech.coffeeshop.utils.RegisterControl;
import com.futech.coffeeshop.utils.listener.DataChangeListener;
import com.futech.coffeeshop.utils.listener.SelectListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartLocalDatabase extends DatabaseHelper<CartData> {


    private static final String DB_NAME = "cart_db";
    private static final int DB_VERSION = 7;
    private final Context context;
    private final String TAG = "CartDB";

    public CartLocalDatabase(Context context) {
        super(context, DB_NAME, DB_VERSION, CartData.TABLE_NAME_KEY);
        this.context = context;
    }

    @Override
    public List<CartData> convertCursorToList(Cursor cursor) {
        return CartData.convertListFromCursor(cursor);
    }

    public void insert(final CartData data, final DataChangeListener listener) {
        ApiControl<CartApi> api = new ApiControl<>(context, context.getString(R.string.api_address) + "cart.php", CartApi.class);
        HashMap<String, String> values = new HashMap<>();
        values.put("item_id", String.valueOf(data.getId()));
        values.put("user_id", String.valueOf(RegisterControl.getRegisterData(context).getId()));
        values.put("count", String.valueOf(data.getCount()));
        values.put("size", data.getSizes());
        values.put("action", "insert");

        api.addParameters(values);
        api.response(new ApiControl.ApiListener<CartApi>() {
            @Override
            public void onResponse(CartApi api) {
                Log.i(TAG, "onResponse: insert success: " + api);
                if (api.getData().getStatus().equals("error")) {
                    listener.onError(api.getData().getMsg());
                }else {
                    listener.onChange();

                    int id = 0;

                    if (api.getData().getItems().length == 1) {
                        id = api.getData().getItems()[0].getCartId();
                    }

                    data.setCartId(id);
                    CartLocalDatabase.super.insert(data);
                }
            }

            @Override
            public void onError(VolleyError error) {
                Log.i(TAG, "onError: Insert has some error: " + error);
                listener.onError(error.toString());
            }
        });
    }

    public void delete(final int id, final DataChangeListener listener) {
        ApiControl<CartApi> api = new ApiControl<>(context, context.getString(R.string.api_address) + "cart.php", CartApi.class);
        api.addParameter("user_id", String.valueOf(RegisterControl.getCurrentUserUid(context)));
        api.addParameter("action", "delete");
        api.addParameter("id", String.valueOf(id));
        api.response(new ApiControl.ApiListener<CartApi>() {
            @Override
            public void onResponse(CartApi api) {
                if (!api.getData().getStatus().equals("error")) {
                    CartLocalDatabase.super.delete(CartData.CART_ID_KEY + "=?", new String[]{String.valueOf(id)});
                    listener.onChange();
                }else {
                    listener.onError(api.getData().getMsg());
                }
            }

            @Override
            public void onError(VolleyError error) {
                listener.onError(String.valueOf(error));
            }
        });
    }

    public void deleteAll(final DataChangeListener listener) {
        ApiControl<CartApi> api = new ApiControl<>(context, context.getString(R.string.api_address) + "cart.php", CartApi.class);
        api.addParameter("user_id", String.valueOf(RegisterControl.getCurrentUserUid(context)));
        api.addParameter("action", "delete_all");
        api.response(new ApiControl.ApiListener<CartApi>() {
            @Override
            public void onResponse(CartApi api) {
                if (!api.getData().getStatus().equals("error")) {
                    CartLocalDatabase.super.delete("", null);
                    listener.onChange();
                }else {
                    listener.onError(api.getData().getMsg());
                }
            }

            @Override
            public void onError(VolleyError error) {
                listener.onError(String.valueOf(error));
            }
        });
    }

    public void update(final int id, final CartData newData, final DataChangeListener listener) {
        ApiControl<CartApi> api = new ApiControl<>(context, context.getString(R.string.api_address) + "cart.php", CartApi.class);
        HashMap<String, String> values = new HashMap<>();
        values.put("item_id", String.valueOf(newData.getId()));
        values.put("user_id", String.valueOf(RegisterControl.getCurrentUserUid(context)));
        values.put("count", String.valueOf(newData.getCount()));
        values.put("size", newData.getSizes());
        values.put("id", String.valueOf(id));
        values.put("action", "update");

        api.addParameters(values);
        api.response(new ApiControl.ApiListener<CartApi>() {
            @Override
            public void onResponse(CartApi api) {
                Log.i(TAG, "onResponse: update success: " + api);
                if (api.getData().getStatus().equals("error")) {
                    listener.onError(api.getData().getMsg());
                }else {
                    listener.onChange();
                    CartLocalDatabase.super.update(newData, CartData.CART_ID_KEY + "=?", new String[]{String.valueOf(id)});
                }
            }

            @Override
            public void onError(VolleyError error) {
                Log.i(TAG, "onError: Update has some error: " + error);
                listener.onError(error.toString());
            }
        });
    }

    public void select(final SelectListener<CartData[]> listener) {
        ApiControl<CartApi> api = new ApiControl<>(context, context.getString(R.string.api_address) + "cart.php", CartApi.class);
        api.addParameter("action", "select").addParameter("user_id", String.valueOf(RegisterControl.getCurrentUserUid(context)));
        select(api, listener);
    }

    public void selectFeeds(final SelectListener<CartData[]> listener) {
        ApiControl<CartApi> api = new ApiControl<>(context, context.getString(R.string.api_address) + "feed.php", CartApi.class);
        api.addParameter("action", "select_order");
        select(api, listener);
    }

    public void select(ApiControl<CartApi> api, final SelectListener<CartData[]> listener) {
        api.response(new ApiControl.ApiListener<CartApi>() {
            @Override
            public void onResponse(CartApi api) {
                if (!api.getData().getStatus().equals("error")) {
                    CartLocalDatabase.super.deleteAll();
                    for (CartApi.CartApiData item : api.getData().getItems()) {
                        item.setCartSync(1);
                        item.setId(item.getItemId());
                        CartLocalDatabase.super.insert(item);
                    }
                    listener.onSelect(api.getData().getItems(), true);
                }else {
                    listener.onError(api.getData().getMsg());
                }
            }

            @Override
            public void onError(VolleyError error) {
                listener.onError(ErrorTranslator.getErrorMessage(error));
            }
        });
    }

    public void updateStatus(final int id, final boolean status, final DataChangeListener listener) {
        ApiControl<CartApi> api = new ApiControl<>(context, context.getString(R.string.api_address) + "cart.php", CartApi.class);
        HashMap<String, String> values = new HashMap<>();
        values.put("status", String.valueOf(status ? 1 : 0));
        values.put("id", String.valueOf(id));
        values.put("user_id", String.valueOf(RegisterControl.getCurrentUserUid(getContext())));
        values.put("action", "update_status");

        api.addParameters(values);
        api.response(new ApiControl.ApiListener<CartApi>() {
            @Override
            public void onResponse(CartApi api) {
                if (api.getData().getStatus().equals("error")) {
                    listener.onError(api.getData().getMsg());
                }else {
                    Log.i(TAG, "onResponse: update success: " + api);
                    listener.onChange();

                    SQLiteDatabase db = getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put("status", status ? 1 : 0);
                    db.update(tableName, values, CartData.CART_ID_KEY + "=?", new String[]{String.valueOf(id)});
                    db.close();
                }
            }

            @Override
            public void onError(VolleyError error) {
                Log.i(TAG, "onError: Update has some error: " + error + "\n" + error.getMessage());
                listener.onError(error.toString());
            }
        });
    }

    @Override
    public ContentValues convertToContentValues(CartData value) {
        return value.convertToContent();
    }

    @Override
    protected String createTableQuery() {
        Map<String, String> columns = ItemLocalDatabase.needleColumns();
        columns.put(CartData.CART_DB_ID_KEY, QueryCreator.INTEGER_KEY);
        columns.put(CartData.CART_COUNT_KEY, QueryCreator.INTEGER_KEY);
        columns.put(CartData.CART_ID_KEY, QueryCreator.INTEGER_KEY);
        columns.put(CartData.ORDER_ID_KEY, QueryCreator.INTEGER_KEY);
        columns.put(CartData.CART_STATUS, QueryCreator.INTEGER_KEY);
        columns.put(CartData.CART_PRICE_KEY, "REAL");
        columns.put(CartData.CART_SYNC_KEY, QueryCreator.INTEGER_KEY);

        return QueryCreator.getCreateDatabaseQuery(CartData.TABLE_NAME_KEY, columns, CartData.CART_DB_ID_KEY);
    }

    @Override
    protected String deleteTableQuery() {
        return "DROP TABLE IF EXISTS " + CartData.TABLE_NAME_KEY;
    }
}
