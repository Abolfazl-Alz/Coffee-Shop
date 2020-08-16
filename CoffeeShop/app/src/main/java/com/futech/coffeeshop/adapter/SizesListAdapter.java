package com.futech.coffeeshop.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.futech.coffeeshop.R;

import java.lang.annotation.Retention;
import java.util.ArrayList;
import java.util.List;

import static java.lang.annotation.RetentionPolicy.SOURCE;

public class SizesListAdapter extends RecyclerView.Adapter<SizesListAdapter.ViewHolder> {

    private List<String> mSizesList;
    private LayoutInflater mInflater;
    private RadioButton mLastRadio;

    public static final int DELETE_MODE = 0;
    static final int SELECT_MODE = 1;
    private int mode;

    private SizesListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        mSizesList = new ArrayList<>();
        mode = SELECT_MODE;
    }

    public static SizesListAdapter newInstance(RecyclerView recyclerView) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(recyclerView.getContext());
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
        SizesListAdapter sizesListAdapter = new SizesListAdapter(recyclerView.getContext());
        recyclerView.setAdapter(sizesListAdapter);
        return sizesListAdapter;
    }

    public void addSize(String size) {
        if (mSizesList.contains(size)) return;
        mSizesList.add(size);
        notifyDataSetChanged();
    }

    public SizesListAdapter addAllSize(String[] sizes) {
        for (String size : sizes) {
            addSize(size);
        }
        return this;
    }

    public String getSelectedSize() {
        if (mLastRadio == null) return "";
        return mLastRadio.getText().toString().trim();
    }

    private void removeSize(int position) {
        mSizesList.remove(position);
        notifyDataSetChanged();
    }

    public String[] getSizesArray() {
        return mSizesList.toArray(new String[0]);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(mInflater.inflate(R.layout.size_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.prepareData(position);
    }

    @Retention(SOURCE)
    @IntDef({DELETE_MODE, SELECT_MODE})
    @interface ClickMode {}

    public void setOnClickMode(@ClickMode int mode) {
        this.mode = mode;
    }

    @Override
    public int getItemCount() {
        return mSizesList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private RadioButton mRadio;
        private int position;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            mRadio = itemView.findViewById(R.id.size_radio);
            mRadio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (SizesListAdapter.this.mode) {
                        case DELETE_MODE:
                            removeSize(position);
                            break;
                        case SELECT_MODE:
                            selectSize();
                            break;
                    }
                }
            });
        }

        private void selectSize() {
            if (mLastRadio != null) mLastRadio.setChecked(false);
            mLastRadio = mRadio;
            mLastRadio.setChecked(true);
        }

        void prepareData(int position) {
            if (SizesListAdapter.this.mode == DELETE_MODE)
                mRadio.setChecked(false);
            this.position = position;
            mRadio.setText(mSizesList.get(position));
        }
    }

}
