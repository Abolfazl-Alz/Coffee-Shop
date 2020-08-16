package com.futech.coffeeshop.ui.address;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.futech.coffeeshop.R;
import com.futech.coffeeshop.adapter.AddressSelectorAdapter;
import com.futech.coffeeshop.obj.address.AddressData;
import com.futech.coffeeshop.utils.RegisterControl;
import com.futech.coffeeshop.utils.local_database.AddressLocalDatabase;
import com.google.android.gms.common.util.ArrayUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AddressPicker extends AppCompatActivity {

    private RecyclerView addressListView;
    private SwipeRefreshLayout refreshLayout;
    private AddressSelectorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_picker);

        addressListView = findViewById(R.id.address_list_view);
        refreshLayout = findViewById(R.id.refresh_layout);
        final LinearLayout addAddress = findViewById(R.id.add_address);
        FloatingActionButton doneButton = findViewById(R.id.button_done);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xffffffff));
            getSupportActionBar().setTitle(R.string.select_address_title);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        refreshList();

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshList();
            }
        });

        addAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddressPicker.this, AddAddressActivity.class));
            }
        });

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adapter == null) return;
                Intent intent = new Intent();
                intent.putExtra("address", adapter.getSelectedAddress());
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void refreshList() {
        AddressLocalDatabase db = new AddressLocalDatabase(this);
        AddressSelectorAdapter.setRecycleViewAdapter(addressListView, db.selectAll());

        db.select(RegisterControl.getCurrentUserUid(this), new AddressLocalDatabase.SelectAddressListener() {
            @Override
            public void onSelect(AddressData[] addressList, boolean isOnline) {
                adapter = AddressSelectorAdapter.setRecycleViewAdapter(addressListView, ArrayUtils.toArrayList(addressList));
                refreshLayout.setRefreshing(false);
            }

            @Override
            public void onError(String msg) {
                Toast.makeText(AddressPicker.this, msg, Toast.LENGTH_SHORT).show();
                refreshLayout.setRefreshing(false);
            }
        });
    }
}
