package com.futech.coffeeshop.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.futech.coffeeshop.R;
import com.futech.coffeeshop.obj.item.ItemData;
import com.futech.coffeeshop.utils.HttpHelper;

import java.util.List;

public class ItemsViewAdapter extends RecyclerView.Adapter<ItemsViewAdapter.ViewHolder> {

    private List<ItemData> items;
    private final ItemClickListener clickListener;
    private LayoutInflater inflater;

    private ItemsViewAdapter(Context context, List<ItemData> items, ItemClickListener clickListener) {
        inflater = LayoutInflater.from(context);
        this.items = items;
        this.clickListener = clickListener;
    }

    public static ItemsViewAdapter setRecycleViewAdapter(RecyclerView recycleView, List<ItemData> itemDataList, ItemClickListener clickListener) {
        GridLayoutManager layoutManager = new GridLayoutManager(recycleView.getContext(), 3);
        recycleView.setLayoutManager(layoutManager);
        ItemsViewAdapter adapter = new ItemsViewAdapter(recycleView.getContext(), itemDataList, clickListener);
        recycleView.setAdapter(adapter);
        return adapter;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.item_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData(position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItemsData(List<ItemData> itemDataList) {
        this.items.clear();
        this.items.addAll(itemDataList);
    }

    public interface ItemClickListener {
        void onClick(ItemData itemData);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView itemTitle, itemPrice;
        private ImageView itemImage;
        private View layoutReveal;
        private int position;
        private int centerX;
        private int centerY;

        public ViewHolder(View view) {
            super(view);
            itemTitle = view.findViewById(R.id.item_title);
            itemImage = view.findViewById(R.id.item_image);
            itemPrice = view.findViewById(R.id.item_price);
//            layoutReveal = view.findViewById(R.id.layout_reveal);
            itemView.setClickable(true);
            itemView.setOnClickListener(this);
            //todo: write this account name
//            itemView.setOnTouchListener(this);
        }

        void setData(final int position) {
            ItemData itemData = items.get(position);
            itemTitle.setText(itemData.getTitle());
            itemPrice.setText(String.format(itemView.getContext().getString(R.string.price_text_view), Integer.valueOf(itemData.getPrice())));
            HttpHelper.loadImage(itemView.getContext(), itemData.getImage(), itemImage, null, 100, 100, itemData.getTitle());
            this.position = position;
            itemView.setOnClickListener(v -> {
                clickListener.onClick(itemData);

            });
        }

//        @Override
//        public boolean onTouch(View v, MotionEvent event) {
//
//            centerX = (int) event.getX();
//            centerY = (int) event.getY();
//            if (event.getAction() == MotionEvent.ACTION_DOWN) appear();
//            else if (event.getAction() == MotionEvent.ACTION_UP) disappear();
//            else disappear();
//
//            return itemView.performClick();
//        }

        private void appear() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                final float radius = (float) Math.hypot(centerX, centerY);
                Animator animator = ViewAnimationUtils.
                        createCircularReveal(layoutReveal, centerX, centerY, 0, radius);
                animator.setDuration(500);
                layoutReveal.setVisibility(View.VISIBLE);
                animator.start();
            }else {
                layoutReveal.setVisibility(View.VISIBLE);
            }
        }

        private void disappear() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                final float radius = (float) Math.hypot(centerX, centerY);
                Animator animator = ViewAnimationUtils.
                        createCircularReveal(layoutReveal, centerX, centerY, radius, 0);
                animator.setDuration(500);
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animator) {
                        layoutReveal.setVisibility(View.GONE);
                    }
                });
                animator.start();

            }
        }

        @Override
        public void onClick(View v) {

        }
    }
}
