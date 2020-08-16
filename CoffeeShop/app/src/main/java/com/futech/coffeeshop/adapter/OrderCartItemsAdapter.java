package com.futech.coffeeshop.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.futech.coffeeshop.R;
import com.futech.coffeeshop.obj.cart.CartData;
import com.futech.coffeeshop.utils.HttpHelper;
import com.futech.coffeeshop.utils.listener.DataChangeListener;
import com.futech.coffeeshop.utils.local_database.CartLocalDatabase;

import java.util.Locale;

public class OrderCartItemsAdapter extends RecyclerView.Adapter<OrderCartItemsAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private final CartData[] items;
    private final boolean showItemName;
    private ListDoneListener listener;

    private boolean canCheck;

    private OrderCartItemsAdapter(Context context, CartData[] items, boolean showItemName) {
        inflater = LayoutInflater.from(context);
        this.items = items;
        this.showItemName = showItemName;
    }

    private OrderCartItemsAdapter(Context context, CartData[] items, boolean showItemName, ListDoneListener listener) {
        inflater = LayoutInflater.from(context);
        this.items = items;
        this.showItemName = showItemName;
        this.listener = listener;
    }

    public static void setAdapter(RecyclerView recyclerView, CartData[] items) {
        setAdapter(recyclerView, items, false);
    }

    public static void setAdapter(RecyclerView recyclerView, CartData[] items, ListDoneListener listener) {
        setAdapter(recyclerView, items, false, listener);
    }

    public static void setAdapter(RecyclerView recyclerView, CartData[] items, boolean showItemName) {
        OrderCartItemsAdapter adapter = new OrderCartItemsAdapter(recyclerView.getContext(), items, showItemName);
        adapter.setForRecycleView(recyclerView);
    }

    public static OrderCartItemsAdapter setAdapter(RecyclerView recyclerView, CartData[] items, boolean showItemName, ListDoneListener listener) {
        OrderCartItemsAdapter adapter = new OrderCartItemsAdapter(recyclerView.getContext(), items, showItemName, listener);
        adapter.setForRecycleView(recyclerView);
        return adapter;
    }

    public void canCheckItems(boolean b) {
        this.canCheck = b;
    }

    private boolean isCanCheckItems() {
        return this.canCheck;
    }

    private void setDoneItems(boolean status, int position) {
        boolean result = true;
        items[position].setStatus(status ? 1 : 0);
        for (CartData b : items) {
            if (!b.getStatus()) {
                result = false;
                break;
            }
        }
        if (result) {
            listener.onDone();
        }else {
            listener.onRemoveDone();
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_order_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.fillData(items[position], position);
    }

    @Override
    public int getItemCount() {
        return items.length;
    }

    private void setForRecycleView(RecyclerView recycleView) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(inflater.getContext());
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        recycleView.setLayoutManager(layoutManager);
        recycleView.setAdapter(this);
    }

    public interface ListDoneListener {
        void onDone();

        void onRemoveDone();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView itemCountText;
        private ImageView itemImage;
        private TextView itemName;
        private View lineSep;
        private ImageView doneCheck;

        private ViewHolder(View view) {
            super(view);
            itemCountText = view.findViewById(R.id.item_count);
            itemImage = view.findViewById(R.id.item_image);
            itemName = view.findViewById(R.id.item_name);
            lineSep = view.findViewById(R.id.line_sep);
            doneCheck = view.findViewById(R.id.done_check);
        }

        private void fillData(final CartData itemData, final int position) {
            itemCountText.setText(String.format(Locale.getDefault(), "%d", itemData.getCount()));
            HttpHelper.loadImage(itemView.getContext(), itemData.getImage(), itemImage, null, 100, 100, itemData.getTitle() + "-item");
            lineSep.setVisibility((position == getItemCount() - 1) ? View.GONE : View.VISIBLE);
            if (showItemName) itemName.setVisibility(View.VISIBLE);
            else {
                itemName.setVisibility(View.GONE);
            }
            itemName.setText(itemData.getTitle());
            setDone(itemData.getStatus());

            if (listener == null) return;
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    doneItem(v, itemData, position);
                }
            });
        }

        private void doneItem(final View v, CartData itemData, final int position) {
            if (!OrderCartItemsAdapter.this.isCanCheckItems()) return;
            CartLocalDatabase db = new CartLocalDatabase(v.getContext());
            db.updateStatus(itemData.getCartId(), !isDone(), new DataChangeListener() {
                @Override
                public void onChange() {
                    setDone(!isDone());
                    OrderCartItemsAdapter.this.setDoneItems(isDone(), position);
                }

                @Override
                public void onError(String msg) {
                    Toast.makeText(v.getContext(), msg, Toast.LENGTH_SHORT).show();
                }
            });
        }


        private void setDone(boolean isCheck) {
            doneCheck.setVisibility(isCheck ? View.VISIBLE : View.GONE);
        }

        private boolean isDone() {
            return doneCheck.getVisibility() == View.VISIBLE;
        }
    }

}
