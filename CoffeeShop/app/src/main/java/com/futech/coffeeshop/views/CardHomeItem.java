package com.futech.coffeeshop.views;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.futech.coffeeshop.R;
import com.futech.coffeeshop.obj.card.CardData;
import com.futech.coffeeshop.utils.HttpHelper;

public class CardHomeItem extends FrameLayout {
    private TextView altText;
    private ImageView imageView;

    private final String TAG = "CardHomeItem";

    public CardHomeItem(Context context) {
        super(context);
        View view = inflate(getContext(), R.layout.card_home_item, this);
        setClickable(true);
        altText = view.findViewById(R.id.card_alt);
        imageView = view.findViewById(R.id.card_image);
    }

    public void setCardData(CardData card) {
        Log.d(TAG, "setCardData: loading card data - " + card.toString());
        altText.setText(card.getAlt());
        changeVisibility(false);
        try {
            setBackgroundColor(Color.parseColor(card.getColor()));
        } catch (Exception ex) {
            //ignore
        }
        HttpHelper.loadImage(getContext(), card.getImage(), imageView, null, 250, 150, "card" + card.getId(), new HttpHelper.HttpImageDownloaderListener() {
            @Override
            public void onDownload(boolean isOnline) {
                changeVisibility(true);
            }

            @Override
            public void onError(String errorMessage) {
                Log.d(TAG, "onError: " + errorMessage);
                changeVisibility(true);
            }
        });
        refreshDrawableState();
    }

    private void changeVisibility(boolean visible) {
        altText.setVisibility(visible ? GONE : VISIBLE);
        imageView.setVisibility(visible ? VISIBLE : GONE);
    }
}
