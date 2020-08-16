package com.futech.coffeeshop.obj.home;

public class CollectionData {

    private int id;
    private String title;
    private String description;
    private CollectionItemData[] items;
    private String color;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public CollectionItemData[] getItems() {
        return items;
    }

    public void setItems(CollectionItemData[] items) {
        this.items = items;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
