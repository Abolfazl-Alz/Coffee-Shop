package com.futech.coffeeshop.utils.local_database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import androidx.annotation.Nullable;

import com.android.volley.VolleyError;
import com.futech.coffeeshop.R;
import com.futech.coffeeshop.obj.review.ReviewApi;
import com.futech.coffeeshop.obj.review.ReviewData;
import com.futech.coffeeshop.utils.ApiControl;
import com.futech.coffeeshop.utils.DatabaseHelper;
import com.futech.coffeeshop.utils.ErrorTranslator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.futech.coffeeshop.obj.review.ReviewData.ID;
import static com.futech.coffeeshop.obj.review.ReviewData.ID_DB;
import static com.futech.coffeeshop.obj.review.ReviewData.IID;
import static com.futech.coffeeshop.obj.review.ReviewData.NAME;
import static com.futech.coffeeshop.obj.review.ReviewData.RATE;
import static com.futech.coffeeshop.obj.review.ReviewData.TEXT;
import static com.futech.coffeeshop.obj.review.ReviewData.UID;

public class ReviewLocalDatabase extends DatabaseHelper<ReviewData> {

    private static final String DB_NAME = "review_db";
    private static final int DB_VERSION = 1;

    private static final String TABLE_NAME = "review";
    @Nullable
    private final Context context;

    public ReviewLocalDatabase(@Nullable Context context) {
        super(context, DB_NAME, DB_VERSION, TABLE_NAME);
        this.context = context;
    }

    @Override
    protected List<ReviewData> convertCursorToList(Cursor cursor) {
        List<ReviewData> reviewDataList = new ArrayList<>();

        while (cursor.moveToNext()) {
            ReviewData data = new ReviewData();

            data.setDbId(cursor.getInt(cursor.getColumnIndexOrThrow(ID_DB)));
            data.setText(cursor.getString(cursor.getColumnIndexOrThrow(ReviewData.TEXT)));
            data.setRate(cursor.getInt(cursor.getColumnIndexOrThrow(ReviewData.RATE)));
            data.setId(cursor.getInt(cursor.getColumnIndexOrThrow(ReviewData.ID)));
            data.setUid(cursor.getInt(cursor.getColumnIndexOrThrow(ReviewData.UID)));

            reviewDataList.add(data);
        }

        return reviewDataList;
    }

    @Override
    protected ContentValues convertToContentValues(ReviewData value) {
        return value.convertToContents();
    }

    public void addReview(int uid, int itemId, String message, float rate, final ActionListener listener) {
        final ReviewData reviewData = new ReviewData(0, 0, uid, itemId, message, rate);
        ApiControl<ReviewApi> api = createApi();
        api.addParameter("uid", String.valueOf(uid));
        api.addParameter("msg", message);
        api.addParameter("rate", String.valueOf(rate));
        api.addParameter("iid", String.valueOf(itemId));
        api.addParameter("action", "insert");
        api.response(new ApiControl.ApiListener<ReviewApi>() {
            @Override
            public void onResponse(ReviewApi api) {
                if (api.getData().getStatus().equals("error")) {
                    listener.onError(api.getData().getMsg());
                }else {
                    listener.onResponse(true);
                    reviewData.setId(Integer.parseInt(api.getData().getMsg()));
                    insert(reviewData);
                }
            }

            @Override
            public void onError(VolleyError error) {
                listener.onError(ErrorTranslator.getErrorMessage(error));
                listener.onResponse(false);
            }
        });
    }

    private ApiControl<ReviewApi> createApi() {
        return new ApiControl<>(context, Objects.requireNonNull(context).getString(R.string.api_address) + "review.php", ReviewApi.class);
    }

    public void addReview(ReviewData reviewData, ActionListener listener) {
        addReview(reviewData.getUid(), reviewData.getItemId(), reviewData.getText(), reviewData.getRate(), listener);
    }

    public void selectReview(int page, int itemId, final SelectListener listener) {
        selectReview(page, 3, itemId, listener);
    }

    public void selectReview(int page, int count, int itemId, final SelectListener listener) {
        if (page < 1) page = 1;
        if (count < 1) count = 1;

        List<ReviewData> reviewsSelected = select(IID + "=?", new String[]{String.valueOf(itemId)});
        List<ReviewData> reviews = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            if (i < reviewsSelected.size()) reviews.add(reviewsSelected.get(i));
            else break;
        }
        listener.onSelect(reviews, false);
        ApiControl<ReviewApi> api = createApi();
        api.addParameter("page", String.valueOf(page));
        api.addParameter("iid", String.valueOf(itemId));
        api.addParameter("count", String.valueOf(count));
        api.addParameter("action", "select");
        api.response(new ApiControl.ApiListener<ReviewApi>() {
            @Override
            public void onResponse(ReviewApi api) {
                if (api.getData().getStatus().equals("error")) {
                    listener.onError(api.getData().getMsg());
                }else {
                    listener.onSelect(Arrays.asList(api.getData().getItems()), true);
                    deleteAll();
                    insertItems(api.getData().getItems());
                }
            }

            @Override
            public void onError(VolleyError error) {
                listener.onError(ErrorTranslator.getErrorMessage(error));
            }
        });
    }

    public void deleteReview(final int id, final ActionListener listener) {
        ApiControl<ReviewApi> api = createApi();
        api.addParameter("id", String.valueOf(id));
        api.addParameter("action", "delete");
        api.response(new ApiControl.ApiListener<ReviewApi>() {
            @Override
            public void onResponse(ReviewApi api) {
                if (api.getData().getStatus().equals("error")) {
                    listener.onError(api.getData().getMsg());
                }else {
                    listener.onResponse(true);
                    delete(ID + "=?", new String[]{String.valueOf(id)});
                }
            }

            @Override
            public void onError(VolleyError error) {
                listener.onError(ErrorTranslator.getErrorMessage(error));
                listener.onResponse(false);
            }
        });
    }

    @Override
    protected String createTableQuery() {
        return "CREATE TABLE " + tableName + " (" + ID_DB + " INTEGER PRIMARY KEY AUTOINCREMENT, " + ID + " REAL, " + UID + " INTEGER, " + TEXT + " TEXT, " + RATE + " FLOAT, " + IID + " Integer, " + NAME + " TEXT" + ")";
    }

    @Override
    protected String deleteTableQuery() {
        return "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public interface ActionListener {
        void onResponse(boolean isSuccess);

        void onError(String error);
    }

    public interface SelectListener {
        void onSelect(List<ReviewData> reviewData, boolean isOnline);

        void onError(String errorMessage);
    }
}
