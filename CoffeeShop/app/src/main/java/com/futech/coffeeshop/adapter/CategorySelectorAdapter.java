package com.futech.coffeeshop.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.futech.coffeeshop.R;
import com.futech.coffeeshop.obj.category.CategoryData;
import com.futech.coffeeshop.utils.HttpHelper;
import com.futech.coffeeshop.utils.local_database.CollectionLocalDatabase;

public class CategorySelectorAdapter extends RecyclerView.Adapter<CategorySelectorAdapter.ViewHolder> {


    private LayoutInflater mInflater;
    private final Context context;
    private final CategoryData[] collectionList;
    private RadioButton lastRadio;
    private SelectListener listener;
    private int selectedId;


    private CategorySelectorAdapter(Context context, CategoryData[] collectionList) {
        mInflater = LayoutInflater.from(context);
        this.context = context;
        this.collectionList = collectionList;
    }

    private void setOnSelectListener(SelectListener listener) {
        this.listener = listener;
    }

    public static void newInstance(final RecyclerView recyclerView, final int selectedId, final SelectListener listener) {
        CollectionLocalDatabase db = new CollectionLocalDatabase(recyclerView.getContext());
        db.selectAllCollection(new CollectionLocalDatabase.SelectListener() {
            @Override
            public void onSelect(CategoryData[] items, boolean isOnline) {
                CategorySelectorAdapter adapter = new CategorySelectorAdapter(recyclerView.getContext(), items);
                adapter.setOnSelectListener(listener);
                adapter.selectedId = selectedId;
                LinearLayoutManager layoutManager = new LinearLayoutManager(recyclerView.getContext());
                layoutManager.setOrientation(RecyclerView.VERTICAL);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(recyclerView.getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(mInflater.inflate(R.layout.category_select_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.prepare(position);
    }

    @Override
    public int getItemCount() {
        return collectionList.length;
    }

    public interface SelectListener {
        void onSelect(CategoryData collection);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView title, information;
        ImageView image;
        RadioButton radio;
        private int position;

        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.category_icon);
            title = itemView.findViewById(R.id.category_title);
            information = itemView.findViewById(R.id.category_text);
            radio = itemView.findViewById(R.id.item_check);
            radio.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        void prepare(int position) {
            CategoryData data = collectionList[position];
            if (data.getId() == selectedId) {
                radio.setChecked(true);
            }
            title.setText(data.getName());
            information.setText(data.getInformation());
            HttpHelper.loadImage(context, data.getImage(), image, null, 250, 250, data.getName());
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            radio.setChecked(true);
            if (lastRadio != null) lastRadio.setChecked(false);
            lastRadio = radio;
            itemView.startAnimation(AnimationUtils.loadAnimation(itemView.getContext(), R.anim.fade_in));
            listener.onSelect(collectionList[position]);
        }
    }
}
