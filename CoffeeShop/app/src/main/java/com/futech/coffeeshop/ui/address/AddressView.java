package com.futech.coffeeshop.ui.address;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.futech.coffeeshop.R;
import com.futech.coffeeshop.adapter.AddressAdapter;
import com.futech.coffeeshop.obj.address.AddressData;
import com.futech.coffeeshop.utils.RegisterControl;
import com.futech.coffeeshop.utils.local_database.AddressLocalDatabase;
import com.google.android.gms.common.util.ArrayUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AddressView extends AppCompatActivity {

    private RecyclerView addressListView;
    private SwipeRefreshLayout refreshLayout;

    private final int ADD_ADDRESS_RESULT = 124;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_view);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.address_list_title);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xffffffff));
        }

        addressListView = findViewById(R.id.address_list);
        FloatingActionButton addBtn = findViewById(R.id.add_address);
        refreshLayout = findViewById(R.id.refresh_layout);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(AddressView.this, AddAddressActivity.class);
                startActivityForResult(intent, ADD_ADDRESS_RESULT);
            }
        });

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadDataList();
            }
        });

        loadDataList();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_ADDRESS_RESULT && resultCode == Activity.RESULT_OK) loadDataList();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    public void loadDataList() {
        refreshLayout.setRefreshing(true);
        AddressLocalDatabase db = new AddressLocalDatabase(this);
        db.select(RegisterControl.getCurrentUserUid(this), new AddressLocalDatabase.SelectAddressListener() {
            @Override
            public void onSelect(AddressData[] addressList, boolean isOnline) {
                AddressAdapter.setRecycleViewAdapter(addressListView, ArrayUtils.toArrayList(addressList));
                refreshLayout.setRefreshing(false);
            }

            @Override
            public void onError(String msg) {
                Toast.makeText(AddressView.this, msg, Toast.LENGTH_SHORT).show();
                refreshLayout.setRefreshing(false);
            }
        });
    }
}
