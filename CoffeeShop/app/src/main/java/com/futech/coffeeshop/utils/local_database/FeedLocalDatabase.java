package com.futech.coffeeshop.utils.local_database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import androidx.annotation.NonNull;

import com.android.volley.VolleyError;
import com.futech.coffeeshop.R;
import com.futech.coffeeshop.obj.feed.FeedApi;
import com.futech.coffeeshop.obj.feed.FeedData;
import com.futech.coffeeshop.obj.item.ItemData;
import com.futech.coffeeshop.utils.ApiControl;
import com.futech.coffeeshop.utils.DatabaseHelper;
import com.futech.coffeeshop.utils.ErrorTranslator;
import com.futech.coffeeshop.utils.listener.SelectListener;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static com.futech.coffeeshop.obj.feed.FeedData.ACTION_KEY;
import static com.futech.coffeeshop.obj.feed.FeedData.COLOR_KEY;
import static com.futech.coffeeshop.obj.feed.FeedData.DB_ID_KEY;
import static com.futech.coffeeshop.obj.feed.FeedData.DESCRIPTION_KEY;
import static com.futech.coffeeshop.obj.feed.FeedData.ID_KEY;
import static com.futech.coffeeshop.obj.feed.FeedData.IMAGE_KEY;
import static com.futech.coffeeshop.obj.feed.FeedData.ITEMS_KEY;
import static com.futech.coffeeshop.obj.feed.FeedData.POSITION_KEY;
import static com.futech.coffeeshop.obj.feed.FeedData.STATUS_KEY;
import static com.futech.coffeeshop.obj.feed.FeedData.TITLE_KEY;

public class FeedLocalDatabase extends DatabaseHelper<FeedData> {

    private static String DB_NAME = "feeds";
    private static int DB_VERSION = 1;
    private static String TABLE_NAME = "feeds";
    private final String API_ADDRESS;

    public FeedLocalDatabase(@NonNull Context context) {
        super(context, DB_NAME, DB_VERSION, TABLE_NAME);
        API_ADDRESS = context.getString(R.string.api_address) + "feed.php";
    }

    @Override
    protected List<FeedData> convertCursorToList(Cursor cursor) {
        List<FeedData> feeds = new ArrayList<>();
        while (cursor.moveToNext()) {
            FeedData feed = new FeedData();

            StringBuilder sb = new StringBuilder();
            final String[] idList = cursor.getString(cursor.getColumnIndexOrThrow(ITEMS_KEY)).split("-");
            for (int i = 0; i < idList.length; i++) {
                if (!sb.toString().equals("")) {
                    sb.append(" or ");
                }
                sb.append(ItemData.ID_KEY).append("=?");
            }
            ItemLocalDatabase db = new ItemLocalDatabase(getContext());

            feed.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(TITLE_KEY)));
            feed.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(DESCRIPTION_KEY)));
            feed.setImage(cursor.getString(cursor.getColumnIndexOrThrow(IMAGE_KEY)));
            feed.setColor(cursor.getString(cursor.getColumnIndexOrThrow(COLOR_KEY)));
            feed.setDbId(cursor.getInt(cursor.getColumnIndexOrThrow(DB_ID_KEY)));
            feed.setId(cursor.getInt(cursor.getColumnIndexOrThrow(ID_KEY)));
            feed.setPosition(cursor.getInt(cursor.getColumnIndexOrThrow(POSITION_KEY)));
            feed.setAction(cursor.getInt(cursor.getColumnIndexOrThrow(ACTION_KEY)));
            feed.setStatus(cursor.getInt(cursor.getColumnIndexOrThrow(STATUS_KEY)));
            feed.setItems(db.select(sb.toString(), idList));

            feeds.add(feed);
        }
        return feeds;
    }

    @Override
    protected ContentValues convertToContentValues(FeedData value) {
        ContentValues values = new ContentValues();
        values.put(ID_KEY, value.getId());
        values.put(TITLE_KEY, value.getTitle());
        values.put(DESCRIPTION_KEY, value.getDescription());
        values.put(ACTION_KEY, value.getAction());
        values.put(POSITION_KEY, value.getPosition());
        values.put(STATUS_KEY, value.getStatus());
        values.put(COLOR_KEY, value.getColor());
        values.put(IMAGE_KEY, value.getImage());
        values.put(ITEMS_KEY, value.getItemsIdString());
        return values;
    }

    public void select(final SelectListener<Map<Integer, List<FeedData>>> listener) {

        listener.onSelect(select(), false);

        ApiControl<FeedApi> api = new ApiControl<>(getContext(), API_ADDRESS, FeedApi.class);
        api.addParameter("action", "select");
        api.response(new ApiControl.ApiListener<FeedApi>() {
            @Override
            public void onResponse(FeedApi api) {
                if (api.getData().isSuccess()) {
                    listener.onSelect(new TreeMap<>(api.getData().getItems()), true);
                    deleteAll();
                    for (List<FeedData> feeds : api.getData().getItems().values())
                        insertItems(feeds.toArray(new FeedData[0]));
                }else listener.onError(api.getData().getMessage());
            }

            @Override
            public void onError(VolleyError error) {
                listener.onError(ErrorTranslator.getErrorMessage(error));
            }
        });
    }

    private Map<Integer, List<FeedData>> select() {

        final List<FeedData> select = super.select(null, null);

        Map<Integer, List<FeedData>> feeds = new Hashtable<>();
        for (FeedData feed : select) {
            if (!feeds.containsKey(feed.getPosition()))
                feeds.put(feed.getPosition(), new ArrayList<FeedData>());
            final List<FeedData> feedData = feeds.get(feed.getPosition());

            if (feedData != null) feedData.add(feed);
        }
        return new TreeMap<>(feeds);
    }

    @Override
    protected String createTableQuery() {
        return String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s INTEGER, %s TEXT, %s TEXT, %s INTEGER, %s INTEGER, %s  INTEGER, %s TEXT, %s TEXT, %s TEXT)", tableName, DB_ID_KEY, ID_KEY, TITLE_KEY, DESCRIPTION_KEY, ACTION_KEY, POSITION_KEY, STATUS_KEY, COLOR_KEY, IMAGE_KEY, ITEMS_KEY);
    }
}
