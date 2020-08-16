package com.futech.coffeeshop.ui.post;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.futech.coffeeshop.R;
import com.futech.coffeeshop.adapter.PostsItemAdapter;
import com.futech.coffeeshop.obj.posts.PostData;
import com.futech.coffeeshop.utils.local_database.PostsLocalDatabase;
import com.futech.coffeeshop.utils.listener.SelectListener;

import java.util.Arrays;

public class PostsListViewFragment extends Fragment implements PostsItemAdapter.ClickListener {

    private RecyclerView mPostsList;
    private SwipeRefreshLayout mRefreshLayout;

    private int mPage = 1;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_posts_list_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPostsList = view.findViewById(R.id.posts_list);
        mRefreshLayout = view.findViewById(R.id.refresh_layout);

        mRefreshLayout.setOnRefreshListener(this::refreshList);

        refreshList();
    }

    private void refreshList() {
        PostsLocalDatabase db = new PostsLocalDatabase(getContext());
        mRefreshLayout.setRefreshing(true);
        db.select(mPage, 15, new SelectListener<PostData[]>() {
            @Override
            public void onSelect(PostData[] data, boolean isOnline) {
                PostsItemAdapter.setAdapter(mPostsList, Arrays.asList(data), PostsListViewFragment.this);
                mRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onError(String msg) {
                Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                mRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onClick(PostData postData) {
        PostViewActivity.startActivity(getContext(), postData);
    }
}
