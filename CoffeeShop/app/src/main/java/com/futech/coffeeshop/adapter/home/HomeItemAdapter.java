package com.futech.coffeeshop.adapter.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.futech.coffeeshop.R;
import com.futech.coffeeshop.obj.home.CollectionItemData;
import com.futech.coffeeshop.utils.HttpHelper;

import java.util.Arrays;
import java.util.List;

public class HomeItemAdapter extends RecyclerView.Adapter<HomeItemAdapter.ViewHolder> {

    private final Context context;
    private final List<CollectionItemData> mItems;
    private final LayoutInflater inflater;
    private final Listener listener;

    private HomeItemAdapter(Context context, CollectionItemData[] items, Listener listener) {
        this.context = context;
        this.mItems = Arrays.asList(items);
        this.inflater = LayoutInflater.from(context);
        this.listener = listener;
    }

    public static void newInstance(RecyclerView recyclerView, CollectionItemData[] items, Listener listener) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(recyclerView.getContext());
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
        HomeItemAdapter adapter = new HomeItemAdapter(recyclerView.getContext(), items, listener);
        recyclerView.setAdapter(adapter);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.home_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.prepare(position);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public interface Listener {
        void onClick(CollectionItemData data);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView itemTitle, itemPrice, itemPriceDiscount, itemDiscount;
        ImageView itemImage;
        View sepLine;

        public ViewHolder(@NonNull View view) {
            super(view);
            itemTitle = view.findViewById(R.id.item_title);
            itemPrice = view.findViewById(R.id.item_price);
            itemPriceDiscount = view.findViewById(R.id.item_price_discount);
            itemImage = view.findViewById(R.id.item_image);
            sepLine = view.findViewById(R.id.line_sep);
            itemDiscount = view.findViewById(R.id.item_discount);
        }

        void prepare(final int position) {
            final CollectionItemData data = mItems.get(position);
            itemTitle.setText(data.getTitle());
            String price = String.valueOf(data.getPrice());
            if (price.endsWith(".0")) {
                price = price.substring(0, price.length() - 2);
            }

            itemPrice.setText(String.format(itemView.getContext().getString(R.string.price_text_view), Integer.valueOf(price)));
            HttpHelper.loadImage(context, data.getImage(), itemImage, null, 250, 250, data.getTitle());
            if (position == mItems.size() - 1) {
                sepLine.setVisibility(View.GONE);
            }
            itemView.setOnClickListener(v -> listener.onClick(data));
            if (data.getDiscount() != null && data.getDiscount().getValue() > 0) {
                itemDiscount.setVisibility(View.VISIBLE);
                itemDiscount.setText(String.valueOf(data.getDiscount().getValue()));
                itemDiscount.append("%");
                itemPrice.setBackgroundResource(R.drawable.strike_through);
                int color = ResourcesCompat.getColor(context.getResources(), android.R.color.holo_red_dark, context.getTheme());
                itemPrice.setTextColor(color);
                itemPriceDiscount.setVisibility(View.VISIBLE);
                int priceDiscount = data.getPrice() - (data.getPrice() * data.getDiscount().getValue() / 100);
                itemPriceDiscount.setText(String.format(itemView.getContext().getString(R.string.price_text_view), priceDiscount));
            }
        }
    }

}
