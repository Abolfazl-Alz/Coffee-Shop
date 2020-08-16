package com.futech.coffeeshop.obj.item;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;

import com.futech.coffeeshop.obj.discount.DiscountData;
import com.futech.coffeeshop.obj.review.ReviewData;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ItemData implements Serializable {

    private int id;
    private int db_id;
    private String title;
    private String information;
    private String image;
    private String type;
    private int category;
    private String sizes;
    private String price;
    private int salesNumber;
    private Date dateCreate;
    private DiscountData discount;
    private ReviewData[] reviews;

    public static final String TABLE_NAME_KEY = "items";
    public static final String ID_KEY = "id";//Local Database ID
    public static final String ID_DB_KEY = "db_id";//Server Database ID
    public static final String TITLE_KEY = "title";
    public static final String INFORMATION_KEY = "information";
    public static final String IMAGE_KEY = "image";
    public static final String TYPE_KEY = "type";
    public static final String CATEGORY_KEY = "category";
    public static final String SIZES_KEY = "sizes";
    public static final String PRICE_KEY = "price";
    public static final String SALES_COUNT_KEY = "sales_count";
    public static final String CREATE_TIME_KEY = "dateCreated";
    public static final String DISCOUNT_ID_KEY = "discountId";
    public static final String DISCOUNT_VALUE_KEY = "discountValue";

    public ItemData() {
    }

    public ItemData(ItemData itemData) {
        id = itemData.getId();
        title = itemData.getTitle();
        information = itemData.getInformation();
        image = itemData.getImage();
        type = itemData.getType();
        category = itemData.getCategory();
        sizes = itemData.getSizes();
        price = itemData.getPrice();
        db_id = itemData.getDb_id();
        dateCreate = itemData.getDateCreate();
        salesNumber = itemData.getSalesNumber();
        discount = itemData.getDiscount();
    }

    public static ItemData getItemDataByNextCursor(Cursor cursor) {
        ItemData itemData = new ItemData();
        itemData.setId(cursor.getInt(cursor.getColumnIndexOrThrow(ItemData.ID_DB_KEY)));
        itemData.setImage(cursor.getString(cursor.getColumnIndexOrThrow(ItemData.IMAGE_KEY)));
        itemData.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(ItemData.TITLE_KEY)));
        itemData.setId(cursor.getInt(cursor.getColumnIndexOrThrow(ItemData.ID_KEY)));
        itemData.setInformation(cursor.getString(cursor.getColumnIndexOrThrow(ItemData.INFORMATION_KEY)));
        itemData.setSizes(cursor.getString(cursor.getColumnIndexOrThrow(ItemData.SIZES_KEY)));
        itemData.setType(cursor.getString(cursor.getColumnIndexOrThrow(ItemData.TYPE_KEY)));
        itemData.setPrice(cursor.getString(cursor.getColumnIndexOrThrow(ItemData.PRICE_KEY)));
        itemData.setCategory(cursor.getInt(cursor.getColumnIndexOrThrow(ItemData.CATEGORY_KEY)));
        itemData.setSalesNumber(cursor.getInt(cursor.getColumnIndexOrThrow(ItemData.SALES_COUNT_KEY)));

        DiscountData discountData = new DiscountData();
        discountData.setId(cursor.getInt(cursor.getColumnIndexOrThrow(ItemData.DISCOUNT_ID_KEY)));
        discountData.setValue(cursor.getInt(cursor.getColumnIndexOrThrow(ItemData.DISCOUNT_VALUE_KEY)));
        itemData.setDiscount(discountData);

        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat();
        try {
            itemData.setDateCreate(dateFormat.parse(cursor.getString(cursor.getColumnIndexOrThrow(ItemData.CREATE_TIME_KEY))));
        } catch (ParseException e) {
            itemData.setDateCreate(new Date());
        }
        return itemData;
    }

    public ContentValues convertToContent() {
        ContentValues values = new ContentValues();
        values.put(ItemData.ID_KEY, getId());
        values.put(ItemData.TITLE_KEY, getTitle());
        values.put(ItemData.INFORMATION_KEY, getInformation());
        values.put(ItemData.IMAGE_KEY, getImage());
        values.put(ItemData.PRICE_KEY, getPrice());
        values.put(ItemData.TYPE_KEY, getType());
        values.put(ItemData.CATEGORY_KEY, getCategory());
        values.put(ItemData.SIZES_KEY, getSizes());
        values.put(ItemData.SALES_COUNT_KEY, getSalesNumber());
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat();
        if (getDateCreate() != null) {
            values.put(ItemData.CREATE_TIME_KEY, dateFormat.format(getDateCreate()));
        }else {
            values.put(ItemData.CREATE_TIME_KEY, dateFormat.format(new Date()));
        }
        values.put(ItemData.DISCOUNT_ID_KEY, getDiscount().getId());
        values.put(ItemData.DISCOUNT_VALUE_KEY, getDiscount().getValue());

        return values;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public String getSizes() {
        return sizes;
    }

    public String[] getSizeList() {
        return getSizes().split("-");
    }

    public void setSizes(String sizes) {
        this.sizes = sizes;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public void setId(int cardId) {
        this.id = cardId;
    }

    private int getDb_id() {
        return db_id;
    }

    public int getSalesNumber() {
        return salesNumber;
    }

    private void setSalesNumber(int salesNumber) {
        this.salesNumber = salesNumber;
    }

    public Date getDateCreate() {
        return dateCreate;
    }

    private void setDateCreate(Date dateCreate) {
        this.dateCreate = dateCreate;
    }

    public DiscountData getDiscount() {
        if (discount != null) return discount;
        else return new DiscountData();
    }

    private void setDiscount(DiscountData discount) {
        this.discount = discount;
    }

    public int getDiscountedPrice() {
        if (getDiscount() != null && getDiscount().getValue() > 0) {
            return Integer.parseInt(getPrice()) * getDiscount().getValue() / 100;
        }else {
            return Integer.parseInt(getPrice());
        }
    }

    public ReviewData[] getReviews() {
        return reviews;
    }

    public void setReviews(ReviewData[] reviews) {
        this.reviews = reviews;
    }
}
