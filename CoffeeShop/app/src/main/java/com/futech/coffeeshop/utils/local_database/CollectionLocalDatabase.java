package com.futech.coffeeshop.utils.local_database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.futech.coffeeshop.R;
import com.futech.coffeeshop.obj.category.CategoryApi;
import com.futech.coffeeshop.obj.category.CategoryData;
import com.futech.coffeeshop.utils.ApiControl;
import com.futech.coffeeshop.utils.DatabaseHelper;
import com.futech.coffeeshop.utils.ErrorTranslator;
import com.futech.coffeeshop.utils.listener.DataChangeListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CollectionLocalDatabase extends DatabaseHelper<CategoryData> {

    private static final String DB_NAME = "collection_db";
    private static final int DB_VERSION = 1;

    private static String TABLE_NAME_KEY = "collection";
    private static String ID_KEY = "id";
    private static String COLLECTION_ID_KEY = "collectionId";
    private static String NAME_KEY = "name";
    private static String INFORMATION_KEY = "information";
    private static String IMAGE_KEY = "image";
    private final String API_ADDRESS;

    public CollectionLocalDatabase(Context context) {
        super(context, DB_NAME, DB_VERSION, TABLE_NAME_KEY);
        API_ADDRESS = context.getString(R.string.api_address) + "collection.php";
    }

    public void selectAllCollection(final SelectListener listener) {
        final List<CategoryData> selectList = select(null, null);
        CategoryData[] select = selectList.toArray(new CategoryData[0]);
        listener.onSelect(select, false);
        ApiControl<CategoryApi> api = new ApiControl<>(getContext(), API_ADDRESS, CategoryApi.class);
        api.addParameter("action", "select");
        api.response(new ApiControl.ApiListener<CategoryApi>() {
            @Override
            public void onResponse(CategoryApi api) {
                if (api.getData().getStatus().equals("error")) {
                    listener.onError(api.getData().getMsg());
                    return;
                }
                deleteAll();
                listener.onSelect(api.getData().getItems(), true);
                insertItems(api.getData().getItems());
            }

            @Override
            public void onError(VolleyError error) {
                listener.onError(ErrorTranslator.getErrorMessage(error));
            }
        });
    }

    /**
     * @param id: category id in Database
     */
    public void selectCollectionById(final int id, final SelectListener listener) {
        final List<CategoryData> categoryData = select("id=?", new String[]{String.valueOf(id)});
        listener.onSelect(categoryData.toArray(new CategoryData[0]), false);
        ApiControl<CategoryApi> api = new ApiControl<>(getContext(), API_ADDRESS, CategoryApi.class);
        api.addParameter("action", "select").addParameter("id", String.valueOf(id));
        api.response(new ApiControl.ApiListener<CategoryApi>() {
            @Override
            public void onResponse(CategoryApi api) {
                if (!api.getData().isSuccess()) {
                    listener.onError(api.getData().getMsg());
                }
                delete("id=?", new String[]{String.valueOf(id)});
                listener.onSelect(api.getData().getItems(), true);
            }

            @Override
            public void onError(VolleyError error) {
                listener.onError(ErrorTranslator.getErrorMessage(error));
            }
        });
    }

    @Override
    protected List<CategoryData> convertCursorToList(Cursor cursor) {
        List<CategoryData> items = new ArrayList<>();
        while (cursor.moveToNext()) {
            CategoryData categoryData = new CategoryData();
            categoryData.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLLECTION_ID_KEY)));
            categoryData.setImage(cursor.getString(cursor.getColumnIndexOrThrow(IMAGE_KEY)));
            categoryData.setName(cursor.getString(cursor.getColumnIndexOrThrow(NAME_KEY)));
            categoryData.setDbId(cursor.getInt(cursor.getColumnIndexOrThrow(ID_KEY)));
            categoryData.setInformation(cursor.getString(cursor.getColumnIndexOrThrow(INFORMATION_KEY)));
            items.add(categoryData);
        }
        return items;
    }

    @Override
    protected ContentValues convertToContentValues(CategoryData categoryData) {
        ContentValues values = new ContentValues();
        values.put(COLLECTION_ID_KEY, categoryData.getId());
        values.put(NAME_KEY, categoryData.getName());
        values.put(INFORMATION_KEY, categoryData.getInformation());
        values.put(IMAGE_KEY, categoryData.getImage());

        return values;
    }

    public void insert(String title, String information, String image, final DataChangeListener listener) {
        ApiControl<CategoryApi> api = new ApiControl<>(getContext(), API_ADDRESS, CategoryApi.class);
        api.addParameter("title", title).addParameter("information", information).addParameter("image", image).addParameter("action", "insert");
        api.response(new ApiControl.ApiListener<CategoryApi>() {
            @Override
            public void onResponse(CategoryApi api) {
                if (api.getData().isSuccess()) listener.onChange();
                else listener.onError(api.getData().getMsg());
            }

            @Override
            public void onError(VolleyError error) {
                listener.onError(ErrorTranslator.getErrorMessage(error));
            }
        });
    }

    public void update(Map<String, String> values, int id, final DataChangeListener listener) {
        ApiControl<CategoryApi> api = new ApiControl<>(getContext(), API_ADDRESS, CategoryApi.class);
        api.addParameters(Request.Method.GET, values).addParameter("action", "update").addParameter("id", String.valueOf(id));
        api.response(new ApiControl.ApiListener<CategoryApi>() {
            @Override
            public void onResponse(CategoryApi api) {
                if (api.getData().isSuccess()) listener.onChange();
                else listener.onError(api.getData().getMsg());
            }

            @Override
            public void onError(VolleyError error) {
                listener.onError(ErrorTranslator.getErrorMessage(error));
            }
        });
    }

    @Override
    protected String createTableQuery() {
        return "CREATE TABLE " + TABLE_NAME_KEY + " (" + ID_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLLECTION_ID_KEY + " INTEGER, " + NAME_KEY + " TEXT, " + INFORMATION_KEY + " TEXT, " + IMAGE_KEY + " TEXT " + ")";
    }

    public interface SelectListener {
        void onSelect(CategoryData[] items, boolean isOnline);

        void onError(String error);
    }
}
