package com.futech.coffeeshop.ui.admin.discount;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.ColorRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;

import com.futech.coffeeshop.R;

public class DiscountControlActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discount_control);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void setActionBarColor(@ColorRes int colorResource) {
        int color = ResourcesCompat.getColor(getResources(), colorResource, getTheme());
        if (getSupportActionBar() != null) {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(color));
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                Color c = Color.valueOf(color);
                float averageColor = (c.red() + c.blue() + c.green()) / 3;
                if (averageColor > (127.5)) {
                    //Light
                    mToolbar.setTitleTextColor(getResources().getColor(android.R.color.black, getTheme()));
                }else {
                    //Dark
                    mToolbar.setTitleTextColor(getResources().getColor(android.R.color.white, getTheme()));
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

}
