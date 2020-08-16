package com.futech.coffeeshop.adapter;

import android.content.Context;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.futech.coffeeshop.R;
import com.futech.coffeeshop.obj.cart.CartData;
import com.futech.coffeeshop.utils.HttpHelper;
import com.futech.coffeeshop.utils.local_database.CartLocalDatabase;
import com.futech.coffeeshop.utils.listener.DataChangeListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.ViewHolder> {


    private final List<CartData> items;
    private final LayoutInflater inflater;
    private ViewHolder.UpdateListener listener;

    public CartItemAdapter(Context context, List<CartData> items) {
        this.items = items;
        this.inflater = LayoutInflater.from(context);
    }

    public void setItems(List<CartData> cartDataList) {
        this.items.clear();
        this.items.addAll(cartDataList);
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.cart_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.fillData(items.get(position));
        holder.addUpdateListener(listener);
    }

    public void setOnListener(ViewHolder.UpdateListener listener) {
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final Context context;
        private TextView itemTitle, totalPrice, itemPrice, itemCount, sizeText, discountedPriceText;
        private ImageView itemImage;
        private CartData cartData;

        List<UpdateListener> listeners = new ArrayList<>();

        void addUpdateListener(UpdateListener listener) {
            listeners.add(listener);
        }

        void callUpdate() {
            for (UpdateListener listener : listeners) {
                listener.onUpdate(itemView);
            }
        }

        ViewHolder(View view) {
            super(view);
            context = view.getContext();
            itemTitle = view.findViewById(R.id.item_name);
            itemCount = view.findViewById(R.id.item_count);
            totalPrice = view.findViewById(R.id.total_price);
            itemPrice = view.findViewById(R.id.item_price);
            itemImage = view.findViewById(R.id.item_image);
            sizeText = view.findViewById(R.id.size_text);
            discountedPriceText = view.findViewById(R.id.discounted_price);

            Button increaseBtn = view.findViewById(R.id.increase_count);
            Button decreaseBtn = view.findViewById(R.id.decrease_count);

            increaseBtn.setOnClickListener(this);
            decreaseBtn.setOnClickListener(this);
        }

        void fillData(CartData cartData) {
            this.cartData = cartData;
            itemTitle.setText(cartData.getTitle());
            itemPrice.setText(String.format(Locale.getDefault(), context.getString(R.string.once_price), Integer.valueOf(cartData.getPrice())));
            itemCount.setText(String.format(Locale.getDefault(), "%d", cartData.getCount()));
            sizeText.setText(cartData.getSizes());
            if (cartData.getDiscount() != null && cartData.getDiscount().getValue() > 0) {
                discountedPriceText.setVisibility(View.VISIBLE);
                int discountedPrice = Integer.parseInt(cartData.getPrice()) * cartData.getDiscount().getValue() / 100;
                discountedPriceText.setText(String.format(Locale.getDefault(), context.getString(R.string.discounted_price), discountedPrice));
            }

            HttpHelper.loadImage(itemView.getContext(), cartData.getImage(), itemImage, null, 205, 250, cartData.getTitle() + "-item");
            updateTotalPrice();
        }

        @Override
        public void onClick(final View v) {
            int count = cartData.getCount();
            final int firstCount = count;
            if (v.getId() == R.id.increase_count) {
                count++;
                cartData.setCount(count);
            }else if (v.getId() == R.id.decrease_count && cartData.getCount() > 1) {
                count--;
                cartData.setCount(count);
            }

            updateTotalPrice();
            CartLocalDatabase db = new CartLocalDatabase(itemView.getContext());

            final int finalCount = count;

            db.update(cartData.getCartId(), cartData, new DataChangeListener() {
                @Override
                public void onChange() {
                    itemCount.setText(String.format(Locale.getDefault(), "%d", finalCount));
                    callUpdate();
                }

                @Override
                public void onError(String msg) {
                    Toast.makeText(v.getContext(), msg, Toast.LENGTH_SHORT).show();
                    cartData.setCount(firstCount);
                    callUpdate();
                }
            });
        }

        private void updateTotalPrice() {
            totalPrice.setText(String.format(Locale.getDefault(), context.getString(R.string.total_price), (cartData.getDiscountedPrice() * cartData.getCount())));
        }

        public int getId() {
            return cartData.getId();
        }

        public interface UpdateListener {
            void onUpdate(View view);
        }
    }

}
