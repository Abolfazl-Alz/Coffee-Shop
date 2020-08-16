package com.futech.coffeeshop.ui.categories_list;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.futech.coffeeshop.R;
import com.futech.coffeeshop.adapter.MiniCategoryItemAdapter;
import com.futech.coffeeshop.obj.category.CategoryData;
import com.futech.coffeeshop.utils.local_database.CollectionLocalDatabase;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Arrays;

public class CategorySelectorActivity extends AppCompatActivity implements View.OnClickListener, MiniCategoryItemAdapter.CategorySelectListener {

    private RecyclerView categoriesList;
    private SwipeRefreshLayout refresh;
    private MiniCategoryItemAdapter adapter;
    private FloatingActionButton submitBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_selector);

        if (getSupportActionBar() != null) {
            int color = ResourcesCompat.getColor(getResources(), android.R.color.white, getTheme());
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(color));
            setTitle(R.string.CategorySelector);
        }

        categoriesList = findViewById(R.id.categories_list);
        refresh = findViewById(R.id.refresh_layout);
        submitBtn = findViewById(R.id.submit_button);
        submitBtn.setOnClickListener(this);

        refresh.setOnRefreshListener(this::loadCategories);
        loadCategories();
    }

    private void loadCategories() {
        CollectionLocalDatabase db = new CollectionLocalDatabase(this);
        refresh.setRefreshing(true);

        db.selectAllCollection(new CollectionLocalDatabase.SelectListener() {
            @Override
            public void onSelect(CategoryData[] items, boolean isOnline) {
                if (!isOnline) return;
                adapter = MiniCategoryItemAdapter.setAdapter(categoriesList, Arrays.asList(items), CategorySelectorActivity.this);
                refresh.setRefreshing(false);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(CategorySelectorActivity.this, error, Toast.LENGTH_SHORT).show();
                refresh.setRefreshing(false);
            }
        });
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        intent.putExtra(CategoryData.TABLE_NAME_KEY, adapter.getSelectedCategory());
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    public void onSelect(CategoryData selectedCategory) {
        submitBtn.setEnabled(true);
    }
}
