package com.OutOfOrder;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "UserInfo.db";
    public static final String TABLE_NAME = "user_table";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "NAME";
    public static final String COL_3 = "REACTION_TIME";
    public static final String COL_4 = "MEM_MISSES";
    public static final String COL_5 = "MEM_COMP_TIME";

    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("create table " + TABLE_NAME + "(ID INTEGER PRIMARY KEY, NAME TEXT, REACTION_TIME INTEGER, MEM_MISSES INTEGER, MEM_COMP_TIME INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void insertData(String name, int reaction_time, int misses, int comp_time){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_2, name);
        cv.put(COL_3, reaction_time);
        cv.put(COL_4, misses + " misses");
        cv.put(COL_5, comp_time + " sec(s)");
        db.insert(TABLE_NAME, null, cv);
        db.close();
    }
}
