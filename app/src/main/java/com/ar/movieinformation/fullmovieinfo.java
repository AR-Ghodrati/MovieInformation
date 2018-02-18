package com.ar.movieinformation;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class fullmovieinfo extends AppCompatActivity {

    TextView Name,farsi;
    String Years,Names,Dirs,Casts,Links,Datas,Simple,small,farsiname="",Story,Style="",MOVIEtime="",award="",naghed="";
    String download_hd_link,download_fhd_link,download_subtitle;
    boolean IsDouble=false;
    ImageView MovieImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullmovieinfo);
        Name=(TextView)findViewById(R.id.moviename) ;
        MovieImage=(ImageView)findViewById(R.id.toolbarImage) ;
        farsi=(TextView)findViewById(R.id.moviefarsiname) ;
        ViewPager pager;
        TabLayout tabLayout;
        pager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout=(TabLayout) findViewById(R.id.tabs);
        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        tabLayout.setupWithViewPager(pager);
        tabLayout.getTabAt(0).setText("اطلاعات امتیازی");
        tabLayout.getTabAt(1).setText("داستان فیلم");
        tabLayout.getTabAt(2).setText("اطلاعات فیلم ");
        tabLayout.getTabAt(3).setText("نقد فیلم");
        tabLayout.getTabAt(4).setText("لینک دانلود");

        Show();
    }
    private void Show()
    {
        Years = getIntent().getExtras().getString("Movie_year");
        Names=getIntent().getExtras().getString("Movie_name");
        Dirs=getIntent().getExtras().getString("MovieDirector_name");
        Casts=getIntent().getExtras().getString("Actors_Actress");
        Links=getIntent().getExtras().getString("Movie_IMDB_link");
        Datas=getIntent().getExtras().getString("count");
        Simple=getIntent().getExtras().getString("IMDB_RANK_simple");
        small=getIntent().getExtras().getString("id");
        farsiname=getIntent().getExtras().getString("Movie_name_farsi");
        if(farsiname != null)
            farsi.setText(farsiname);
            Name.setText(Names);
        Name.setGravity(Gravity.CENTER);
        farsi.setGravity(Gravity.CENTER);
        SQLiteDatabase temp;
        database movieDB = new database(getApplicationContext(),"Movie_db", null,1);
        temp=movieDB.getReadableDatabase();
        Cursor cursor = temp.query("MOVIEFARSIDATA2", new String[]{"Movie_ENname", "Movie_time", "Movie_Style", "Movie_Award","MovieStory", "MovieQUPIC","Movie_Naghed","Download_movie_link_720p","Download_movie_link_1080p","Download_movie_subtitle_link","isdubled"}, null, null, null, null, null);
        while (cursor.moveToNext()) {
            if(cursor.getString(cursor.getColumnIndex("Movie_ENname")).lastIndexOf(0)==' ') {
                Story=cursor.getString(cursor.getColumnIndex("MovieStory"));
                award=cursor.getString(cursor.getColumnIndex("Movie_Award"));
                Style=cursor.getString(cursor.getColumnIndex("Movie_Style"));
                MOVIEtime=cursor.getString(cursor.getColumnIndex("Movie_time"));
                Names = cursor.getString(cursor.getColumnIndex("Movie_ENname")).trim().substring(0, cursor.getString(cursor.getColumnIndex("Movie_ENname")).length() - 1);
            }
            else {
                String name = cursor.getString(cursor.getColumnIndex("Movie_ENname")).trim();
                if (Names != null && Names.equalsIgnoreCase(name)) {
                    Story=cursor.getString(cursor.getColumnIndex("MovieStory"));
                    award=cursor.getString(cursor.getColumnIndex("Movie_Award"));
                    Style=cursor.getString(cursor.getColumnIndex("Movie_Style"));
                    MOVIEtime=cursor.getString(cursor.getColumnIndex("Movie_time"));
                    naghed=cursor.getString(cursor.getColumnIndex("Movie_Naghed"));
                    download_hd_link=cursor.getString(cursor.getColumnIndex("Download_movie_link_720p"));
                    download_fhd_link=cursor.getString(cursor.getColumnIndex("Download_movie_link_1080p"));
                    download_subtitle=cursor.getString(cursor.getColumnIndex("Download_movie_subtitle_link"));
                    String check=cursor.getString(cursor.getColumnIndex("isdubled"));
                    IsDouble = !check.equalsIgnoreCase("false");
                    Names = name;
                    break;
                }
            }
        }
        if(award.trim().equals(""))
            award="جوایز فیلم یافت نشد";
        if(Style.trim().equals(""))
            Style=" سبک فیلم یافت نشد ";
        if(MOVIEtime.trim().equals(""))
            MOVIEtime=" زمان فیلم یافت نشد ";
        File file = null;
        if (Names != null) {
            file = new File(getApplicationContext().getFilesDir().getPath() + "/" + Names.trim());
        }
        assert file != null;
        if(file.exists()) {
            final Bitmap bmp = BitmapFactory.decodeFile(getApplicationContext().getFilesDir().getPath() + "/" + Names.trim());
            if (file.length() > 10000) {
                MovieImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                MovieImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                   /* PhotoViewAttacher pAttacher;
                    pAttacher = new PhotoViewAttacher(MovieImage);
                    pAttacher.setZoomable(true);
                    pAttacher.setZoomTransitionDuration(1000);
                    pAttacher.update();
                    */
                        Intent i=new Intent(getApplicationContext(),FullScreenImageActivity.class);
                        i.putExtra("BitmapImage",getApplicationContext().getFilesDir().getPath() + "/" + Names.trim());
                        i.putExtra("name",Names);
                        startActivity(i);
                    }
                });
            } else {
                MovieImage.setScaleType(ImageView.ScaleType.CENTER);
                MovieImage.setScaleX(4);
                MovieImage.setScaleY(4);
                MovieImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                     Toast.makeText(getApplicationContext(),"کیفیت پوستر برای بزرگنمایی مناسب نیست",Toast.LENGTH_SHORT).show();
                    }
                });

            }
            MovieImage.setImageBitmap(bmp);

        }
        else {
            Bitmap bmp = BitmapFactory.decodeResource(getResources(),R.drawable.icon);
            MovieImage.setImageBitmap(Bitmap.createScaledBitmap(bmp,200,200,false));
            Toast.makeText(getApplicationContext(), "عکس برای این فیلم پیدا نشد،لطفا لیست را بروز رسانی کنید", Toast.LENGTH_SHORT).show();
        }

    }
    private class MyPagerAdapter extends FragmentPagerAdapter {

        MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            switch(pos) {

                case 0: {
                    return firstfragmet.setdataandshow(Simple,small,Datas,Links);
                }
                case 1:
                {
                    return SecondFragment.setdataandshow(Story);
                }
                case 2:
                {
                    return ThirdFragment.setdataandshow(Dirs,Casts,Years,award,MOVIEtime,Style);
                }
                case 3:
                {
                    return FourFragment.setdataandshow(naghed);
                }
                case 4:
                {
                    return FiveFragment.setdataandshow(download_hd_link,download_fhd_link,download_subtitle,IsDouble);
                }
                default: return ThirdFragment.setdataandshow(Dirs,Casts,Years,award,MOVIEtime,Style);
            }
        }

        public int getCount() {
            return 5;
        }
    }
}
