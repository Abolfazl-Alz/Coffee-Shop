package com.futech.coffeeshop.ui.items_list;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.futech.coffeeshop.adapter.ItemsViewAdapter;
import com.futech.coffeeshop.obj.item.ItemData;
import com.futech.coffeeshop.utils.local_database.ItemLocalDatabase;
import com.futech.coffeeshop.utils.listener.SelectListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CollectionItemsFragment extends ItemsFragment {

    private final int collection;
    private final ItemsViewAdapter.ItemClickListener clickListener;
    private List<ItemData> items;

    public CollectionItemsFragment(int collection, ItemsViewAdapter.ItemClickListener clickListener) {
        super();
        this.clickListener = clickListener;
        items = new ArrayList<>();
        this.collection = collection;
        super.setRefreshListener(this::loadDataList);
        super.setClickListener(clickListener);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadDataList();
    }

    private void loadDataList() {
        ItemLocalDatabase db = new ItemLocalDatabase(getContext());
        db.select(collection, new SelectListener<ItemData[]>() {
            @Override
            public void onSelect(ItemData[] data, boolean isOnline) {
                setItemsList(data);
                CollectionItemsFragment.super.setItems(getItemsList());
            }

            @Override
            public void onError(String msg) {
                if (getContext() != null)
                    Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private List<ItemData> getItemsList() {
        return this.items;
    }

    private void setItemsList(ItemData[] items) {
        this.items.clear();
        this.items.addAll(Arrays.asList(items));
    }
}
