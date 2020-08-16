package com.futech.coffeeshop.ui.admin.order;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.futech.coffeeshop.R;
import com.futech.coffeeshop.adapter.OrderCartItemsAdapter;
import com.futech.coffeeshop.adapter.OrdersAdapter;
import com.futech.coffeeshop.obj.cart.CartData;
import com.futech.coffeeshop.obj.order.OrderData;
import com.futech.coffeeshop.utils.NotificationDatabase;
import com.futech.coffeeshop.utils.local_database.OrderLocalDatabase;
import com.google.android.gms.common.util.ArrayUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import static com.futech.coffeeshop.obj.notification.NotificationData.NotificationStatus.ORDER_ACCEPT_STATUS;
import static com.futech.coffeeshop.obj.notification.NotificationData.NotificationStatus.ORDER_ARRIVED_STATUS;
import static com.futech.coffeeshop.obj.notification.NotificationData.NotificationStatus.ORDER_DEFAULT_STATUS;
import static com.futech.coffeeshop.obj.notification.NotificationData.NotificationStatus.ORDER_READY_STATUS;
import static com.futech.coffeeshop.obj.notification.NotificationData.NotificationStatus.ORDER_SENT_STATUS;

public class OrderFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener {

    private RecyclerView mOrderList;
    private SwipeRefreshLayout mRefreshLayout;
    private RecyclerView mCartItems;
    private SlidingUpPanelLayout mSlidingLayout;
    private TextView mCustomerName;
    private GoogleMap googleMap;
    private ConstraintLayout mMapLayout;

    private Button mStatusBtn;
    private ButtonStatus mButtonStatus;

    //last item selected information
    private String mLatitude, mLongitude;
    private int mId;
    private int mStatus;
    private OrderData orderData;


    private OrdersAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_orders, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mOrderList = view.findViewById(R.id.order_list);
        mRefreshLayout = view.findViewById(R.id.refresh_layout);
        mCartItems = view.findViewById(R.id.cart_list);
        mSlidingLayout = view.findViewById(R.id.sliding_layout);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshList();
            }
        });
        mCustomerName = view.findViewById(R.id.customer_name);
        mMapLayout = view.findViewById(R.id.map_layout);
        mStatusBtn = view.findViewById(R.id.order_change_status);
        FloatingActionButton locationShareBtn = view.findViewById(R.id.share_btn);
        refreshList();

        mButtonStatus = new ButtonStatus();


        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) mapFragment.getMapAsync(this);
        else Toast.makeText(getContext(), "unable to load map", Toast.LENGTH_SHORT).show();

        locationShareBtn.setOnClickListener(this);
        mStatusBtn.setOnClickListener(this);
    }

    private void refreshList() {
        final OrderLocalDatabase db = new OrderLocalDatabase(getContext());
        mRefreshLayout.setRefreshing(true);
        db.selectDeliveredItems(new OrderLocalDatabase.SelectItemsListener() {
            @Override
            public void onSelect(OrderData[] orders) {
                adapter = OrdersAdapter.setOrderAdapter(mOrderList, ArrayUtils.toArrayList(orders));
                adapter.setOnItemClickListener(new OrdersAdapter.ItemClickListener() {

                    @Override
                    public void onClick(final OrderData orderData) {
                        setSelectedItem(orderData);
                    }
                });
                mRefreshLayout.setRefreshing(false);
                for (OrderData order : orders) {
                    db.markAsView(order.getId());
                }
            }

            @Override
            public void onError(String msg) {
                Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                mRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void setSelectedItem(final OrderData orderData) {

        OrderFragment.this.orderData = orderData;
        mId = orderData.getId();
        mStatus = orderData.getStatus();
        if (orderData.getAddress() != null) {
            mMapLayout.setVisibility(View.VISIBLE);
            goToLocation(new LatLng(orderData.getAddress().getLat(), orderData.getAddress().getLng()));
            mLatitude = String.valueOf(orderData.getAddress().getLat());
            mLongitude = String.valueOf(orderData.getAddress().getLng());
        }else {
            mMapLayout.setVisibility(View.GONE);
        }
        if (orderData.getRegisterData() != null) {
            mCustomerName.setText(orderData.getRegisterData().getFullName());
        }
        if (orderData.getCarts() != null) {
            final OrderCartItemsAdapter adapter = OrderCartItemsAdapter.setAdapter(mCartItems, orderData.getCarts(), true, new OrderCartItemsAdapter.ListDoneListener() {
                @Override
                public void onDone() {
                    Toast.makeText(getContext(), "it's done", Toast.LENGTH_SHORT).show();
                    changeOrderStatus(ORDER_READY_STATUS, orderData);
                }

                @Override
                public void onRemoveDone() {
                    changeOrderStatus(ORDER_ACCEPT_STATUS, orderData);
                }
            });
            adapter.canCheckItems(orderData.getStatus() > ORDER_DEFAULT_STATUS);
        }
        mSlidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
        mButtonStatus.updateBtn(orderData.getStatus() + 1);
    }

    private void changeOrderStatus(final int status, final OrderData orderData) {
        NotificationDatabase.newInstance().sendOrderStatusChanging(orderData.getRegisterData().getId(), status, orderData.getId(), new NotificationDatabase.NotificationListener() {
            @Override
            public void onReceive() {
                Toast.makeText(getContext(), R.string.notification_sent, Toast.LENGTH_SHORT).show();
                orderData.setStatus(status);
                setSelectedItem(orderData);
                if (orderData.getStatus() >= ORDER_READY_STATUS) {
                    for (CartData cart : orderData.getCarts()) {
                        cart.setStatus(1);
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }

    private void goToLocation(LatLng latLng) {
        googleMap.clear();
        final MarkerOptions marker = new MarkerOptions().position(latLng);
        googleMap.addMarker(marker);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
    }

    @Override
    public void onClick(View v) {

        //share location
        switch (v.getId()) {
            case R.id.share_btn:
                String uri = "geo:" + mLatitude + "," + mLongitude + "?q=" + mLatitude + "," + mLongitude;
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uri)));
                break;
            case R.id.order_change_status:

                mStatus = orderData.getStatus();
                mButtonStatus.updateBtn(orderData.getStatus() + 1);

                if (mId > 0) {
                    if (mStatus < ORDER_ARRIVED_STATUS) mStatus += 1;
                    changeOrderStatus(mStatus, orderData);
                }
                break;
        }
    }

    private class ButtonStatus {

        void updateBtn(int status) {
            int text;
            int drawable;
            mStatusBtn.setEnabled(true);

            if (status == ORDER_ACCEPT_STATUS) {
                text = R.string.order_accept_btn;
                drawable = R.drawable.ic_done;
            }else if (status == ORDER_READY_STATUS) {
                text = R.string.order_ready_btn;
                drawable = R.drawable.ic_done;
            }else if (status == ORDER_SENT_STATUS) {
                text = R.string.order_sent_btn;
                drawable = R.drawable.ic_send;
            }else if (status >= ORDER_ARRIVED_STATUS) {
                text = R.string.order_arrived_btn;
                drawable = R.drawable.ic_location_on;
            }else {
                mStatusBtn.setEnabled(false);
                return;
            }

            mStatusBtn.setText(text);
            if (getActivity() == null) return;
            final Drawable icon = ResourcesCompat.getDrawable(getResources(), drawable, getActivity().getTheme());
            assert icon != null;
            icon.setBounds(0, 0, 0, 0);
            mStatusBtn.setCompoundDrawablesRelative(icon, null, null, null);
        }

    }
}
