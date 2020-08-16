package com.futech.coffeeshop.adapter.home;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.futech.coffeeshop.R;
import com.futech.coffeeshop.obj.home.CollectionData;

import java.util.List;

public class HomeCollectionAdapter extends RecyclerView.Adapter<HomeCollectionAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    private final List<CollectionData> collections;
    private final HomeItemAdapter.Listener listener;


    private HomeCollectionAdapter(Context context, List<CollectionData> collections, HomeItemAdapter.Listener listener) {
        mInflater = LayoutInflater.from(context);
        this.collections = collections;
        this.listener = listener;
    }

    public static HomeCollectionAdapter newInstance(RecyclerView recyclerView, List<CollectionData> collections, HomeItemAdapter.Listener listener) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(recyclerView.getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        HomeCollectionAdapter adapter = new HomeCollectionAdapter(recyclerView.getContext(), collections, listener);
        recyclerView.setAdapter(adapter);
        return adapter;
    }

    public void addItem(CollectionData data) {
        collections.add(data);
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(mInflater.inflate(R.layout.home_collection, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.prepareData(position);
    }

    @Override
    public int getItemCount() {
        return collections.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView collectionTitle, collectionDescription;
        private RecyclerView collectionItems;
        private View sepLine;

        public ViewHolder(@NonNull View view) {
            super(view);
            collectionTitle = view.findViewById(R.id.collection_title);
            collectionDescription = view.findViewById(R.id.collection_description);
            collectionItems = view.findViewById(R.id.collection_items);
            sepLine = view.findViewById(R.id.line_sep);
        }

        void prepareData(final int position) {
            final CollectionData collectionData = collections.get(position);
            collectionTitle.setText(collectionData.getTitle());
            try {
                itemView.setBackgroundColor(Color.parseColor(collectionData.getColor()));
            } catch (Exception ex) {
                //
            }
            collectionDescription.setText(collectionData.getDescription());
            HomeItemAdapter.newInstance(collectionItems, collectionData.getItems(), listener);
            if (position == collections.size() - 1) {
                sepLine.setVisibility(View.GONE);
            }
        }
    }

}
