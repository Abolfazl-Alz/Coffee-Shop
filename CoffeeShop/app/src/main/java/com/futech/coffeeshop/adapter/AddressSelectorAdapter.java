package com.futech.coffeeshop.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.futech.coffeeshop.R;
import com.futech.coffeeshop.obj.address.AddressData;
import com.futech.coffeeshop.ui.address.AddAddressActivity;

import java.util.List;

public class AddressSelectorAdapter extends RecyclerView.Adapter<AddressSelectorAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private final Context context;
    private final List<AddressData> addressList;
    private boolean isFirst = true;

    private RadioButton lastCheckRadio;
    private int indexSelected = -1;

    private AddressSelectorAdapter(Context context, List<AddressData> addressList) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.addressList = addressList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.address_select_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.fillData(position);
    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    public static AddressSelectorAdapter setRecycleViewAdapter(RecyclerView recycleView, List<AddressData> addressList) {
        AddressSelectorAdapter adapter = new AddressSelectorAdapter(recycleView.getContext(), addressList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(recycleView.getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recycleView.setLayoutManager(layoutManager);
        recycleView.setAdapter(adapter);
        return adapter;
    }

    public AddressData getSelectedAddress() {
        return addressList.get(indexSelected);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        RadioButton radioButton;
        TextView addressName, addressInformation, userName, userPhone;
        View lineSep;
        private AddressData addressData;
        private int selectedIndex;

        ViewHolder(@NonNull final View itemView) {
            super(itemView);
            radioButton = itemView.findViewById(R.id.radio_address);
            addressName = itemView.findViewById(R.id.address_name);
            addressInformation = itemView.findViewById(R.id.address_text);
            userName = itemView.findViewById(R.id.name_text);
            userPhone = itemView.findViewById(R.id.user_phone);
            lineSep = itemView.findViewById(R.id.line_sep);
            Button editAddressBtn = itemView.findViewById(R.id.edit_address);
            radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        if (lastCheckRadio != null) lastCheckRadio.setChecked(false);
                        lastCheckRadio = radioButton;
                        indexSelected = selectedIndex;
                        indexSelected = addressList.indexOf(addressData);
                        itemView.startAnimation(AnimationUtils.loadAnimation(itemView.getContext(), R.anim.fade_in));
                    }
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    radioButton.setChecked(true);
                }
            });

            if (isFirst) {
                radioButton.setChecked(true);
                indexSelected = selectedIndex;
                isFirst = false;
            }

            editAddressBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), AddAddressActivity.class);
                    intent.putExtra("address", addressData);
                    v.getContext().startActivity(intent);
                }
            });
        }

        void fillData(int index) {
            this.selectedIndex = index;
            AddressData data = addressList.get(index);
            boolean isLast = index == getItemCount() - 1;
            userName.setText(data.getUserName());
            addressName.setText(data.getName());
            userPhone.setText(data.getUserPhone());
            addressInformation.setText(data.getAddress());
            this.addressData = data;
            lineSep.setBackgroundColor(isLast ? ContextCompat.getColor(context, android.R.color.transparent) : ContextCompat.getColor(context, R.color.borderColor));
        }
    }
}
