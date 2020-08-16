package com.futech.coffeeshop.utils.local_database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.android.volley.VolleyError;
import com.futech.coffeeshop.R;
import com.futech.coffeeshop.obj.address.AddressApi;
import com.futech.coffeeshop.obj.address.AddressData;
import com.futech.coffeeshop.utils.ApiControl;
import com.futech.coffeeshop.utils.DatabaseHelper;
import com.futech.coffeeshop.utils.ErrorTranslator;

import java.util.ArrayList;
import java.util.List;

public class AddressLocalDatabase extends DatabaseHelper<AddressData> {

    private static final String DB_NAME = "address_db";
    private static final String TABLE_NAME = "address";
    private static final int TABLE_VERSION = 1;
    private final String API_ADDRESS;

    public AddressLocalDatabase(Context context) {
        super(context, DB_NAME, TABLE_VERSION, TABLE_NAME);
        API_ADDRESS = getContext().getString(R.string.api_address) + "address.php";
    }

    @Override
    protected List<AddressData> convertCursorToList(Cursor cursor) {
        List<AddressData> addressDataList = new ArrayList<>();

        while (cursor.moveToNext()) {
            AddressData data = new AddressData();
            data.setId(cursor.getInt(cursor.getColumnIndexOrThrow(AddressData.ID_KEY)));
            data.setDbId(cursor.getInt(cursor.getColumnIndexOrThrow(AddressData.ID_DB_KEY)));
            data.setAddress(cursor.getString(cursor.getColumnIndexOrThrow(AddressData.ADDRESS_KEY)));
            data.setName(cursor.getString(cursor.getColumnIndexOrThrow(AddressData.NAME_KEY)));
            data.setUid(cursor.getInt(cursor.getColumnIndexOrThrow(AddressData.UID_KEY)));
            data.setLat(cursor.getFloat(cursor.getColumnIndexOrThrow(AddressData.LAT_KEY)));
            data.setLng(cursor.getFloat(cursor.getColumnIndexOrThrow(AddressData.LNG_KEY)));
        }

        return addressDataList;
    }

    @Override
    protected ContentValues convertToContentValues(AddressData value) {
        return value.convertToContentValues();
    }

    public void insert(final AddressData data, final AddressListener listener) {

        ApiControl<AddressApi> api = new ApiControl<>(getContext(), API_ADDRESS, AddressApi.class);
        api.addParameter(AddressData.NAME_KEY, data.getName());
        api.addParameter(AddressData.ADDRESS_KEY, data.getAddress());
        api.addParameter(AddressData.UID_KEY, String.valueOf(data.getUid()));
        api.addParameter(AddressData.LAT_KEY, String.valueOf(data.getLat()));
        api.addParameter(AddressData.LNG_KEY, String.valueOf(data.getLng()));
        api.addParameter("action", "insert");

        api.response(new ApiControl.ApiListener<AddressApi>() {
            @Override
            public void onResponse(AddressApi api) {
                if (api.getData().getStatus().equals("error")) {
                    listener.onError("Adding Error: " + api.getData().getMsg());
                }else {
                    try {
                        data.setId(Integer.parseInt(api.getData().getMsg()));
                        listener.onChange(data);
                    } catch (NumberFormatException ex) {
                        listener.onError("Invalid Response from Server");
                    }
                }
            }

            @Override
            public void onError(VolleyError error) {
                listener.onError("Response Error: " + ErrorTranslator.getErrorMessage(error));
            }
        });
    }

    public void select(final int uid, final SelectAddressListener listener) {
        ApiControl<AddressApi> api = new ApiControl<>(getContext(), API_ADDRESS, AddressApi.class);
        AddressData[] addressList = new AddressData[selectAll().size()];
        listener.onSelect(selectAll().toArray(addressList), false);
        api.addParameter(AddressData.UID_KEY, String.valueOf(uid));
        api.addParameter("action", "select");
        api.response(new ApiControl.ApiListener<AddressApi>() {
            @Override
            public void onResponse(AddressApi api) {
                if (api.getData().getStatus().equals("error")) {
                    listener.onError(api.getData().getMsg());
                }else {
                    listener.onSelect(api.getData().getItems(), true);
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

    public void delete(final int addressId, final AddressListener listener) {
        ApiControl<AddressApi> api = new ApiControl<>(getContext(), API_ADDRESS, AddressApi.class);
        api.addParameter("action", "delete");
        api.addParameter("id", String.valueOf(addressId));
        api.response(new ApiControl.ApiListener<AddressApi>() {
            @Override
            public void onResponse(AddressApi api) {
                if (api.getData().getStatus().equals("error")) {
                    listener.onError(api.getData().getMsg());
                    return;
                }

                listener.onChange(null);
            }

            @Override
            public void onError(VolleyError error) {
                listener.onError(ErrorTranslator.getErrorMessage(error));
            }
        });
    }

    public void update(final int addressId, final AddressData data, final AddressListener listener) {
        ApiControl<AddressApi> api = new ApiControl<>(getContext(), API_ADDRESS, AddressApi.class);
        api.addParameter("action", "update");
        api.addParameter(AddressData.NAME_KEY, data.getName());
        api.addParameter(AddressData.ADDRESS_KEY, data.getAddress());
        api.addParameter(AddressData.UID_KEY, String.valueOf(data.getUid()));
        api.addParameter(AddressData.LAT_KEY, String.valueOf(data.getLat()));
        api.addParameter(AddressData.LNG_KEY, String.valueOf(data.getLng()));
        api.addParameter("id", String.valueOf(addressId));
        api.response(new ApiControl.ApiListener<AddressApi>() {
            @Override
            public void onResponse(AddressApi api) {
                if (api.getData().getStatus().equals("error")) {
                    listener.onError(api.getData().getMsg());
                    return;
                }

                listener.onChange(null);
            }

            @Override
            public void onError(VolleyError error) {
                listener.onError(ErrorTranslator.getErrorMessage(error));
            }
        });
    }

    @Override
    protected String createTableQuery() {
        return "CREATE TABLE " + tableName + "(" + AddressData.ID_DB_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT, " + AddressData.ID_KEY + " INTEGER, " + AddressData.ADDRESS_KEY + " TEXT, " + AddressData.UID_KEY + " Integer, " + AddressData.NAME_KEY + " TEXT, " + AddressData.LAT_KEY + " REAL, " + AddressData.LNG_KEY + " REAL " + ")";
    }

    public interface AddressListener {
        void onChange(AddressData addressData);

        void onError(String msg);
    }

    public interface SelectAddressListener {
        void onSelect(AddressData[] addressList, boolean isOnline);

        void onError(String msg);
    }
}
