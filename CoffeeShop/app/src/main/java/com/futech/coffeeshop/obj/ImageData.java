package com.futech.coffeeshop.obj;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ImageData {

    public static final String ID_KEY = "id";
    public static final String IMAGE_NAME_KEY = "imageName";
    public static final String FILE_PATH_KEY = "filePath";
    public static final String SERVER_URL_KEY = "serverUrl";
    public static final String CREATE_TIME_KEY = "createTime";
    public static final String LAST_USE_KEY = "lastUseKey";

    private int id;
    private String imageName;
    private String filePath;
    private String serverUrl;
    private Date createTime;
    private Date lastUse;

    public ImageData(String imageName, String filePath, String serverUrl, Date createTime, Date lastUse) {
        this.imageName = imageName;
        this.filePath = filePath;
        this.serverUrl = serverUrl;
        this.createTime = createTime;
        this.lastUse = lastUse;
    }

    private ImageData() {
        imageName = "";
        filePath = "";
        serverUrl = "";
        createTime = new Date();
        lastUse = new Date();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    private void setCreateTimeString(String createTime) {
        try {
            this.createTime = getDateFormat().parse(createTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public Date getLastUse() {
        return lastUse;
    }

    public void setLastUse(Date lastUse) {
        this.lastUse = lastUse;
    }

    private void setLastUseString(String date) {
        try {
            lastUse = getDateFormat().parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public ContentValues convertToContentValues() {
        ContentValues values = new ContentValues();
        values.put(IMAGE_NAME_KEY, getImageName());
        values.put(FILE_PATH_KEY, getFilePath());
        values.put(SERVER_URL_KEY, getServerUrl());
        SimpleDateFormat dateFormat = getDateFormat();
        values.put(CREATE_TIME_KEY, dateFormat.format(getCreateTime()));

        values.put(LAST_USE_KEY, dateFormat.format(getLastUse()));
        return values;
    }

    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat getDateFormat() {
        return new SimpleDateFormat("yyyy-MM-dd HH-mm");
    }

    public static List<ImageData> cursorToImageData(Cursor cursor) {
        List<ImageData> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            ImageData imageData = new ImageData();
            imageData.setCreateTimeString(cursor.getString(cursor.getColumnIndexOrThrow(CREATE_TIME_KEY)));
            imageData.setFilePath(cursor.getString(cursor.getColumnIndexOrThrow(FILE_PATH_KEY)));
            imageData.setId(cursor.getInt(cursor.getColumnIndexOrThrow(ID_KEY)));
            imageData.setImageName(cursor.getString(cursor.getColumnIndexOrThrow(IMAGE_NAME_KEY)));
            imageData.setLastUseString(cursor.getString(cursor.getColumnIndexOrThrow(LAST_USE_KEY)));
            imageData.setServerUrl(cursor.getString(cursor.getColumnIndexOrThrow(SERVER_URL_KEY)));
            list.add(imageData);
        }
        return list;
    }

    public void setLastUseNow() {
        setLastUse(new Date());
    }
}
