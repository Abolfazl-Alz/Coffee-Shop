package com.futech.coffeeshop.utils;

import android.annotation.SuppressLint;
import android.content.Context;

import com.android.volley.VolleyError;
import com.futech.coffeeshop.R;
import com.futech.coffeeshop.obj.discount.DiscountApi;
import com.futech.coffeeshop.obj.discount.DiscountData;
import com.futech.coffeeshop.utils.listener.DataChangeListener;
import com.futech.coffeeshop.utils.listener.SelectListener;
import com.google.android.gms.common.util.ArrayUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DiscountHelper {

    private final Context context;
    private final String API_ADDRESS;

    public DiscountHelper(Context context) {
        this.context = context;
        API_ADDRESS = context.getString(R.string.api_address) + "discount.php";

    }

    public void select(SelectListener<List<DiscountData>> listener) {
        ApiControl<DiscountApi> api = new ApiControl<>(context, API_ADDRESS, DiscountApi.class);
        api.addParameter("action", "select");
        api.response(new ApiControl.ApiListener<DiscountApi>() {
            @Override
            public void onResponse(DiscountApi api) {
                if (api.getData().isSuccess())
                    listener.onSelect(ArrayUtils.toArrayList(api.getData().getItems()), true);
                else listener.onError(api.getData().getMessage());
            }

            @Override
            public void onError(VolleyError error) {
                listener.onError(ErrorTranslator.getErrorMessage(error));
            }
        });
    }

    public void getDiscountData(String code, SelectListener<DiscountData> listener) {
        ApiControl<DiscountApi> api = new ApiControl<>(context, API_ADDRESS, DiscountApi.class);
        api.addParameter("action", "select_by_code").addParameter("code", code);
        api.response(new ApiControl.ApiListener<DiscountApi>() {
            @Override
            public void onResponse(DiscountApi api) {
                if (api.getData().isSuccess() && api.getData().getItems().length == 1)
                    listener.onSelect(api.getData().getItems()[0], true);
                else if (api.getData().isSuccess() && api.getData().getItems().length == 0)
                    listener.onSelect(null, true);
                else listener.onError(api.getData().getMessage());
            }

            @Override
            public void onError(VolleyError error) {
                listener.onError(ErrorTranslator.getErrorMessage(error));
            }
        });
    }

    public void addNewDiscount(String title, String code, int value, Date expiration, DataChangeListener listener) {
        ApiControl<DiscountApi> api = new ApiControl<>(context, API_ADDRESS, DiscountApi.class);
        @SuppressLint(
                "SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat(context.getString(R.string.date_db_format), new Locale("en"));
        String expirationString = formatter.format(expiration);
        api.addParameter("title", title).addParameter("code", code).addParameter("value", String.valueOf(value)).addParameter("expiration", expirationString).addParameter("action", "insert");
        api.response(new ApiControl.ApiListener<DiscountApi>() {
            @Override
            public void onResponse(DiscountApi api) {
                if (!api.getData().isSuccess()) listener.onError(api.getData().getMessage());
                else listener.onChange();
            }

            @Override
            public void onError(VolleyError error) {
                listener.onError(ErrorTranslator.getErrorMessage(context, error));
            }
        });
    }


    public void getDiscountList(SelectListener<DiscountData[]> discountList) {
        ApiControl<DiscountApi> api = new ApiControl<>(context, API_ADDRESS, DiscountApi.class);
        api.addParameter("action", "select");
        api.response(new ApiControl.ApiListener<DiscountApi>() {
            @Override
            public void onResponse(DiscountApi api) {
                if (api.getData().isSuccess())
                    discountList.onSelect(api.getData().getItems(), true);
                else discountList.onError(api.getData().getMessage());
            }

            @Override
            public void onError(VolleyError error) {
                discountList.onError(ErrorTranslator.getErrorMessage(error));
            }
        });
    }

    public void deleteDiscount(int id, DataChangeListener listener) {
        ApiControl<DiscountApi> api = new ApiControl<>(context, API_ADDRESS, DiscountApi.class);
        api.addParameter("action", "delete").addParameter("id", String.valueOf(id));
        api.response(new ApiControl.ApiListener<DiscountApi>() {
            @Override
            public void onResponse(DiscountApi api) {
                if (api.getData().isSuccess()) listener.onChange();
                else listener.onError(api.getData().getMessage());
            }

            @Override
            public void onError(VolleyError error) {
                listener.onError(ErrorTranslator.getErrorMessage(context, error));
            }
        });
    }
}
