package com.imtiaz_acedamy.apisecurity.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DB extends SQLiteOpenHelper {

    public static final String DB_NAME = "encrypted_data";
    public static final int DB_VERSION = 2;

    public DB(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists profileInfo (id INTEGER primary key autoincrement, name TEXT, phoneNo TEXT, mail TEXT)");
        db.execSQL("create table if not exists encryptData (id INTEGER primary key autoincrement, title TEXT, encryptedTxt TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("drop table if exists encryptData");
        db.execSQL("drop table if exists profileInfo");
        onCreate(db);

    }





    // add encrypted data here
    public void addEncryptedData(String title, String encryptedTxt){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", title);
        contentValues.put("encryptedTxt", encryptedTxt);

        db.insert("encryptData", null, contentValues);

    }

    // add profile data here
    public void addProfileData(String name, String phoneNo, String mail){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("phoneNo", phoneNo);
        contentValues.put("mail", mail);

        db.insert("profileInfo", null, contentValues);
    }

     //get all profile data here
    public Cursor getAllProfileData() {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("select * from profileInfo", null);
        return cursor;
    }

    // get all encrypted data here
    public Cursor getAllEncryptedData() {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("select * from encryptData", null);
        return cursor;
    }

    // Delete Encrypted Data
    public void deleteEncryptedData(String id){
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("delete from encryptData where id like " + id);
    }
}
