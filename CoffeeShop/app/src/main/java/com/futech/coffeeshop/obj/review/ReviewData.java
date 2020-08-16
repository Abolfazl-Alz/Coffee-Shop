package com.futech.coffeeshop.obj.review;

import android.content.ContentValues;

import com.futech.coffeeshop.obj.register.RegisterData;

import java.io.Serializable;

public class ReviewData implements Serializable {

    public static final String ID = "id";
    public static final String ID_DB = "idDb";
    public static final String UID = "uid";
    public static final String IID = "iid";
    public static final String TEXT = "text";
    public static final String RATE = "rate";
    public static final String NAME = "name";

    private int id;
    private int dbId;
    private Integer uid;
    private int iid;
    private String text;
    private float rate;
    private RegisterData registerData;

    public ContentValues convertToContents() {
        ContentValues values = new ContentValues();
        values.put(ID, getId());
        values.put(UID, getUid());
        values.put(TEXT, getText());
        values.put(RATE, getRate());
        return values;
    }

    public ReviewData(int id, int dbId, Integer uid, int iid, String text, float rate) {
        this.id = id;
        this.dbId = dbId;
        this.uid = uid;
        this.iid = iid;
        this.text = text;
        this.rate = rate;
    }

    public ReviewData() {
        this(0, 0, -1, 0, "", 0);
    }

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

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public int getItemId() {
        return iid;
    }

    public void setItemId(int iid) {
        this.iid = iid;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public RegisterData getRegisterData() {
        return registerData;
    }

    public void setRegisterData(RegisterData registerData) {
        this.registerData = registerData;
    }
}
