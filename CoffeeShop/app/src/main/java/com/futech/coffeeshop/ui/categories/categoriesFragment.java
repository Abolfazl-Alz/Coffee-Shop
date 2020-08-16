package com.futech.coffeeshop.ui.categories;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.futech.coffeeshop.ui.main_activity.MainActivity;
import com.futech.coffeeshop.R;
import com.futech.coffeeshop.adapter.CategoriesAdapter;
import com.futech.coffeeshop.obj.category.CategoryData;
import com.futech.coffeeshop.utils.Internet;
import com.futech.coffeeshop.utils.local_database.CollectionLocalDatabase;

public class categoriesFragment extends Fragment {

    private RecyclerView collectionsList;
    private SwipeRefreshLayout refreshLayout;

    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_categories, container, false);
        collectionsList = root.findViewById(R.id.home_collections_list);
        refreshLayout = root.findViewById(R.id.refresh_layout);
        requireActivity().setTitle(R.string.categories_menu);
        refreshList(root.getContext());

        refreshLayout.setOnRefreshListener(() -> refreshList(root.getContext()));

        return root;
    }

    private void loadCollections() {
        CollectionLocalDatabase db = new CollectionLocalDatabase(this.getContext());
        db.selectAllCollection(new CollectionLocalDatabase.SelectListener() {
            @Override
            public void onSelect(CategoryData[] items, boolean isOnline) {
                setAdapter(items);
                refreshLayout.setRefreshing(false);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                refreshLayout.setRefreshing(false);
            }
        });
    }

    private void refreshList(final Context context) {
        if (!Internet.isConnected(context)) {
            Toast.makeText(context, "Please connect your internet to refresh", Toast.LENGTH_SHORT).show();
            refreshLayout.setRefreshing(false);
            return;
        }
        loadCollections();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainActivity mainActivity = (MainActivity) getActivity();
        assert mainActivity != null;
        mainActivity.getToolbar().setBackgroundResource(R.drawable.toolbar_transparent);
    }

    private void setAdapter(CategoryData[] categoryData) {
        CategoriesAdapter categoriesAdapter = new CategoriesAdapter(getContext(), categoryData);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        collectionsList.setLayoutManager(layoutManager);
        collectionsList.setAdapter(categoriesAdapter);
    }
}
