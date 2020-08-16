package com.futech.coffeeshop.ui.post;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.futech.coffeeshop.R;
import com.futech.coffeeshop.obj.discount.DiscountData;
import com.futech.coffeeshop.obj.posts.PostData;
import com.futech.coffeeshop.utils.HttpHelper;
import com.futech.coffeeshop.views.DiscountButton;
import com.google.android.material.appbar.CollapsingToolbarLayout;

public class PostViewActivity extends AppCompatActivity {

    CollapsingToolbarLayout toolbarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_view);
        TextView postText = findViewById(R.id.post_text);
        ImageView image = findViewById(R.id.post_image);
        Toolbar toolbar = findViewById(R.id.toolbar);
        LinearLayout linearLayout = findViewById(R.id.post_discounts);
        toolbarLayout = findViewById(R.id.toolbar_layout);
        setSupportActionBar(toolbar);
        if (getIntent() == null || getIntent().getExtras() == null || getIntent().getExtras().getSerializable(PostData.TABLE_POST_KEY) == null) {
            finish();
            return;
        }
        PostData data = (PostData) getIntent().getExtras().getSerializable(PostData.TABLE_POST_KEY);
        if (data == null) {
            finish();
            return;
        }
        toolbarLayout.setBackgroundColor(data.getColor());
        setTitle(data.getTitle());
        HttpHelper.loadImage(this, data.getImageUrl(), image, null, 500, 250, "post-" + data.getId());
        if (data.getDiscounts() == null) return;
        for (DiscountData discount : data.getDiscounts()) {
            DiscountButton discountBtn = new DiscountButton(this);
            discountBtn.setDiscountCode(discount.getCode());
            linearLayout.addView(discountBtn);
        }

        postText.setText(data.getText());
    }

    public static void startActivity(Context context, PostData data) {
        Intent intent = new Intent(context, PostViewActivity.class);
        intent.putExtra(PostData.TABLE_POST_KEY, data);
        context.startActivity(intent);
    }
}
