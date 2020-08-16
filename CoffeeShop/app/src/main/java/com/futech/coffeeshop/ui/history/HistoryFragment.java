package com.futech.coffeeshop.ui.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.futech.coffeeshop.R;
import com.futech.coffeeshop.adapter.OrdersAdapter;
import com.futech.coffeeshop.obj.order.OrderData;
import com.futech.coffeeshop.utils.local_database.OrderLocalDatabase;
import com.google.android.gms.common.util.ArrayUtils;

public class HistoryFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mRefreshLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requireActivity().setTitle(getString(R.string.history_title));
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = view.findViewById(R.id.history_list);
        mRefreshLayout = view.findViewById(R.id.refresh_layout);

        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadHistory();
            }
        });

        loadHistory();
    }

    private void loadHistory() {
        mRefreshLayout.setRefreshing(true);
        OrderLocalDatabase db = new OrderLocalDatabase(getContext());
        db.selectHistory(new OrderLocalDatabase.SelectItemsListener() {
            @Override
            public void onSelect(OrderData[] orders) {
                OrdersAdapter.setHistoryAdapter(mRecyclerView, ArrayUtils.toArrayList(orders));
                mRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onError(String msg) {
                Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                mRefreshLayout.setRefreshing(false);
            }
        });

    }
}
