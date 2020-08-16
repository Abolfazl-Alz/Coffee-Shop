package com.futech.coffeeshop.obj.address;

import android.content.ContentValues;

import java.io.Serializable;

public class AddressData implements Serializable {

    public static final String ID_KEY = "id";
    public static final String ID_DB_KEY = "idDb";
    public static final String LAT_KEY = "lat";
    public static final String LNG_KEY = "lng";
    public static final String ADDRESS_KEY = "address";
    public static final String UID_KEY = "uid";
    public static final String NAME_KEY = "name";

    private int id; //Server Db ID
    private int uid; //user id
    private String name;
    private String address;
    private String userName = "";
    private String userPhone = "";
    private float lat;
    private float lng;

    public AddressData() {
        this.id = -1;
    }

    public ContentValues convertToContentValues() {
        ContentValues values = new ContentValues();
        values.put(ID_KEY, getId());
        values.put(LAT_KEY, getLat());
        values.put(LNG_KEY, getLng());
        values.put(ADDRESS_KEY, getAddress());
        values.put(UID_KEY, getUid());
        values.put(NAME_KEY, getName());
        return values;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDbId(int dbId) {
        //local db ID
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getUid() {
        return uid;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public float getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public float getLng() {
        return lng;
    }

    public void setLng(float lng) {
        this.lng = lng;
    }
}
