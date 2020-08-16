package com.futech.coffeeshop.obj.posts;

import android.graphics.Color;
import android.os.Build;

import androidx.annotation.ColorInt;
import androidx.annotation.RequiresApi;

import com.futech.coffeeshop.obj.discount.DiscountData;
import com.futech.coffeeshop.obj.register.RegisterData;

import java.io.Serializable;
import java.util.Date;

public class PostData implements Serializable {

    public static final String TABLE_POST_KEY = "posts";
    public static final String ID_KEY = "id";
    public static final String ID_DB_KEY = "idDb";
    public static final String TITLE_KEY = "title";
    public static final String TEXT_KEY = "text";
    public static final String IMAGE_KEY = "imageUrl";
    public static final String DATE_KEY = "dateCreated";
    public static final String WRITER_ID = "writerId";
    public static final String WRITER_NAME = "writerName";
    public static final String COLOR_KEY = "color";

    private int id;
    private int dbId;
    private String title;
    private String text;
    private String imageUrl;
    private Date createdTime;
    private RegisterData writer;
    private String color;
    private DiscountData[] discounts;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDbId() {
        return dbId;
    }

    public void setDbId(int dbId) {
        this.dbId = dbId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public RegisterData getWriter() {
        return writer;
    }

    public void setWriter(RegisterData writer) {
        this.writer = writer;
    }

    public void setColorString(String color) {
        this.color = color;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setColor(@ColorInt int color) {
        this.color = Color.valueOf(color).toString();
    }

    public int getColor() {
        return Color.parseColor(color);
    }

    public String getColorString() {
        return color;
    }

    public DiscountData[] getDiscounts() {
        return discounts;
    }

    public void setDiscounts(DiscountData[] discounts) {
        this.discounts = discounts;
    }
}
