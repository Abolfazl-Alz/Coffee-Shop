package com.futech.coffeeshop.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.futech.coffeeshop.R;
import com.futech.coffeeshop.obj.category.CategoryData;
import com.futech.coffeeshop.ui.items_pager.ItemsPager;
import com.futech.coffeeshop.utils.HttpHelper;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.ViewHolder> {

    private CategoryData[] categoryDataList;
    private LayoutInflater inflater;
    private final String TAG = "HomeAdapter";

    public CategoriesAdapter(Context context, CategoryData[] categoryDataList) {
         inflater = LayoutInflater.from(context);
        this.categoryDataList = categoryDataList;
    }

    @NonNull
    @Override
    public CategoriesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CategoriesAdapter.ViewHolder(inflater.inflate(R.layout.category_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriesAdapter.ViewHolder holder, int position) {
        holder.fillData(categoryDataList[position]);
    }

    @Override
    public int getItemCount() {
        return categoryDataList.length;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final Context context;
        TextView collectionName;
        TextView informationText;
        ImageView imageView;
        private final String TAG = "HomeAdapter.ViewHolder";
        private View view;

        ViewHolder(View view) {
            super(view);
            this.view = view;
            context = view.getContext();
            collectionName = view.findViewById(R.id.title_text);
            informationText = view.findViewById(R.id.collection_information);
            imageView = view.findViewById(R.id.category_icon);
        }

        void fillData(final CategoryData categoryData) {
            collectionName.setText(categoryData.getName());
            informationText.setText(categoryData.getInformation());
            categoryData.setImage(categoryData.getImage());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ItemsPager.class);
                    intent.putExtra("collection", categoryData.getId());
                    intent.putExtra("collectionTitle", categoryData.getName());
                    context.startActivity(intent);
                }
            });

            int id = categoryData.getId();
            HttpHelper.loadImage(context,
                    categoryData.getImage(),
                    imageView, null,
                    500, 500,
                    categoryData.getName());
        }
    }
}
