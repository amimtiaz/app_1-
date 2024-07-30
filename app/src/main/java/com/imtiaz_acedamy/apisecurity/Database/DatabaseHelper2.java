package com.imtiaz_acedamy.apisecurity.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class DatabaseHelper2 extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "dictionary.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper2(Context context) {
        super(context, DATABASE_NAME,null, DATABASE_VERSION);
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from Dictionary", null);
        return cursor;
    }

    public Cursor searchDataById(String word) {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from Dictionary where word like '%" + word + "%' ", null);
        return cursor;

    }

}
