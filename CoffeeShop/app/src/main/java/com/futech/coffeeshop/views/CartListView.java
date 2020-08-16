package com.futech.coffeeshop.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.futech.coffeeshop.R;
import com.futech.coffeeshop.adapter.CartItemAdapter;
import com.futech.coffeeshop.obj.cart.CartData;
import com.futech.coffeeshop.utils.local_database.CartLocalDatabase;
import com.futech.coffeeshop.utils.listener.DataChangeListener;

import java.util.ArrayList;
import java.util.List;

public class CartListView extends RecyclerView {

    List<CartData> carts = new ArrayList<>();
    CartItemAdapter adapter;
    CartLocalDatabase db;

    List<OnChanged> listener = new ArrayList<>();

    public CartListView(@NonNull Context context) {
        super(context);
        initializeView(context);
    }

    public CartListView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initializeView(context);
    }

    public CartListView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeView(context);
    }

    private void initializeView(Context context) {

        adapter = new CartItemAdapter(context, carts);
        adapter.setOnListener(view -> onCartChanged());

        db = new CartLocalDatabase(context);

        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(
                    @NonNull RecyclerView recyclerView,
                    @NonNull ViewHolder viewHolder, @NonNull ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(ViewHolder viewHolder, int direction) {
                CartData cartData = carts.get(viewHolder.getAdapterPosition());
                carts.remove(viewHolder.getAdapterPosition());
                adapter.notifyDataSetChanged();
                int cartId = cartData.getCartId();
                db.delete(cartId, new DataChangeListener() {
                    @Override
                    public void onChange() {
                        Toast.makeText(getContext(), R.string.item_deleted_from_cart, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(String msg) {
                        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };
        new ItemTouchHelper(callback).attachToRecyclerView(this);

        LinearLayoutManager layout = new LinearLayoutManager(getContext());
        layout.setOrientation(RecyclerView.VERTICAL);
        setLayoutManager(layout);
        setAdapter(adapter);
        refreshList();

    }

    public void refreshList() {

        adapter.notifyDataSetChanged();
    }

    private void onCartChanged() {
        callListener();
    }

    public void addItemToCart(CartData cart) {
        carts.add(cart);
        adapter.notifyDataSetChanged();
        callListener();
    }

    public void addItemsToCart(List<CartData> carts) {
        for (CartData cartData : carts) {
            addItemToCart(cartData);
        }
    }

    public void removeItemsInCart() {
        carts.clear();
        callListener();
        adapter.notifyDataSetChanged();
    }

    private void callListener() {
        for (OnChanged onChanged : listener) {
            onChanged.dataOnChanged();
        }
    }

    public void addOnChangedListener(OnChanged onChanged) {
        listener.add(onChanged);
    }

    public float getTotalPrice() {
        float price = 0;
        for (CartData cart : carts) {
            price += cart.getDiscountedPrice() * cart.getCount();
        }

        return price;
    }


    public interface OnChanged {
        void dataOnChanged();
    }
}
