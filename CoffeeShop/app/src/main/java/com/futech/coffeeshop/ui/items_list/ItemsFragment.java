package com.futech.coffeeshop.ui.items_list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.futech.coffeeshop.R;
import com.futech.coffeeshop.adapter.ItemsViewAdapter;
import com.futech.coffeeshop.obj.item.ItemData;

import java.lang.annotation.Retention;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.lang.annotation.RetentionPolicy.SOURCE;

public class ItemsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private List<ItemData> items;
    private RefreshItemsListener listener;
    private ItemsViewAdapter.ItemClickListener clickListener;

    private SwipeRefreshLayout refreshLayout;
    private RecyclerView itemsListView;
    private ItemsViewAdapter adapter;

    static final int SORT_BY_EXPENSIVE = 0;
    static final int SORT_BY_SALES = 1;
    static final int SORT_BY_CHEAPEST = 2;
    static final int SORT_BY_NEW_ITEM = 3;

    ItemsFragment() {
        this.items = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_items, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        itemsListView = view.findViewById(R.id.items_list);
        refreshLayout = view.findViewById(R.id.refresh_layout);

        refreshLayout.setOnRefreshListener(this);
    }

    //Sort modes and states
    @Retention(SOURCE)
    @IntDef({SORT_BY_CHEAPEST, SORT_BY_EXPENSIVE, SORT_BY_NEW_ITEM, SORT_BY_SALES})
    public @interface SortMode {}

    public void setSortMode(@SortMode int sortMode) {
        if (this.items == null || adapter == null) return;
        this.items = sortItems(sortMode, this.items);
        adapter.setItemsData(this.items);
        adapter.notifyDataSetChanged();
    }

    private List<ItemData> sortItems(@SortMode int sort, List<ItemData> list) {
        List<SortItem> li;
        li = SortItem.getItems(list, sort);
        Collections.sort(li);
        return SortItem.convertToItems(li);
    }

    public void setItems(List<ItemData> items) {
        setRefresh(false);
        this.items = items;
        adapter = ItemsViewAdapter.setRecycleViewAdapter(itemsListView, items, clickListener);
    }

    private void setRefresh(boolean status) {
        refreshLayout.setRefreshing(status);
    }

    @Override
    public void onRefresh() {
        setRefresh(true);
        listener.onRefresh();
    }

    public interface RefreshItemsListener {
        void onRefresh();
    }

    void setRefreshListener(RefreshItemsListener listener) {
        this.listener = listener;
    }

    void setClickListener(ItemsViewAdapter.ItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public static class SortItem implements Comparable<SortItem> {

        private final ItemData val;
        private final int sortType;

        SortItem(ItemData val, int sortType) {
            this.val = val;
            this.sortType = sortType;
        }

        public static List<SortItem> getItems(List<ItemData> items, int sortType) {
            List<SortItem> itemsSort = new ArrayList<>();
            for (ItemData item : items) {
                SortItem sort = new SortItem(item, sortType);
                itemsSort.add(sort);
            }
            return itemsSort;
        }

        @Override
        public int compareTo(@androidx.annotation.NonNull SortItem itemData) {
            switch (sortType) {
                case SORT_BY_EXPENSIVE:
                    return Float.compare(Float.parseFloat(itemData.val.getPrice()), Float.parseFloat(val.getPrice()));
                case SORT_BY_CHEAPEST:
                    return Float.compare(Float.parseFloat(val.getPrice()), Float.parseFloat(itemData.val.getPrice()));
                case SORT_BY_SALES:
                    return Integer.compare(itemData.val.getSalesNumber(), val.getSalesNumber());
                default:
                    if (itemData.val.getDateCreate() == null || val.getDateCreate() == null)
                        return Integer.compare(val.getSalesNumber(), itemData.val.getSalesNumber());
                    return itemData.val.getDateCreate().compareTo(val.getDateCreate());
            }
        }

        static List<ItemData> convertToItems(List<SortItem> list) {
            List<ItemData> items = new ArrayList<>();
            for (SortItem sort : list) {
                items.add(sort.val);
            }
            return items;
        }
    }
}
