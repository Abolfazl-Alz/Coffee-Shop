package com.futech.coffeeshop.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.futech.coffeeshop.R;
import com.futech.coffeeshop.obj.category.CategoryData;
import com.futech.coffeeshop.utils.HttpHelper;

import java.util.List;

public class MiniCategoryItemAdapter extends RecyclerView.Adapter<MiniCategoryItemAdapter.ViewHolder> {

    private List<CategoryData> mCategories;
    private LayoutInflater mInflater;
    private RadioButton mLastRadio;
    private int mLastCheckIndex;
    private CategorySelectListener mListener;

    private MiniCategoryItemAdapter(Context context, List<CategoryData> categories) {
        mInflater = LayoutInflater.from(context);
        this.mCategories = categories;
    }

    public static MiniCategoryItemAdapter setAdapter(RecyclerView recyclerView, List<CategoryData> categories, CategorySelectListener listener) {
        GridLayoutManager layoutManager = new GridLayoutManager(recyclerView.getContext(), 3);
        MiniCategoryItemAdapter adapter = new MiniCategoryItemAdapter(recyclerView.getContext(), categories);
        adapter.setOnCategorySelectListener(listener);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        return adapter;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(mInflater.inflate(R.layout.mini_category_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData(position);
    }

    public CategoryData getSelectedCategory() {
        return mCategories.get(mLastCheckIndex);
    }

    private void setOnCategorySelectListener(CategorySelectListener listener) {
        this.mListener = listener;
    }

    @Override
    public int getItemCount() {
        return mCategories.size();
    }

    public interface CategorySelectListener {
        void onSelect(CategoryData selectedCategory);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mNameView;
        private ImageView mIconView;
        private RadioButton mSelectorView;

        ViewHolder(View view) {
            super(view);
            mNameView = view.findViewById(R.id.category_title);
            mIconView = view.findViewById(R.id.category_icon);
            mSelectorView = view.findViewById(R.id.category_selector);
        }

        void setData(int index) {
            CategoryData data = mCategories.get(index);
            if (data == null) return;
            mNameView.setText(data.getName());
            HttpHelper.loadImage(itemView.getContext(), data.getImage(), mIconView, null, 90, 90, "category-" + data.getName());
            mSelectorView.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) checkItem(index);
            });
            itemView.setOnClickListener(v -> mSelectorView.setChecked(true));
        }

        private void checkItem(int index) {
            if (mLastRadio != null) mLastRadio.setChecked(false);
            MiniCategoryItemAdapter.this.mLastRadio = this.mSelectorView;
            mSelectorView.setChecked(true);
            mLastCheckIndex = index;
            CategoryData selectedCategory = mCategories.get(index);
            if (mListener != null) mListener.onSelect(selectedCategory);
        }
    }

}
