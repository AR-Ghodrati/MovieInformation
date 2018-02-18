package com.ar.movieinformation;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by alireza on 08/08/2017.
 */

public class database extends SQLiteOpenHelper {

    public database(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context,name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE MOVIEDATA(_id integer primary key autoincrement,Movie_Rank varchar,IMDB_RANK_simple varchar,Movie_year varchar ,Movie_name varchar,Movie_farsi_name varchar,MovieDirector_name varchar,Actors_Actress varchar,IMDB_RANK_full varchar,Movie_IMDB_link varchar,Movie_picture_link varchar)");
        db.execSQL("CREATE TABLE MOVIEDATAOLD(_id integer primary key autoincrement,Movie_Rank varchar,IMDB_RANK_simple varchar,IMDB_RANK_full varchar,Movie_name varchar)");
       // db.execSQL("CREATE TABLE MOVIEFARSINAME(_id integer primary key autoincrement,Movie_Rank varchar,Movie_name varchar,Movie_farsi_name varchar)");
        db.execSQL("CREATE TABLE MOVIEFARSIDATA1(_id integer primary key autoincrement,Movie_farsi_name varchar,link varchar,ENmoviename varchar)");
        db.execSQL("CREATE TABLE MOVIEFARSIDATA2(_id integer primary key autoincrement,Movie_ENname varchar,Movie_time varchar,Movie_Style varchar,Movie_Award text,MovieStory text,MovieQUPIC text,Movie_Naghed varchar,Download_movie_link_720p varchar,Download_movie_link_1080p varchar,Download_movie_subtitle_link varchar,isdubled boolean NOT NULL DEFAULT false)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
