package com.futech.coffeeshop.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.futech.coffeeshop.R;
import com.futech.coffeeshop.obj.posts.PostData;

import java.util.List;

public class PostsItemAdapter extends RecyclerView.Adapter<PostsItemAdapter.ViewHolder> {

    private List<PostData> posts;
    private LayoutInflater mLayoutInflater;
    private ClickListener clickListener;

    private PostsItemAdapter(Context context, List<PostData> posts) {
        this.posts = posts;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(mLayoutInflater.inflate(R.layout.post_item_view, parent, false));
    }

    public static void setAdapter(RecyclerView recyclerView, List<PostData> posts, ClickListener listener) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(recyclerView.getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        PostsItemAdapter adapter = new PostsItemAdapter(recyclerView.getContext(), posts);
        recyclerView.setAdapter(adapter);
        adapter.setOnClickListener(listener);
    }

    private void setOnClickListener(ClickListener listener) {
        this.clickListener = listener;
    }

    public interface ClickListener {
        void onClick(PostData postData);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.prepareView(position);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mWriter;
        private TextView mTitle;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            mWriter = itemView.findViewById(R.id.post_writer);
            mTitle = itemView.findViewById(R.id.post_title);
        }

        void prepareView(int position) {
            PostData post = posts.get(position);
            mWriter.setText(post.getWriter().getFullName());
            mTitle.setText(post.getTitle());
            itemView.setOnClickListener(v -> {
                clickListener.onClick(post);
            });
        }
    }

}
