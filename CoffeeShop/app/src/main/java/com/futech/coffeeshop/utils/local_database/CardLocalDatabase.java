package com.futech.coffeeshop.utils.local_database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.android.volley.VolleyError;
import com.futech.coffeeshop.R;
import com.futech.coffeeshop.obj.card.CardApi;
import com.futech.coffeeshop.obj.card.CardData;
import com.futech.coffeeshop.utils.ApiControl;
import com.futech.coffeeshop.utils.DatabaseHelper;
import com.futech.coffeeshop.utils.ErrorTranslator;
import com.futech.coffeeshop.utils.listener.SelectListener;

import java.util.ArrayList;
import java.util.List;

import static com.futech.coffeeshop.obj.card.CardData.*;

public class CardLocalDatabase extends DatabaseHelper<CardData> {

    private static final String TABLE_NAME = "card";
    private static final String DB_NAME = "card_db";
    private static final int DB_VERSION = 1;
    private final String API_ADDRESS;

    public CardLocalDatabase(Context context) {
        super(context, DB_NAME, DB_VERSION, TABLE_NAME);
        API_ADDRESS = context.getString(R.string.api_address) + "card.php";
    }

    @Override
    protected List<CardData> convertCursorToList(Cursor cursor) {

        List<CardData> cards = new ArrayList<>();
        while (cursor.moveToNext()) {
            CardData data = new CardData();
            data.setAlt(cursor.getString(cursor.getColumnIndexOrThrow(ALT_KEY)));
            data.setColor(cursor.getString(cursor.getColumnIndexOrThrow(COLOR_KEY)));
            data.setId(cursor.getInt(cursor.getColumnIndexOrThrow(ID_KEY)));
            data.setIdDb(cursor.getInt(cursor.getColumnIndexOrThrow(ID_DB_KEY)));
            data.setImage(cursor.getString(cursor.getColumnIndexOrThrow(IMAGE_KEY)));
            data.setLink(cursor.getString(cursor.getColumnIndexOrThrow(LINK_KEY)));
            cards.add(data);
        }
        return cards;
    }

    public void select(final SelectListener<CardData[]> listener) {
        listener.onSelect(selectAll().toArray(new CardData[0]), false);
        ApiControl<CardApi> api = new ApiControl<>(getContext(), API_ADDRESS, CardApi.class);
        api.addParameter("action", "select");
        api.response(new ApiControl.ApiListener<CardApi>() {
            @Override
            public void onResponse(CardApi api) {
                if (api.getData().isSuccess()) {
                    listener.onSelect(api.getData().getItems(), true);
                    deleteAll();
                    insertItems(api.getData().getItems());
                }else {
                    listener.onError(api.getData().getMessage());
                }
            }

            @Override
            public void onError(VolleyError error) {
                listener.onError(ErrorTranslator.getErrorMessage(error));
            }
        });
    }

    @Override
    protected ContentValues convertToContentValues(CardData value) {
        return value.CardDataToValues();
    }

    @Override
    protected String createTableQuery() {
        return "CREATE TABLE " + TABLE_NAME + " (" + ID_DB_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT, " + ID_KEY + " INTEGER, " + IMAGE_KEY + " TEXT, " + ALT_KEY + " TEXT, " + LINK_KEY + " TEXT, " + COLOR_KEY + " TEXT)";
    }
}
