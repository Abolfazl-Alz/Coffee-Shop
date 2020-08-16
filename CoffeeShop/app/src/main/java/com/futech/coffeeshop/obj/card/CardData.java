package com.futech.coffeeshop.obj.card;

import android.content.ContentValues;

import androidx.annotation.NonNull;

public class CardData {

    //Database Keys
    public static final String ID_KEY = "id";
    public static final String ID_DB_KEY = "idDb";
    public static final String IMAGE_KEY = "image";
    public static final String ALT_KEY = "alt";
    public static final String COLOR_KEY = "color";
    public static final String LINK_KEY = "link";

    private int id;
    private int idDb;
    private String image;
    private String alt;
    private String link;
    private String color;

    public ContentValues CardDataToValues() {
        ContentValues values = new ContentValues();
        values.put(ID_KEY, id);
        values.put(IMAGE_KEY, image);
        values.put(ALT_KEY, alt);
        values.put(COLOR_KEY, alt);
        return values;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAlt() {
        return alt;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }

    public Link getLink() {
        if (link == null) return new Link("", "");
        String[] sp = link.split(":");
        if (sp.length == 1) return new Link(sp[0], "");
        return new Link(sp[0], sp[1]);
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getIdDb() {
        return idDb;
    }

    public void setIdDb(int idDb) {
        this.idDb = idDb;
    }

    @NonNull
    @Override
    public String toString() {
        return getId() + ". " + getAlt();
    }

    public static class Link {

        public static final String POST_KEY = "post";

        private String action;
        private String value;

        public Link(String action, String value) {
            this.action = action;
            this.value = value;
        }

        public String getAction() {
            return action;
        }

        public String getValue() {
            return value;
        }
    }
}
