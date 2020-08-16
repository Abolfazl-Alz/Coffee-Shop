package com.futech.coffeeshop.views;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

public class PriceTextView extends androidx.appcompat.widget.AppCompatTextView {
    private CharSequence text;

    public PriceTextView(Context context) {
        super(context);
    }

    public PriceTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PriceTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

//    @Override
//    public void setText(CharSequence text, BufferType type) {
//        super.setText(NumberUtils.getString(text.toString()), type);
//    }

    public void setPriceText(float price) {
        super.setText((String.format(getTextLocale(), "%.2f تومان", price)).replace(".00", ""));
    }
}
