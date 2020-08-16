package com.futech.coffeeshop.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.futech.coffeeshop.R;
import com.futech.coffeeshop.obj.address.AddressData;
import com.futech.coffeeshop.ui.address.AddAddressActivity;
import com.futech.coffeeshop.utils.local_database.AddressLocalDatabase;

import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {


    private LayoutInflater inflater;

    private final List<AddressData> addressList;

    private AddressAdapter(Context context, List<AddressData> addressList) {
        inflater = LayoutInflater.from(context);
        this.addressList = addressList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.address_view_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.fillData(addressList.get(position));
    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    public static void setRecycleViewAdapter(RecyclerView recycleView, List<AddressData> addressList) {
        AddressAdapter adapter = new AddressAdapter(recycleView.getContext(), addressList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(recycleView.getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recycleView.setLayoutManager(layoutManager);
        recycleView.setAdapter(adapter);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView nameText, addressText;
        private AddressData data;

        private ViewHolder(final View view) {
            super(view);
            nameText = view.findViewById(R.id.address_name);
            addressText = view.findViewById(R.id.address_text);
            ImageButton deleteButton = view.findViewById(R.id.delete_address);
            final Button editButton = view.findViewById(R.id.edit_address);

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AddressLocalDatabase db = new AddressLocalDatabase(v.getContext());
                    db.delete(data.getId(), new AddressLocalDatabase.AddressListener() {
                        @Override
                        public void onChange(AddressData addressData) {
                            addressList.remove(data);
                            AddressAdapter.this.notifyDataSetChanged();
                        }

                        @Override
                        public void onError(String msg) {
                            Toast.makeText(view.getContext(), msg, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), AddAddressActivity.class);
                    intent.putExtra(editButton.getContext().getString(R.string.address_bundle_key), data);
                    v.getContext().startActivity(intent);
                }
            });
        }

        private void fillData(AddressData data) {
            this.data = data;
            nameText.setText(data.getName());
            addressText.setText(data.getAddress());
        }
    }

}
