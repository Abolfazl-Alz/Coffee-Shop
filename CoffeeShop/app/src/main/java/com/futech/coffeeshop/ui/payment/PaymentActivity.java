package com.futech.coffeeshop.ui.payment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.futech.coffeeshop.R;
import com.futech.coffeeshop.adapter.DiscountListViewAdapter;
import com.futech.coffeeshop.adapter.OrderCartItemsAdapter;
import com.futech.coffeeshop.obj.address.AddressData;
import com.futech.coffeeshop.obj.cart.CartData;
import com.futech.coffeeshop.obj.discount.DiscountData;
import com.futech.coffeeshop.ui.address.AddressPicker;
import com.futech.coffeeshop.utils.DiscountHelper;
import com.futech.coffeeshop.utils.Internet;
import com.futech.coffeeshop.utils.listener.SelectListener;
import com.futech.coffeeshop.utils.local_database.CartLocalDatabase;
import com.futech.coffeeshop.utils.local_database.OrderLocalDatabase;

import java.util.ArrayList;
import java.util.Date;

public class PaymentActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener, TextView.OnEditorActionListener {

    private RecyclerView itemsInCartRecycle;
    private View addressLayout;
    private TextView addressName, addressText, totalPrice;
    private RadioButton sendAddress, comeCoffeeShop;

    private DiscountListViewAdapter discountAdapter;

    public final int ADDRESS_SELECT = 123;
    private int addressId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        Button changeAddress = findViewById(R.id.change_select_address);
        addressLayout = findViewById(R.id.address_selector_layout);
        itemsInCartRecycle = findViewById(R.id.items_in_carts);
        addressName = findViewById(R.id.address_name);
        addressText = findViewById(R.id.address_text);
        totalPrice = findViewById(R.id.total_price);
        //options
        sendAddress = findViewById(R.id.send_to_address_option);
        comeCoffeeShop = findViewById(R.id.come_to_coffee_shop_option);
        //discount
        EditText discountEditText = findViewById(R.id.discount_code);
        RecyclerView userDiscountList = findViewById(R.id.user_discount_list);

        Button doneBtn = findViewById(R.id.button_continue);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.order_items);
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xffffffff));
        }

        RadioGroup orderType = findViewById(R.id.order_type_group);

        orderType.setOnCheckedChangeListener(this);

        final CartLocalDatabase cartLocalDatabase = new CartLocalDatabase(this);
        refresh_items(cartLocalDatabase);

        if (Internet.isConnected(this)) {
            cartLocalDatabase.select(new SelectListener<CartData[]>() {
                @Override
                public void onSelect(CartData[] carts, boolean isOnline) {
                    refresh_items(cartLocalDatabase);
                }

                @Override
                public void onError(String msg) {
                    Toast.makeText(PaymentActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            Toast.makeText(this, R.string.check_internet, Toast.LENGTH_SHORT).show();
        }

        changeAddress.setOnClickListener(v -> {
            Intent intent = new Intent(PaymentActivity.this, AddressPicker.class);
            startActivityForResult(intent, ADDRESS_SELECT);

        });

        doneBtn.setOnClickListener(v -> {
            if (sendAddress.isChecked() && addressId == -1) {
                Toast.makeText(PaymentActivity.this, R.string.select_address_msg, Toast.LENGTH_SHORT).show();
                return;
            }else if (comeCoffeeShop.isChecked()) {
                addressId = -1;
            }
            OrderLocalDatabase db = new OrderLocalDatabase(PaymentActivity.this);
            //todo: ad discount code in payment page.
            db.addOrder("", addressId, "", new OrderLocalDatabase.OrderListener() {
                @Override
                public void onListener() {
                    Toast.makeText(PaymentActivity.this, R.string.payment_done_msg, Toast.LENGTH_SHORT).show();
                    setResult(Activity.RESULT_OK);
                    finish();
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(PaymentActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            });
        });

        discountAdapter = DiscountListViewAdapter.setAdapter(userDiscountList, new ArrayList<>());
        discountEditText.setOnEditorActionListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == ADDRESS_SELECT) {
            if (data != null) {
                if (data.getExtras() != null && data.getExtras().getSerializable("address") != null && data.getExtras().getSerializable("address") instanceof AddressData) {
                    AddressData addressData = (AddressData) data.getExtras().getSerializable("address");
                    if (addressData == null) return;
                    fillAddress(addressData);
                }
            }
        }
    }

    private void fillAddress(AddressData addressData) {
        addressName.setText(addressData.getName());
        addressText.setText(addressData.getAddress());
        addressId = addressData.getId();
    }

    private void refresh_items(CartLocalDatabase cartLocalDatabase) {
        CartData[] cartList = cartLocalDatabase.selectAll().toArray(new CartData[0]);
        OrderCartItemsAdapter.setAdapter(itemsInCartRecycle, cartList);
        int totalPrice = 0;
        for (CartData cart : cartList) {
            totalPrice += cart.getDiscountedPrice() * cart.getCount();
        }

        this.totalPrice.setText(String.format(getString(R.string.total_price), totalPrice));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.send_to_address_option:
                addressLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.come_to_coffee_shop_option:
                addressLayout.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (v.getId() == R.id.discount_code) {
            String code = v.getText().toString().trim();
            DiscountHelper helper = new DiscountHelper(this);
            helper.getDiscountData(code, new SelectListener<DiscountData>() {
                @Override
                public void onSelect(DiscountData data, boolean isOnline) {
                    if (data == null)
                        Toast.makeText(PaymentActivity.this, R.string.discount_not_valid, Toast.LENGTH_SHORT).show();
                    else if (discountAdapter.isCodeExist(data.getCode())) {
                        Toast.makeText(PaymentActivity.this, R.string.code_is_use_discount, Toast.LENGTH_SHORT).show();
                    }else if (!data.getCanUse()) {
                        Toast.makeText(PaymentActivity.this, R.string.unable_discount_code, Toast.LENGTH_SHORT).show();
                    }else if (data.getExpiration().compareTo(new Date()) > 0) {
                        Toast.makeText(PaymentActivity.this, R.string.deadline_error_discount, Toast.LENGTH_SHORT).show();
                    }else {
                        discountAdapter.addDiscountToList(data);
                        v.setText("");
                    }
                }

                @Override
                public void onError(String msg) {
                    Toast.makeText(PaymentActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            }); return true;
        } return false;
    }
}
