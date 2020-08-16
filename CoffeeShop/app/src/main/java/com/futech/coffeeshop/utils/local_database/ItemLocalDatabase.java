package com.futech.coffeeshop.utils.local_database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.futech.coffeeshop.R;
import com.futech.coffeeshop.obj.item.ItemApi;
import com.futech.coffeeshop.obj.item.ItemData;
import com.futech.coffeeshop.utils.ApiControl;
import com.futech.coffeeshop.utils.DatabaseHelper;
import com.futech.coffeeshop.utils.ErrorTranslator;
import com.futech.coffeeshop.utils.RegisterControl;
import com.futech.coffeeshop.utils.listener.SelectListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ItemLocalDatabase extends DatabaseHelper<ItemData> {


    private static final String DB_NAME = "items_db";
    private static final int DB_VERSION = 5;

    private final String API_ADDRESS;

    public ItemLocalDatabase(@Nullable Context context) {
        super(context, DB_NAME, DB_VERSION, ItemData.TABLE_NAME_KEY);
        assert context != null;
        API_ADDRESS = context.getString(R.string.api_address) + "items.php";
    }

    public void select(final int collection, final SelectListener<ItemData[]> listener) {
        ApiControl<ItemApi> api = new ApiControl<>(getContext(), API_ADDRESS, ItemApi.class);
        api.addParameter("action", "select").addParameter("collection", String.valueOf(collection));
        ItemData[] localItems = select(ItemData.CATEGORY_KEY + " = ?", new String[]{String.valueOf(collection)}).toArray(new ItemData[0]);
        listener.onSelect(localItems, false);
        select(api, new SelectListener<ItemData[]>() {
            @Override
            public void onSelect(ItemData[] items, boolean online) {
                listener.onSelect(items, online);
                delete(ItemData.CATEGORY_KEY + " = ?", new String[]{String.valueOf(collection)});
                insertItems(items);
            }

            @Override
            public void onError(String message) {
                listener.onError(message);
            }
        });
    }

    @Override
    public void insert(ItemData data) {
        final String whereCause = ItemData.ID_KEY + " = ?";
        final String[] args = {String.valueOf(data.getId())};
        final List<ItemData> select = super.select(whereCause, args);
        if (select.size() == 1) super.update(data, whereCause, args);
        else super.insert(data);
    }

    @Override
    public void insertItems(ItemData[] data) {
        for (ItemData item : data) {
            insert(item);
        }
    }

    public void selectFeeds(final SelectListener<ItemData[]> listener) {
        ApiControl<ItemApi> api = new ApiControl<>(getContext(), getContext().getString(R.string.api_address) + "feed.php", ItemApi.class);
        api.addParameter("action", "select_order");
        select(api, listener);
    }

    public void selectNewItems(final SelectListener<ItemData[]> listener) {
        ApiControl<ItemApi> api = new ApiControl<>(getContext(), getContext().getString(R.string.api_address) + "feed.php", ItemApi.class);
        api.addParameter("action", "select_new_items");
        select(api, listener);
    }

    public void selectLastOrderItems(final SelectListener<ItemData[]> listener) {
        ApiControl<ItemApi> api = new ApiControl<>(getContext(), getContext().getString(R.string.api_address) + "feed.php", ItemApi.class);
        api.addParameter("action", "select_last_order").addParameter("uid", String.valueOf(RegisterControl.getCurrentUserUid(getContext())));
        select(api, listener);
    }

    public void select(final ApiControl<ItemApi> api, final SelectListener<ItemData[]> listener) {
        api.response(new ApiControl.ApiListener<ItemApi>() {
            @Override
            public void onResponse(ItemApi api) {
                if (api.getData().getStatus().equals("error")) {
                    listener.onError(api.getData().getMsg());
                    return;
                }
                listener.onSelect(api.getData().getItems(), true);
                insertItems(api.getData().getItems());
            }

            @Override
            public void onError(VolleyError error) {
                listener.onError(ErrorTranslator.getErrorMessage(error));
            }
        });
    }

    public void insert(String title, String description, String type, int category, String sizes, float prices, String image, final ResultListener listener) {
        ApiControl<ItemApi> api = new ApiControl<>(getContext(), API_ADDRESS, ItemApi.class);
        api.addParameter("action", "insert").addParameter(ItemData.IMAGE_KEY, image).addParameter(ItemData.TITLE_KEY, title).addParameter(ItemData.INFORMATION_KEY, description).addParameter(ItemData.TYPE_KEY, type).addParameter("category", String.valueOf(category)).addParameter(ItemData.SIZES_KEY, sizes).addParameter(ItemData.PRICE_KEY, String.valueOf(prices)).addParameter("token", getContext().getString(R.string.api_token));

        api.response(new ApiControl.ApiListener<ItemApi>() {
            @Override
            public void onResponse(ItemApi api) {
                if (api.getData().getStatus().equals("error"))
                    listener.onError(api.getData().getMsg());
                else listener.onResult();
            }

            @Override
            public void onError(VolleyError error) {
                listener.onError(ErrorTranslator.getErrorMessage(error));
            }
        });
    }

    public void update(int id, Map<String, String> params, final ResultListener listener) {
        ApiControl<ItemApi> api = new ApiControl<>(getContext(), API_ADDRESS, ItemApi.class);
        api.addParameter("action", "update").addParameter("id", String.valueOf(id)).addParameters(Request.Method.GET, params);
        api.response(new ApiControl.ApiListener<ItemApi>() {
            @Override
            public void onResponse(ItemApi api) {
                if (api.getData().isSuccess()) {
                    listener.onResult();
                }else listener.onError(api.getData().getMsg());
            }

            @Override
            public void onError(VolleyError error) {
                listener.onError(ErrorTranslator.getErrorMessage(error));
            }
        });
    }

    @Override
    protected List<ItemData> convertCursorToList(Cursor cursor) {
        List<ItemData> items = new ArrayList<>();
        while (cursor.moveToNext()) {
            items.add(ItemData.getItemDataByNextCursor(cursor));
        }
        return items;
    }

    @Override
    public ContentValues convertToContentValues(ItemData itemData) {
        return itemData.convertToContent();
    }

    @Override
    protected String createTableQuery() {
        return QueryCreator.getCreateDatabaseQuery(ItemData.TABLE_NAME_KEY, needleColumns(), ItemData.ID_DB_KEY);
    }

    static Map<String, String> needleColumns() {
        Map<String, String> columns = new HashMap<>();
        columns.put(ItemData.ID_KEY, "INTEGER");
        columns.put(ItemData.ID_DB_KEY, "INTEGER");
        columns.put(ItemData.TITLE_KEY, "TEXT");
        columns.put(ItemData.INFORMATION_KEY, QueryCreator.STRING_KEY);
        columns.put(ItemData.IMAGE_KEY, QueryCreator.STRING_KEY);
        columns.put(ItemData.TYPE_KEY, QueryCreator.STRING_KEY);
        columns.put(ItemData.CATEGORY_KEY, QueryCreator.STRING_KEY);
        columns.put(ItemData.SIZES_KEY, QueryCreator.STRING_KEY);
        columns.put(ItemData.CREATE_TIME_KEY, QueryCreator.STRING_KEY);
        columns.put(ItemData.SALES_COUNT_KEY, QueryCreator.INTEGER_KEY);
        columns.put(ItemData.DISCOUNT_VALUE_KEY, QueryCreator.INTEGER_KEY);
        columns.put(ItemData.DISCOUNT_ID_KEY, QueryCreator.INTEGER_KEY);
        columns.put(ItemData.PRICE_KEY, QueryCreator.STRING_KEY);
        return columns;
    }

    public interface ResultListener {
        void onResult();

        void onError(String msg);
    }
}
