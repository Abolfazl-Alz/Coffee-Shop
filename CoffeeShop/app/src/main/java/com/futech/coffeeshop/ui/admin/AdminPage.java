package com.futech.coffeeshop.ui.admin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.futech.coffeeshop.obj.category.CategoryData;
import com.futech.coffeeshop.obj.item.ItemData;
import com.futech.coffeeshop.ui.admin.discount.DiscountControlActivity;
import com.futech.coffeeshop.ui.categories_list.CategorySelectorActivity;
import com.futech.coffeeshop.ui.items_list.ItemPickerActivity;
import com.futech.coffeeshop.ui.main_activity.MainActivity;
import com.futech.coffeeshop.R;
import com.futech.coffeeshop.utils.Internet;

public class AdminPage extends Fragment {

    private static final int ITEM_PICKER_REQUEST = 100;
    private static final int CATEGORY_PICKER_REQUEST = 200;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button addNewItem = view.findViewById(R.id.add_new_item);
        Button editItem = view.findViewById(R.id.edit_item);
        Button addNewCategory = view.findViewById(R.id.add_new_category);
        Button editCategory = view.findViewById(R.id.edit_category);
        Button discountCode = view.findViewById(R.id.discount_code_btn);

        requireActivity().setTitle(getString(R.string.admin_menu));
        if (getActivity() != null && getActivity().getActionBar() != null && getActivity() instanceof MainActivity) {
            MainActivity activity = (MainActivity) getActivity();
            activity.setToolbarIcon(R.drawable.ic_user);
        }

        addNewItem.setOnClickListener(v -> {
            if (!Internet.isConnected(v.getContext())) {
                Toast.makeText(v.getContext(), R.string.check_internet, Toast.LENGTH_SHORT).show();
                return;
            }
            startActivity(new Intent(v.getContext(), ItemEditor.class));
        });

        editItem.setOnClickListener(v -> {
            if (!Internet.isConnected(v.getContext())) {
                Toast.makeText(v.getContext(), R.string.check_internet, Toast.LENGTH_SHORT).show();
                return;
            }
            startActivityForResult(new Intent(v.getContext(), ItemPickerActivity.class), ITEM_PICKER_REQUEST);
        });

        addNewCategory.setOnClickListener(v -> {
            if (!Internet.isConnected(v.getContext())) {
                Toast.makeText(v.getContext(), R.string.check_internet, Toast.LENGTH_SHORT).show();
                return;
            }
            startActivity(new Intent(v.getContext(), CategoryEditor.class));
        });

        editCategory.setOnClickListener(v -> {
            if (!Internet.isConnected(v.getContext())) {
                Toast.makeText(v.getContext(), R.string.check_internet, Toast.LENGTH_SHORT).show();
                return;
            }
            startActivityForResult(new Intent(v.getContext(), CategorySelectorActivity.class), CATEGORY_PICKER_REQUEST);
        });

        discountCode.setOnClickListener(v -> {
            if (!Internet.isConnected(v.getContext())) {
                Toast.makeText(v.getContext(), R.string.check_internet, Toast.LENGTH_SHORT).show();
                return;
            }
            startActivity(new Intent(v.getContext(), DiscountControlActivity.class));
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ITEM_PICKER_REQUEST:
                if (resultCode != Activity.RESULT_OK || getContext() == null || data == null || data.getExtras() == null || !(data.getExtras().getSerializable(ItemData.TABLE_NAME_KEY) instanceof ItemData))
                    return;
                data.setClass(getContext(), ItemEditor.class);
                startActivity(data);
                break;
            case CATEGORY_PICKER_REQUEST:
                if (resultCode != Activity.RESULT_OK || getContext() == null || data == null || data.getExtras() == null || !(data.getExtras().getSerializable(CategoryData.TABLE_NAME_KEY) instanceof CategoryData))
                    return;
                data.setClass(getContext(), CategoryEditor.class);
                startActivity(data);
                break;
        }
    }
}
