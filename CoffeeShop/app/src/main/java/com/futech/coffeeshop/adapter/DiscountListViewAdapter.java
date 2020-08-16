package com.futech.coffeeshop.adapter;

import android.content.Context;
import android.os.Build;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.futech.coffeeshop.R;
import com.futech.coffeeshop.obj.discount.DiscountData;
import com.futech.coffeeshop.utils.DiscountHelper;
import com.futech.coffeeshop.utils.RegisterControl;
import com.futech.coffeeshop.utils.listener.DataChangeListener;

import java.util.List;
import java.util.Locale;

public class DiscountListViewAdapter extends RecyclerView.Adapter<DiscountListViewAdapter.ViewHolder> {

    private final List<DiscountData> mDiscountData;
    private LayoutInflater mInflater;
    private SelectItemListener mSelectListener;

    private DiscountListViewAdapter(Context context, List<DiscountData> discountData) {
        mInflater = LayoutInflater.from(context);
        mDiscountData = discountData;
    }

    public static DiscountListViewAdapter setAdapter(RecyclerView recyclerView, List<DiscountData> discountDataList) {
        DiscountListViewAdapter adapter = new DiscountListViewAdapter(recyclerView.getContext(), discountDataList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(recyclerView.getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        return adapter;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(mInflater.inflate(R.layout.discount_list_view_item, parent, false));
    }

    private void removeDiscountFromList(int index) {
        mDiscountData.remove(index);
        notifyItemRemoved(index);
    }

    public void addDiscountToList(DiscountData discountData) {
        mDiscountData.add(discountData);
        notifyDataSetChanged();
    }

    public boolean isCodeExist(String code) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            return mDiscountData.stream().anyMatch(x -> x.getCode().equals(code));
        for (DiscountData data : mDiscountData)
            if (data.getCode().equals(code)) return true;
        return false;
    }

    public void setSelectListener(SelectItemListener listener) {
        this.mSelectListener = listener;
    }

    public interface SelectItemListener {
        void onSelectDiscountItem(DiscountData data);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.prepareView(position);
    }

    @Override
    public int getItemCount() {
        return mDiscountData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {

        private TextView discountName;
        private TextView discountCode;
        private TextView discountValue;
        private View lineSep;
        private int index;

        ViewHolder(View view) {
            super(view);
            discountName = view.findViewById(R.id.discount_name);
            discountCode = view.findViewById(R.id.discount_code);
            discountValue = view.findViewById(R.id.discount_value);
            lineSep = view.findViewById(R.id.line_sep);
            itemView.setOnCreateContextMenuListener(this);
        }

        void prepareView(int index) {
            this.index = index;
            DiscountData data = mDiscountData.get(index);
            discountName.setText(data.getTitle());
            discountCode.setText(data.getCode());
            discountValue.setText(String.format(Locale.getDefault(), "%%%d", data.getValue()));
            if (mDiscountData.size() - 1 == index) lineSep.setVisibility(View.GONE);

            itemView.setOnClickListener(v -> {
                if (mSelectListener == null) return;
                mSelectListener.onSelectDiscountItem(data);
            });

            itemView.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {
            itemView.showContextMenu();
            return true;
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            MenuItem deleteMenu = menu.add(R.string.delete_menu_item);
            deleteMenu.setOnMenuItemClickListener(this);

            if (RegisterControl.isAdmin(itemView.getContext())) {
                MenuItem editMenu = menu.add(R.string.edit_menu_item);
                editMenu.setOnMenuItemClickListener(this);
            }
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            final Context context = itemView.getContext();
            final String delete = context.getString(R.string.delete_menu_item);

            if (item.getTitle() == delete) {
                DiscountData discountData = mDiscountData.get(index);
                deleteItem(index, discountData.getId());
            }else if (item.getTitle() == context.getString(R.string.edit_menu_item)) {
                editItem();
            }else {
                return false;
            }

            return true;
        }

        private void deleteItem(int index, int id) {
            DiscountHelper helper = new DiscountHelper(itemView.getContext());
            helper.deleteDiscount(id, new DataChangeListener() {
                @Override
                public void onChange() {
                    DiscountListViewAdapter.this.removeDiscountFromList(index);
                    Toast.makeText(itemView.getContext(), R.string.discount_deleted_msg, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String msg) {
                    Toast.makeText(itemView.getContext(), msg, Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void editItem() {
        }
    }
}
