package com.futech.coffeeshop.obj.cart;

import android.content.ContentValues;
import android.database.Cursor;

import androidx.annotation.NonNull;

import com.futech.coffeeshop.obj.item.ItemData;

import java.util.ArrayList;
import java.util.List;

public class CartData extends ItemData {

    public static final String CART_ID_KEY = "cartId";
    public static final String CART_COUNT_KEY = "count";
    public static final String TABLE_NAME_KEY = "cart";
    public static final String CART_DB_ID_KEY = "cartDbId";
    public static final String CART_SYNC_KEY = "cartIsSync";
    public static final String ORDER_ID_KEY = "orderId";
    public static final String CART_STATUS = "status";
    public static final String CART_PRICE_KEY = "cartPrice";

    /**
     * @param itemData Item Data for item information
     * @param count    count of item in cart
     */
    public CartData(ItemData itemData, int count) {
        super(itemData);
        this.count = count;
    }

    private CartData(ItemData itemData) {
        super(itemData);
    }

    public static List<CartData> convertListFromCursor(Cursor cursor) {
        List<CartData> items = new ArrayList<>();
        while (cursor.moveToNext()) {
            CartData cartData = new CartData(ItemData.getItemDataByNextCursor(cursor));

            cartData.setCartId(cursor.getInt(cursor.getColumnIndexOrThrow(CartData.CART_ID_KEY)));
            cartData.setCount(cursor.getInt(cursor.getColumnIndexOrThrow(CartData.CART_COUNT_KEY)));
            cartData.setCartSync(cursor.getInt(cursor.getColumnIndexOrThrow(CartData.CART_SYNC_KEY)));
            cartData.setOrderId(cursor.getInt(cursor.getColumnIndexOrThrow(CartData.ORDER_ID_KEY)));
            cartData.setStatus(cursor.getInt(cursor.getColumnIndexOrThrow(CartData.CART_STATUS)));
            cartData.setCartPrice(cursor.getFloat(cursor.getColumnIndexOrThrow(CartData.CART_PRICE_KEY)));

            items.add(cartData);
        }
        return items;
    }

    @Override
    public ContentValues convertToContent() {
        ContentValues values = super.convertToContent();
        values.put(CART_COUNT_KEY, getCount());
        values.put(CART_ID_KEY, getCartId());
        values.put(CART_SYNC_KEY, getCartSync());
        values.put(CART_STATUS, getStatus());
        values.put(CART_PRICE_KEY, getCartPrice());
        return values;
    }

    CartData() {
    }

    private int cartId;
    private int count;
    private int cartSync;
    private int orderId;
    private int status;
    private float cartPrice;
    private int allCount;


    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @NonNull
    @Override
    public String toString() {
        return getCategory() + getTitle() + getId();
    }

    public int getCartId() {
        return cartId;
    }

    public void setCartId(int cartId) {
        this.cartId = cartId;
    }

    private int getCartSync() {
        return cartSync;
    }

    public void setCartSync(int cartSync) {
        this.cartSync = cartSync;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public boolean getStatus() {
        return status == 1;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public float getCartPrice() {
        return cartPrice;
    }

    public void setCartPrice(float cartPrice) {
        this.cartPrice = cartPrice;
    }

    public int getAllCount() {
        return allCount;
    }

    public void setAllCount(int allCount) {
        this.allCount = allCount;
    }
}
