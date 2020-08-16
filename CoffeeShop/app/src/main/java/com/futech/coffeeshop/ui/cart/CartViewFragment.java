package com.futech.coffeeshop.ui.cart;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.futech.coffeeshop.R;
import com.futech.coffeeshop.obj.cart.CartData;
import com.futech.coffeeshop.ui.main_activity.MainActivity;
import com.futech.coffeeshop.ui.payment.PaymentActivity;
import com.futech.coffeeshop.utils.local_database.CartLocalDatabase;
import com.futech.coffeeshop.utils.listener.DataChangeListener;
import com.futech.coffeeshop.utils.listener.SelectListener;
import com.futech.coffeeshop.views.CartListView;

import java.util.List;

public class CartViewFragment extends Fragment {

    private CartLocalDatabase db;
    private TextView totalPriceText;
    private CartListView cartList;
    private Button doneButton;
    private SwipeRefreshLayout refreshLayout;

    private static final int PAYMENT_FINISH = 142;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart_view, container, false);
        cartList = view.findViewById(R.id.cart_list);
        doneButton = view.findViewById(R.id.payment_btn);
        refreshLayout = view.findViewById(R.id.refresh_layout);

        Button deleteAll = view.findViewById(R.id.delete_item);
        totalPriceText = view.findViewById(R.id.total_price);

        db = new CartLocalDatabase(getContext());

        cartList.addItemsToCart(db.selectAll());

        refreshItemsInCart();

        deleteAll.setOnClickListener(v -> db.deleteAll(new DataChangeListener() {
            @Override
            public void onChange() {
                refreshItemsInCart();
            }

            @Override
            public void onError(String msg) {
                Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
            }
        }));

        return view;
    }

    private void refreshItemsInCart() {
        refreshLayout.setRefreshing(true);
        doneButton.setEnabled(false);
        db.select(new SelectListener<CartData[]>() {
            @Override
            public void onSelect(CartData[] carts, boolean isOnline) {
                List<CartData> select = db.selectAll();
                cartList.removeItemsInCart();
                cartList.addItemsToCart(select);
                doneButton.setEnabled(select.size() > 0);
                refreshLayout.setRefreshing(false);
            }

            @Override
            public void onError(String msg) {
                refreshLayout.setRefreshing(false);
                Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
        cartList.refreshList();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        doneButton.setOnClickListener(v -> {
            if (cartList.getChildCount() == 0) {
                Toast.makeText(getContext(), R.string.cart_is_empty_msg, Toast.LENGTH_SHORT).show();
                return;
            }
            startActivityForResult(new Intent(getContext(), PaymentActivity.class), PAYMENT_FINISH);
        });

        refreshLayout.setOnRefreshListener(this::refreshItemsInCart);

        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity == null) return;
        mainActivity.setTitle(getString(R.string.cart_header));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getContext() != null) {
            mainActivity.setToolbarColor(getContext().getColor(R.color.home_background));
        }else {
            mainActivity.setToolbarColor(0xEDEDED);
        }


        cartList.addOnChangedListener(() -> setTotalPrice((int) cartList.getTotalPrice()));
    }

    private void setTotalPrice(int totalPrice) {
        if (totalPrice == 0) {
            totalPriceText.setText(R.string.cart_empty);
        }else {
            if (getContext() == null) return;
            String price = (getContext()).getString(R.string.total_price);
            totalPriceText.setText(String.format(price, totalPrice));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PAYMENT_FINISH && resultCode == Activity.RESULT_OK) {
            refreshItemsInCart();
        }
    }
}
