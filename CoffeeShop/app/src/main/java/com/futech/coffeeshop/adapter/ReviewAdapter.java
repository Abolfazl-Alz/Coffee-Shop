package com.futech.coffeeshop.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.futech.coffeeshop.R;
import com.futech.coffeeshop.obj.review.ReviewData;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    private final LayoutInflater inflater;
    private final List<ReviewData> reviewList;

    public ReviewAdapter(@NonNull Context context, List<ReviewData> reviewList) {
        inflater = LayoutInflater.from(context);
        this.reviewList = reviewList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.review_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.fillData(reviewList.get(position));
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView usernameText, messageText;
        RatingBar ratingBar;

        ViewHolder(@NonNull View view) {
            super(view);
            usernameText = view.findViewById(R.id.phone_number);
            messageText = view.findViewById(R.id.reviewText);
            ratingBar = view.findViewById(R.id.rating);
        }

        void fillData(ReviewData reviewData) {
            usernameText.setText(reviewData.getRegisterData().getFullName());
            messageText.setText(reviewData.getText());
            ratingBar.setRating(reviewData.getRate());
        }

    }


}
