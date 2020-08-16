package com.futech.coffeeshop.obj.order;

import android.content.ContentValues;

import com.futech.coffeeshop.obj.register.RegisterData;
import com.futech.coffeeshop.obj.address.AddressData;
import com.futech.coffeeshop.obj.cart.CartData;

import static com.futech.coffeeshop.obj.notification.NotificationData.NotificationStatus.ORDER_DEFAULT_STATUS;

public class OrderData {
    public static final String ID_KEY = "id";
    public static final String ID_DB_KEY = "idDb";
    public static final String UID_KEY = "uid";
    public static final String MESSAGE_KEY = "message";
    public static final String CART_KEY = "cartItems";
    public static final String ACCEPT_KEY = "accept";
    public static final String TEXT_STATUS_KEY = "textStatus";

    private int id;
    private int dbId;
    private Integer uid;
    private String message;
    private CartData[] carts;
    private AddressData address;
    private RegisterData registerData;
    private Integer status;
    private String statusText;

    public ContentValues convertToContentValues() {
        ContentValues values = new ContentValues();
        values.put(ID_KEY, getId());
        values.put(UID_KEY, getUid());
        values.put(MESSAGE_KEY, getMessage());
        values.put(ACCEPT_KEY, isAccept());
        values.put(TEXT_STATUS_KEY, getStatusText());

        return values;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public CartData[] getCarts() {
        return carts;
    }

    public void setCarts(CartData[] carts) {
        this.carts = carts;
    }

    public int getDbId() {
        return dbId;
    }

    public void setDbId(int dbId) {
        this.dbId = dbId;
    }

    public AddressData getAddress() {
        return address;
    }

    public void setAddress(AddressData address) {
        this.address = address;
    }

    public RegisterData getRegisterData() {
        return registerData;
    }

    public boolean isAccept() {
        return status > ORDER_DEFAULT_STATUS;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public int getStatus() {
        return this.status;
    }

    public float getPrice() {
        if (carts.length == 0) return 0;
        float price = 0;
        for (CartData cart : carts) {
            price += cart.getCartPrice() * cart.getCount();
        }

        return price;
    }

    public String getStatusText() {
        return statusText;
    }

}