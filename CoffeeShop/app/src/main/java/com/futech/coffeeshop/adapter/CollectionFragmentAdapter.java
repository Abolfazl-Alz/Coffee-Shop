package com.futech.coffeeshop.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.futech.coffeeshop.obj.category.CategoryData;
import com.futech.coffeeshop.ui.items_list.CollectionItemsFragment;
import com.futech.coffeeshop.ui.items_list.ItemsFragment;

public class CollectionFragmentAdapter extends FragmentStatePagerAdapter {

    private final CategoryData[] categories;
    private final ItemsViewAdapter.ItemClickListener clickListener;
    private int sortMode = 0;
    private CollectionItemsFragment lastFragment;

    public CollectionFragmentAdapter(@NonNull
                                             FragmentManager fm, CategoryData[] categories, ItemsViewAdapter.ItemClickListener clickListener) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.categories = categories;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public CollectionItemsFragment getItem(int position) {
        lastFragment = new CollectionItemsFragment(categories[position].getId(), clickListener);
        lastFragment.setSortMode(sortMode);
        return lastFragment;
    }

    @Override
    public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        super.setPrimaryItem(container, position, object);
        lastFragment = (CollectionItemsFragment) object;
        lastFragment.setSortMode(sortMode);
    }

    public void setSortMode(@ItemsFragment.SortMode int sortMode) {
        this.sortMode = sortMode;
        if (lastFragment != null) lastFragment.setSortMode(sortMode);
    }

    @Override
    public int getCount() {
        return categories.length;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return categories[position].getName();
    }
}
