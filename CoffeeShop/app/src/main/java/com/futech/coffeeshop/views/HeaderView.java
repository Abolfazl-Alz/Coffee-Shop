package com.futech.coffeeshop.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.futech.coffeeshop.R;

public class HeaderView extends FrameLayout {

    String mTitle = "";
    int mSrc = 0;

    public HeaderView(Context context) {
        super(context);
        prepareLayout(null);
    }

    public HeaderView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        prepareLayout(attrs);
    }

    public HeaderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        prepareLayout(attrs);
    }

    private void prepareLayout(AttributeSet attrs) {
        View view = inflate(getContext(), R.layout.header_view, this);
        TextView title = view.findViewById(R.id.header_text);
        ImageView image = view.findViewById(R.id.header_image);

        TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.HeaderView, 0, 0);

        try {
            mTitle = typedArray.getString(R.styleable.HeaderView_title);
            mSrc = typedArray.getResourceId(R.styleable.HeaderView_src, 0);
            title.setText(mTitle);
            image.setImageResource(mSrc);
        } finally {
            typedArray.recycle();
        }
    }
}
