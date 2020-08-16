package com.futech.coffeeshop.ui.items_list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.futech.coffeeshop.R;
import com.futech.coffeeshop.adapter.CollectionFragmentAdapter;
import com.futech.coffeeshop.adapter.ItemsViewAdapter;
import com.futech.coffeeshop.obj.category.CategoryData;
import com.futech.coffeeshop.obj.item.ItemData;
import com.futech.coffeeshop.ui.item_view.ItemViewFragment;
import com.futech.coffeeshop.ui.main_activity.MainActivity;
import com.futech.coffeeshop.utils.local_database.CollectionLocalDatabase;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textview.MaterialTextView;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

public class ItemsListViewFragment extends Fragment implements ItemsViewAdapter.ItemClickListener {

    private final ItemsViewAdapter.ItemClickListener clickListener;
    private SlidingUpPanelLayout sliding;
    private Spinner spinner;
    private CollectionFragmentAdapter collectionAdapter;

    public ItemsListViewFragment() {
        clickListener = this;
    }

    ItemsListViewFragment(ItemsViewAdapter.ItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_items_list_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sliding = view.findViewById(R.id.sliding_layout);
        spinner = view.findViewById(R.id.sort_selector);
        ViewPager viewPager = view.findViewById(R.id.pager);
        TabLayout tabLayout = view.findViewById(R.id.tab_layout);

        CollectionLocalDatabase db = new CollectionLocalDatabase(view.getContext());
        db.selectAllCollection(new CollectionLocalDatabase.SelectListener() {
            @Override
            public void onSelect(CategoryData[] items, boolean isOnline) {
                collectionAdapter = new CollectionFragmentAdapter(getParentFragmentManager(), items, clickListener);
            }

            @Override
            public void onError(String error) {

            }
        });
        viewPager.setAdapter(collectionAdapter);
        tabLayout.setupWithViewPager(viewPager);

        if (getActivity() != null && getActivity() instanceof MainActivity) {
            final MainActivity activity = (MainActivity) getActivity();
            activity.setTitle(R.string.items_menu);
            activity.setToolbarColor(view.getContext().getResources().getColor(android.R.color.white));
        }

        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(view.getContext(), R.array.sort_items, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                collectionAdapter.setSortMode(getSortType());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private int getSortType() {
        String text = "";
        if (spinner.getSelectedView() != null && spinner.getSelectedView() instanceof MaterialTextView)
            text = ((TextView) spinner.getSelectedView()).getText().toString();
        if (text.equals(getString(R.string.sort_by_new_items))) {
            return ItemsFragment.SORT_BY_NEW_ITEM;
        }else if (text.equals(getString(R.string.sort_most_cheapest))) {
            return ItemsFragment.SORT_BY_CHEAPEST;
        }else if (text.equals(getString(R.string.sort_most_expensive))) {
            return ItemsFragment.SORT_BY_EXPENSIVE;
        }else if (text.equals(getString(R.string.sort_order_items))) {
            return ItemsFragment.SORT_BY_SALES;
        }else {
            return ItemsFragment.SORT_BY_NEW_ITEM;
        }
    }

    @Override
    public void onClick(ItemData itemData) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.item_view, ItemViewFragment.newInstance(itemData));
        transaction.commit();
        sliding.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
    }
}