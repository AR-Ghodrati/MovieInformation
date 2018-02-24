package com.ar.movieinformation;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Alireza Ghodrati on 22/02/2018.
 */

public class StoryDB extends SQLiteOpenHelper {
    public StoryDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE STORY(_id integer primary key autoincrement,Movie_Name varchar,STORY varchar)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
