package com.imtiaz_acedamy.apisecurity.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DicSavedDB extends SQLiteOpenHelper {

    public static final String DB_NAME = "dictionary_saved_item";
    public static final int DB_VERSION = 2;

    public DicSavedDB(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists savedItem (id INTEGER primary key autoincrement, word TEXT, meaning TEXT, example Txt)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("drop table if exists savedItem");

        onCreate(db);

    }

    // add encrypted data here
    public void addWordData(String word, String meaning,String example){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("word", word);
        contentValues.put("meaning", meaning);
        contentValues.put("example", example);

        db.insert("savedItem", null, contentValues);

    }


     //get all profile data here
    public Cursor getAllWordData() {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("select * from savedItem", null);
        return cursor;
    }

    // Delete Encrypted Data
    public void deleteEncryptedData(String id){
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("delete from encryptData where id like " + id);
    }
}
