package com.futech.coffeeshop.ui.admin.discount;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.futech.coffeeshop.R;
import com.futech.coffeeshop.adapter.DiscountListViewAdapter;
import com.futech.coffeeshop.obj.discount.DiscountData;
import com.futech.coffeeshop.utils.DiscountHelper;
import com.futech.coffeeshop.utils.listener.SelectListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class DiscountListFragment extends Fragment implements SelectListener<DiscountData[]>, DiscountListViewAdapter.SelectItemListener {

    private RecyclerView mDiscountList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.discount_list_fragment, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mDiscountList = view.findViewById(R.id.discount_list);
        FloatingActionButton mInsertItemButton = view.findViewById(R.id.insert_item);
        DiscountHelper discount = new DiscountHelper(view.getContext());
        discount.getDiscountList(this);

        if (getActivity() != null && getActivity() instanceof DiscountControlActivity) {
            ((DiscountControlActivity) getActivity()).setActionBarColor(R.color.home_background);
        }

        mInsertItemButton.setOnClickListener(v -> NavHostFragment.findNavController(DiscountListFragment.this).navigate(R.id.action_ListDiscountFragment_to_InsertDiscountFragment));
    }

    @Override
    public void onSelect(DiscountData[] dataList, boolean isOnline) {
        DiscountListViewAdapter adapter = DiscountListViewAdapter.setAdapter(mDiscountList, new ArrayList<>());
        for (DiscountData data : dataList) {
            adapter.addDiscountToList(data);
        }
        adapter.setSelectListener(this);
    }

    @Override
    public void onError(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSelectDiscountItem(DiscountData data) {
        Toast.makeText(getContext(), data.getTitle(), Toast.LENGTH_SHORT).show();
    }
}
