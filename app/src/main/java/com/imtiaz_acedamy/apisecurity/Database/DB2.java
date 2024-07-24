package com.imtiaz_acedamy.apisecurity.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DB2 extends SQLiteOpenHelper {

   public static final String DB_NAME = "practice_bag";
    public static final int DB_VERSION = 2;

    public DB2(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists drug (id INTEGER primary key autoincrement, name TEXT, price DOUBLE)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists drug");
    }

    //===============
    public void addDrugExpense (String name, double price){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues conval = new ContentValues();
        conval.put("name", name);
        conval.put("price", price);

        db.insert("drug", null, conval);

    }

    //===========
    public void addDrugIncome(String name, double price){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues conval = new ContentValues();
        conval.put("name", name);
        conval.put("price", price);

        db.insert("drug", null, conval);
    }

    //=============
    public double calculateTotalExpense(){
        double totalExpense = 0;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor  cursor = db.rawQuery("select * from drug", null);

        if (cursor != null & cursor.getCount()>0){
            while (cursor.moveToNext()){
                double price = cursor.getDouble(2);
                totalExpense = totalExpense + price;
            }
        }

        return totalExpense;

    }

    //==========
    public double calculateTotalIncome (){
        double total = 0;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from drug", null);

        if (cursor != null & cursor.getCount()>0){
            while (cursor.moveToNext()){
                double price = cursor.getDouble(2);
                total = total + price;
            }
        }
        return total;

    }

    //===========
    public Cursor getExpenseAllData() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from drug", null);
        return cursor;
    }

    //=========
    public Cursor getIncomeAllData(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =  db.rawQuery("select * from drug", null);
        return cursor;
    }

    //==========
    public void updateExpense(String id){
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("delete from expense where id like "+ id);
    }

    //========
    public void updateIncome(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("update from income where id like " + id);
    }
}
