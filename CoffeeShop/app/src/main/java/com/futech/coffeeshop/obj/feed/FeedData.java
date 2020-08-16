package com.futech.coffeeshop.obj.feed;

import com.futech.coffeeshop.obj.item.ItemData;

import java.util.List;

public class FeedData {

    public static final String DB_ID_KEY = "idDb";
    public static final String ID_KEY = "id";
    public static final String TITLE_KEY = "title";
    public static final String DESCRIPTION_KEY = "description";
    public static final String ACTION_KEY = "action";
    public static final String POSITION_KEY = "position";
    public static final String STATUS_KEY = "status";
    public static final String COLOR_KEY = "color";
    public static final String IMAGE_KEY = "image";
    public static final String ITEMS_KEY = "items";

    private int id;
    private int dbId;
    private String title;
    private String description;
    //    private DateTime time;
    private int action;
    private int position;
    private int status;
    private String color;
    private String image;
    private ItemData[] items;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public ItemData[] getItems() {
        return items;
    }

    public String getItemsIdString() {
        StringBuilder str = new StringBuilder();
        for (ItemData item : getItems()) {
            if (!str.toString().equals("")) str.append("-");
            str.append(item.getId());
        }
        return str.toString();
    }

    public void setItems(ItemData[] items) {
        this.items = items;
    }

    public void setItems(List<ItemData> items) {
        this.items = items.toArray(new ItemData[0]);
    }

    public int getDbId() {
        return dbId;
    }

    public void setDbId(int dbId) {
        this.dbId = dbId;
    }
}
