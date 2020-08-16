package com.futech.coffeeshop.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.List;

public abstract class DatabaseHelper<T> extends SQLiteOpenHelper {

    protected final String tableName;
    private String TAG = "DatabaseHelper";
    private final Context context;

    public DatabaseHelper(
            @Nullable Context context, @Nullable String db_name, int db_version, String tableName) {
        super(context, db_name, null, db_version);
        this.tableName = tableName;
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "onCreate: Create database for " + getDatabaseName() + " - " + tableName);
        Log.i(TAG, "onCreate: Query: " + createTableQuery());
        db.execSQL(createTableQuery());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "onUpgrade: Delete table for " + getDatabaseName() + " - " + tableName);
        Log.i(TAG, "onUpgrade: Query: " + deleteTableQuery());
        db.execSQL(deleteTableQuery());
        onCreate(db);
    }

    public void insert(T data) {
        SQLiteDatabase db = getWritableDatabase();
        db.insert(tableName, null, convertToContentValues(data));
        db.close();
    }

    public void insertItems(T[] data) {
        for (T t : data) {
            insert(t);
        }
    }

    public void update(T data, String whereClause, String[] whereArgs) {
        SQLiteDatabase db = getWritableDatabase();

        db.update(tableName, convertToContentValues(data), whereClause, whereArgs);
        db.close();
    }

    public List<T> selectAll() {
        return select(null, null);
    }

    public List<T> select(String whereCause, String[] args) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(tableName, null, whereCause, args, null, null, null);
        List<T> list = convertCursorToList(cursor);
        db.close();
        return list;
    }

    public void delete(String whereCause, String[] args) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(tableName, whereCause, args);
    }

    public void deleteAll() {
        delete(null, null);
    }

    protected abstract List<T> convertCursorToList(Cursor cursor);

    protected abstract ContentValues convertToContentValues(T value);

    protected abstract String createTableQuery();

    protected String deleteTableQuery() {
        return "DROP TABLE IF EXISTS " + tableName;
    }

    public Context getContext() {
        return context;
    }
}
