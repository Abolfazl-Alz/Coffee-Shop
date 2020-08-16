package com.futech.coffeeshop.ui.items_list;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.futech.coffeeshop.R;
import com.futech.coffeeshop.adapter.ItemsViewAdapter;
import com.futech.coffeeshop.obj.item.ItemData;

public class ItemPickerActivity extends AppCompatActivity implements ItemsViewAdapter.ItemClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_picker);
        setTitle(getString(R.string.select_item_header));

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.items_list, new ItemsListViewFragment(this));
        transaction.commit();
    }

    @Override
    public void onClick(ItemData itemData) {
        Intent intent = new Intent();
        intent.putExtra(ItemData.TABLE_NAME_KEY, itemData);
        setResult(RESULT_OK, intent);
        finish();
    }
}
