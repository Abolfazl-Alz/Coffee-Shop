package com.futech.coffeeshop.utils.local_database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.futech.coffeeshop.R;
import com.futech.coffeeshop.obj.posts.PostData;
import com.futech.coffeeshop.obj.posts.PostsApi;
import com.futech.coffeeshop.obj.register.RegisterData;
import com.futech.coffeeshop.utils.ApiControl;
import com.futech.coffeeshop.utils.DatabaseHelper;
import com.futech.coffeeshop.utils.ErrorTranslator;
import com.futech.coffeeshop.utils.listener.SelectListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class PostsLocalDatabase extends DatabaseHelper<PostData> {

    private static final int DB_VERSION = 2;
    private SimpleDateFormat dateFormat;
    private final String API_ADDRESS;

    @SuppressLint("SimpleDateFormat")
    public PostsLocalDatabase(Context context) {
        super(context, PostData.TABLE_POST_KEY, DB_VERSION, PostData.TABLE_POST_KEY);
        dateFormat = new SimpleDateFormat(getContext().getString(R.string.date_db_format));
        API_ADDRESS = context.getString(R.string.api_address) + "post.php";
    }

    public void select(int page, int count, SelectListener<PostData[]> listener) {
        listener.onSelect(selectAll().toArray(new PostData[0]), false);
        ApiControl<PostsApi> api = new ApiControl<>(getContext(), API_ADDRESS, PostsApi.class);
        api.addParameter("action", "select").addParameter(Request.Method.GET, "page", String.valueOf(page)).addParameter(Request.Method.GET, "count", String.valueOf(count));
        api.response(new ApiControl.ApiListener<PostsApi>() {
            @Override
            public void onResponse(PostsApi api) {
                if (api.getData().isSuccess()) {
                    listener.onSelect(api.getData().getItems(), true);
                    deleteAll();
                    insertItems(api.getData().getItems());
                }else listener.onError(api.getData().getMessage());
            }

            @Override
            public void onError(VolleyError error) {
                listener.onError(ErrorTranslator.getErrorMessage(error));
            }
        });
    }

    public void selectById(int id, SelectListener<PostData> listener) {
        ApiControl<PostsApi> api = new ApiControl<>(getContext(), API_ADDRESS, PostsApi.class);
        api.addParameter("action", "select_by_id").addParameter(Request.Method.GET, "id", String.valueOf(id));
        api.response(new ApiControl.ApiListener<PostsApi>() {
            @Override
            public void onResponse(PostsApi api) {
                if (api.getData().isSuccess()) {
                    if (api.getData().getItems().length == 0) listener.onSelect(null, true);
                    if (api.getData().getItems().length > 0)
                        listener.onSelect(api.getData().getItems()[0], true);
//                    delete("id=?", new String[]{String.valueOf(api.getData().getItems()[0].getId())});
                    else
                        listener.onSelect(null, true);

                }else listener.onError(api.getData().getMessage());
            }

            @Override
            public void onError(VolleyError error) {
                listener.onError(ErrorTranslator.getErrorMessage(error));
            }
        });
    }

    @Override
    protected List<PostData> convertCursorToList(Cursor cursor) {
        List<PostData> list = new ArrayList<>();

        while (cursor.moveToNext()) {
            PostData data = new PostData();
            data.setImageUrl(cursor.getString(cursor.getColumnIndexOrThrow(PostData.IMAGE_KEY)));
            data.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(PostData.TITLE_KEY)));
            data.setText(cursor.getString(cursor.getColumnIndexOrThrow(PostData.TEXT_KEY)));
            data.setColorString(cursor.getString(cursor.getColumnIndexOrThrow(PostData.COLOR_KEY)));
            RegisterData writer = new RegisterData();
            writer.setFirstname(cursor.getString(cursor.getColumnIndexOrThrow(PostData.WRITER_NAME)));
            writer.setId(cursor.getInt(cursor.getColumnIndexOrThrow(PostData.WRITER_ID)));
            data.setWriter(writer);
            try {
                data.setCreatedTime(dateFormat.parse(cursor.getString(cursor.getColumnIndexOrThrow(PostData.DATE_KEY))));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            list.add(data);
        }

        return list;
    }

    @Override
    protected ContentValues convertToContentValues(PostData value) {
        ContentValues values = new ContentValues();
        values.put(PostData.ID_KEY, value.getId());
        values.put(PostData.ID_DB_KEY, value.getDbId());
        values.put(PostData.TITLE_KEY, value.getTitle());
        values.put(PostData.TEXT_KEY, value.getText());
        values.put(PostData.DATE_KEY, dateFormat.format(value.getCreatedTime()));
        values.put(PostData.IMAGE_KEY, value.getImageUrl());
        values.put(PostData.WRITER_ID, value.getWriter().getId());
        values.put(PostData.WRITER_NAME, value.getWriter().getFullName());
        values.put(PostData.COLOR_KEY, value.getColorString());
        return values;
    }

    @Override
    protected String createTableQuery() {
        return "CREATE TABLE " + PostData.TABLE_POST_KEY + " (" + PostData.ID_DB_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT, " + PostData.ID_KEY + " INTEGER, " + PostData.TITLE_KEY + " TEXT, " + PostData.TEXT_KEY + " TEXT, " + PostData.COLOR_KEY + " TEXT, " + PostData.DATE_KEY + " TEXT, " + PostData.IMAGE_KEY + " TEXT, " + PostData.WRITER_ID + " INTEGER, " + PostData.WRITER_NAME + " TEXT" + ")";
    }
}
