package com.futech.coffeeshop.obj.home;

import android.util.Log;

import com.futech.coffeeshop.obj.cart.CartData;
import com.futech.coffeeshop.obj.discount.DiscountData;
import com.futech.coffeeshop.obj.item.ItemData;

import java.util.List;

public class CollectionItemData {

    private String title;
    private int price;
    private DiscountData discount;
    private String image;
    private Object tag;

    private static final String TAG = "CollectionItemData";

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public DiscountData getDiscount() {
        return discount;
    }

    private void setDiscount(DiscountData discount) {
        this.discount = discount;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    private static CollectionItemData fromItemData(ItemData itemData) {
        CollectionItemData data = new CollectionItemData();
        data.setTitle(itemData.getTitle());
        data.setImage(itemData.getImage());
        data.setTag(itemData);
        data.setDiscount(itemData.getDiscount());
        try {
            data.setPrice(Integer.parseInt(itemData.getPrice()));
        } catch (NumberFormatException ex) {
            Log.i(TAG, "fromItemData: invalid number format for price");
        }
        return data;
    }

    private static CollectionItemData fromCartData(CartData itemData) {
        CollectionItemData data = new CollectionItemData();
        data.setTitle(itemData.getTitle());
        data.setImage(itemData.getImage());
        try {
            data.setPrice(Integer.parseInt(itemData.getPrice()));
        } catch (NumberFormatException ex) {
            Log.i(TAG, "fromItemData: invalid number format for price");
        }
        return data;
    }

    public static CollectionItemData[] getItemsData(ItemData[] items) {
        CollectionItemData[] collections = new CollectionItemData[items.length];
        for (int i = 0; i < collections.length; i++) {
            collections[i] = fromItemData(items[i]);
        }
        return collections;
    }

    public static CollectionItemData[] getItemsData(List<CartData> items) {
        CollectionItemData[] collections = new CollectionItemData[items.size()];
        for (int i = 0; i < collections.length; i++) {
            collections[i] = fromCartData(items.get(i));
        }
        return collections;
    }

    public Object getTag() {
        return tag;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }
}
