package com.futech.coffeeshop.adapter.home;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.futech.coffeeshop.obj.card.CardData;
import com.futech.coffeeshop.obj.home.HomeData;

import java.util.List;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder> {

    private final Context context;
    private HomeData feeds;

    private final int IMAGE_VIEW = 0;
    private final int RECYCLER_VIEW = 1;
    private final int BREAK_LINE = 2;

    private FeedAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case IMAGE_VIEW:
                return new ViewHolder(new ImageView(context));
            case RECYCLER_VIEW:
                return new ViewHolder(new RecyclerView(context));
            case BREAK_LINE:
                return new ViewHolder(new View(context));
        }
        return new ViewHolder(new View(context));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.initView(position);
    }

    @Override
    public int getItemViewType(int position) {
        if (feeds.getItem(position) instanceof CardData) {
            return IMAGE_VIEW;
        }else if (feeds.getItem(position) instanceof List) {
            return RECYCLER_VIEW;
        }

        return BREAK_LINE;
    }

    @Override
    public int getItemCount() {
        return feeds.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        void initView(int index) {
//            Object obj = feeds.get(index);
        }


    }

}
