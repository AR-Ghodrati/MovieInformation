package com.ar.movieinformation;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class ShowFullInfo extends AppCompatActivity{
    TextView Year,Name,Dir,Cast,Link,Data,Simpledata,smalldata;
    ImageView MovieImage;
    RelativeLayout layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_layout);
        Year=(TextView)findViewById(R.id.YaerMovie);
        Name=(TextView)findViewById(R.id.MovieName);
        Dir=(TextView)findViewById(R.id.DirectorMovie);
        Cast=(TextView)findViewById(R.id.CastMovie);
        Link=(TextView)findViewById(R.id.IMDBLink);
        Data=(TextView)findViewById(R.id.IMDBdata);
        Simpledata=(TextView)findViewById(R.id.SimpleIMDB);
        MovieImage=(ImageView)findViewById(R.id.MovieIMG);
        smalldata=(TextView)findViewById(R.id.Showsmalldata);
        layout=(RelativeLayout) findViewById(R.id.fullrellayout);
        Show();
    }
    private void Show()
    {
        String Years,Names,Dirs,Casts,Links,Datas,Simple,small,farsiname;
        Years = getIntent().getExtras().getString("Movie_year");
        Names=getIntent().getExtras().getString("Movie_name");
        Dirs=getIntent().getExtras().getString("MovieDirector_name");
        Casts=getIntent().getExtras().getString("Actors_Actress");
        Links=getIntent().getExtras().getString("Movie_IMDB_link");
        Datas=getIntent().getExtras().getString("IMDB_RANK_full");
        Simple=getIntent().getExtras().getString("IMDB_RANK_simple");
        small=getIntent().getExtras().getString("id");
        farsiname=getIntent().getExtras().getString("Movie_name_farsi");
        Year.setText(Years);
        if(farsiname == null)
        Name.setText(Names);
        else
            Name.setText(Names+"\n"+farsiname);
        Dir.setText(Dirs);
        Cast.setText(Casts);
        Link.setText(Links);
        Data.setText(Simple+" از "+getIntent().getExtras().getString("count")+" امتیاز کاربران");
        smalldata.setText(small);
        Simpledata.setText(Simple);
        SQLiteDatabase temp;
        database movieDB = new database(getApplicationContext(),"Movie_db", null,1);
        temp=movieDB.getReadableDatabase();
        Cursor cursor = temp.query("MOVIEFARSIDATA2", new String[]{"Movie_ENname", "Movie_time", "Movie_Style", "Movie_Award","MovieStory", "MovieQUPIC"}, null, null, null, null, null);
        while (cursor.moveToNext()) {
            if(cursor.getString(cursor.getColumnIndex("Movie_ENname")).lastIndexOf(0)==' ') {
                String name = cursor.getString(cursor.getColumnIndex("Movie_ENname")).trim().substring(0, cursor.getString(cursor.getColumnIndex("Movie_ENname")).length() - 1);
            }
            else {
                String name = cursor.getString(cursor.getColumnIndex("Movie_ENname")).trim();
                if (Names != null && Names.equalsIgnoreCase(name)) {
                    Names = name;
                    break;
                }
            }
        }
        File file = null;
        if (Names != null) {
            file = new File(getApplicationContext().getFilesDir().getPath() + "/" + Names.trim() + ".jpg");
        }
        assert file != null;
        if(file.exists()) {
            Bitmap bmp = BitmapFactory.decodeFile(getApplicationContext().getFilesDir().getPath() + "/" + Names + ".jpg");
            MovieImage.setImageBitmap(bmp);
        }
        else
        Toast.makeText(getApplicationContext(),"عکس برای این فیلم پیدا نشد،لطفا لیست را بروز رسانی کنید",Toast.LENGTH_SHORT).show();
    }
}



