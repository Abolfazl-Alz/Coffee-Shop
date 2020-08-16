package com.futech.coffeeshop.ui.items_pager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.futech.coffeeshop.R;
import com.futech.coffeeshop.adapter.ItemsPagerAdapter;
import com.futech.coffeeshop.obj.item.ItemData;
import com.futech.coffeeshop.utils.transformations.ZoomOutTransformation;
import com.futech.coffeeshop.utils.local_database.ItemLocalDatabase;
import com.futech.coffeeshop.utils.listener.SelectListener;

import java.util.Objects;

public class ItemsPager extends AppCompatActivity {

    private final String TAG = "ItemsListActivity";

    private ViewPager itemsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items_pager);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        itemsList = findViewById(R.id.items_list);

        TextView categoryText = findViewById(R.id.category_text);

        Intent intent = getIntent();
        final int collection = Objects.requireNonNull(intent.getExtras()).getInt("collection");
        String collectionName = Objects.requireNonNull(intent.getExtras()).getString("collectionTitle");

        categoryText.setText(collectionName);

        ItemLocalDatabase db = new ItemLocalDatabase(this);
        db.select(collection, new SelectListener<ItemData[]>() {
            @Override
            public void onSelect(ItemData[] items, boolean online) {
                setAdapter(items);
            }

            @Override
            public void onError(String message) {
                Toast.makeText(ItemsPager.this, message, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setAdapter(ItemData[] api) {
        ItemsPagerAdapter items = new ItemsPagerAdapter(getSupportFragmentManager(), api);
        itemsList.setPageTransformer(true, new ZoomOutTransformation());
        itemsList.setAdapter(items);
    }
}
