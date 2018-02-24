package com.ar.movieinformation;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Alireza Ghodrati on 21/02/2018.
 */

public class OMDBDatabase extends SQLiteOpenHelper {


    public OMDBDatabase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE OMDB(_id integer primary key autoincrement,Movie_Name varchar,Movie varchar)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
