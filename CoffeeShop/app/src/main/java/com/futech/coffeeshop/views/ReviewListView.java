package com.futech.coffeeshop.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.futech.coffeeshop.R;
import com.futech.coffeeshop.adapter.ReviewAdapter;
import com.futech.coffeeshop.obj.review.ReviewData;
import com.futech.coffeeshop.utils.local_database.ReviewLocalDatabase;

import java.util.ArrayList;
import java.util.List;

public class ReviewListView extends RecyclerView {

    public void setReviews(List<ReviewData> reviews) {
        this.reviews.clear();
        this.reviews.addAll(reviews);
        adapter.notifyDataSetChanged();
    }

    private List<ReviewData> reviews = new ArrayList<>();
    private int itemId = -1;
    private int count = 10;
    private ReviewAdapter adapter;

    public ReviewListView(@NonNull Context context, List<ReviewData> reviews) {
        super(context);
        this.reviews = reviews;
        initializeView();
        setBackgroundColor(0x00000000);
    }

    public ReviewListView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initializeView();
        initializeAttribute(attrs);
    }

    private void initializeAttribute(AttributeSet attrs) {
        TypedArray type = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.ReviewListView, 0, 0);
        try {
            setItemId(type.getInt(R.styleable.ReviewListView_itemId, -1));
            setCount(type.getInt(R.styleable.ReviewListView_count, 10));
        } finally {
            type.recycle();
        }
    }

    public ReviewListView(
            @NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeView();
        initializeAttribute(attrs);
    }

    private void initializeView() {
        LinearLayoutManager layout = new LinearLayoutManager(getContext());
        layout.setOrientation(VERTICAL);
        setLayoutManager(layout);
        adapter = new ReviewAdapter(getContext(), reviews);
        setAdapter(adapter);
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
        if (itemId > 0) {
            refreshList();
        }
    }

    public void refresh() {
        refreshList();
    }

    private void refreshList() {
        ReviewLocalDatabase db = new ReviewLocalDatabase(getContext());
        int page = 1;
        db.selectReview(page, 3, getItemId(), new ReviewLocalDatabase.SelectListener() {
            @Override
            public void onSelect(List<ReviewData> reviewData, boolean isOnline) {
                if (isOnline)
                    setReviews(reviewData);
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
        if (count > 0) refreshList();
    }
}
