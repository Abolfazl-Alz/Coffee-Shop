package com.futech.coffeeshop.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.futech.coffeeshop.R;
import com.futech.coffeeshop.obj.order.OrderData;
import com.futech.coffeeshop.utils.NotificationDatabase;
import com.futech.coffeeshop.views.PriceTextView;

import java.util.List;
import java.util.Locale;

import static com.futech.coffeeshop.obj.notification.NotificationData.NotificationStatus.ORDER_ACCEPT_STATUS;
import static com.futech.coffeeshop.obj.notification.NotificationData.NotificationStatus.ORDER_DELIVER_STATUS;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.ViewHolder> {

    private List<OrderData> mOrders;
    private LayoutInflater mLayoutInflater;
    private final boolean mHistory;
    private ItemClickListener listener;

    public static OrdersAdapter setOrderAdapter(RecyclerView recyclerView, List<OrderData> orders) {
        return setAdapter(recyclerView, orders, false);
    }

    public static void setHistoryAdapter(RecyclerView recyclerView, List<OrderData> orders) {
        setAdapter(recyclerView, orders, true);
    }

    private static OrdersAdapter setAdapter(RecyclerView recyclerView, List<OrderData> orders, boolean history) {
        OrdersAdapter adapter = new OrdersAdapter(recyclerView.getContext(), orders, history);
        LinearLayoutManager layoutManager = new LinearLayoutManager(recyclerView.getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        return adapter;
    }

    public void setOnItemClickListener(ItemClickListener listener) {

        this.listener = listener;
    }

    private OrdersAdapter(Context context, List<OrderData> orders, boolean history) {
        this.mOrders = orders;
        mLayoutInflater = LayoutInflater.from(context);
        this.mHistory = history;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(mLayoutInflater.inflate(R.layout.order_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.fillData(mOrders.get(position));
    }

    @Override
    public int getItemCount() {
        return mOrders.size();
    }

    public interface ItemClickListener {
        void onClick(OrderData orderData);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView customerName, itemCount, addressText, deliveryTypeText, orderStatus;
        private final PriceTextView itemPrice;
        private final View view;
        private final LinearLayout addressLayout;
        private Button acceptOrder, completeOrder;
        private ImageButton messageBtn, callBtn;

        ViewHolder(View view) {
            super(view);
            customerName = view.findViewById(R.id.customer_name);
            itemCount = view.findViewById(R.id.items_count);
            addressText = view.findViewById(R.id.address_text);
            messageBtn = view.findViewById(R.id.message_user_btn);
            callBtn = view.findViewById(R.id.call_user_btn);
            acceptOrder = view.findViewById(R.id.accept_order);
            addressLayout = view.findViewById(R.id.address_layout);
            deliveryTypeText = view.findViewById(R.id.delivery_type_text);
            itemPrice = view.findViewById(R.id.items_price);
            orderStatus = view.findViewById(R.id.order_status);
            completeOrder = view.findViewById(R.id.complete_btn);
            LinearLayout adminOrderPanel = view.findViewById(R.id.admin_order_control);
            LinearLayout historyOrderPanel = view.findViewById(R.id.history_order_control);
            this.view = view;

            adminOrderPanel.setVisibility(mHistory ? View.GONE : View.VISIBLE);
            historyOrderPanel.setVisibility(mHistory ? View.VISIBLE : View.GONE);
        }

        void fillData(final OrderData orderData) {

            orderStatus.setText(orderData.getStatusText());
            customerName.setText(orderData.getRegisterData().getFullName());
            int len = 0;
            if (orderData.getCarts() != null) len = orderData.getCarts().length;
            itemCount.setText(String.format(Locale.getDefault(), "%d", len));
            if (orderData.getAddress() != null)
                addressText.setText(orderData.getAddress().getAddress());

            itemPrice.setPriceText(orderData.getPrice());

            final boolean isAddress = orderData.getAddress() == null || orderData.getAddress().getId() == -1;
            addressLayout.setVisibility(isAddress ? View.GONE : View.VISIBLE);
            deliveryTypeText.setText(isAddress ? R.string.come_to_coffee_shop : R.string.delivery_to_address);


            acceptOrder.setEnabled(!orderData.isAccept());
            completeOrder.setEnabled(orderData.getStatus() != ORDER_DELIVER_STATUS);

            messageBtn.setOnClickListener(v -> {
                Uri uri = Uri.parse("smsto:" + orderData.getRegisterData().getPhoneNumber());
                Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
                intent.putExtra("sms_body", v.getContext().getString(R.string.msg_prepare_order));
                v.getContext().startActivity(intent);
            });

            callBtn.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", orderData.getRegisterData().getPhoneNumber(), null));
                v.getContext().startActivity(intent);
            });

            view.setOnClickListener(v -> {
                if (OrdersAdapter.this.listener == null) return;
                OrdersAdapter.this.listener.onClick(orderData);
            });

            acceptOrder.setTag(orderData);
            acceptOrder.setOnClickListener(this);
            completeOrder.setTag(orderData);
            completeOrder.setOnClickListener(this);
        }

        private void sendOrderStatus(final View v, int status, OrderData orderData) {
            NotificationDatabase db = new NotificationDatabase(v.getContext());
            db.sendOrderStatusChanging(orderData.getRegisterData().getId(), status, orderData.getId(), new NotificationDatabase.NotificationListener() {
                @Override
                public void onReceive() {
                    Toast.makeText(v.getContext(), R.string.notification_has_sent, Toast.LENGTH_SHORT).show();
                    v.setEnabled(false);
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(v.getContext(), error, Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void onClick(View v) {
            int notification;
            switch (v.getId()) {
                case R.id.complete_btn:
                    notification = ORDER_DELIVER_STATUS;
                    break;
                case R.id.accept_order:
                    notification = ORDER_ACCEPT_STATUS;
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + v.getId());
            }
            sendOrderStatus(v, notification, (OrderData) v.getTag());
        }
    }

}
