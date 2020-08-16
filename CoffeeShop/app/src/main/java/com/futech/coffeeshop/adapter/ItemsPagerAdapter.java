package com.futech.coffeeshop.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.futech.coffeeshop.obj.item.ItemData;
import com.futech.coffeeshop.ui.item_view.ItemViewFragment;

public class ItemsPagerAdapter extends FragmentPagerAdapter {

    private final ItemData[] itemData;

    public ItemsPagerAdapter(@NonNull FragmentManager fm, ItemData[] itemData) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.itemData = itemData;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return ItemViewFragment.newInstance(itemData[position]);
    }

    @Override
    public int getCount() {
        return itemData.length;
    }
}
