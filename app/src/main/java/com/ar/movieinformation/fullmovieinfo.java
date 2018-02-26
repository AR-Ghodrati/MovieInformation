package com.ar.movieinformation;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.ar.movieinformation.OMDB.Model.ShortPlot;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;

public class fullmovieinfo extends AppCompatActivity {

    TextView Name,farsi;
    ImageView MovieImage;
    Movie movie;
    ShortPlot plot;
    boolean haveDownloadLink =true,haveNaghed=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("loaded","onCreate");
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_fullmovieinfo);
        Name=(TextView)findViewById(R.id.moviename) ;
        MovieImage=(ImageView)findViewById(R.id.toolbarImage) ;
        farsi=(TextView)findViewById(R.id.moviefarsiname) ;
        ViewPager pager;
        TabLayout tabLayout;
        pager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout=(TabLayout) findViewById(R.id.tabs);


        int MoviePosition=getIntent().getExtras().getInt("MoviePosition",-1);
        String type=getIntent().getExtras().getString("MovieListType","");

        if(MoviePosition!=-1)
        {
            if(type.equals("")) {
                movie = movie_list.MovieListTemp.get(MoviePosition);
                plot = movie_list.MovieData.get(movie.getTitle());
            }
            else if(type.equals("Dir"))
            {
                movie = movie_list.moviesdir.get(MoviePosition);
                plot = movie_list.MovieData.get(movie.getTitle());
            }
            else {
                movie = movie_list.SearchObj.get(MoviePosition);
                plot = movie_list.MovieData.get(movie.getTitle());
            }
            try{
                if(plot.getNaghed()==null || plot.getNaghed().equals(""))
                    haveNaghed=false;
                if(plot.getDownloadLinks()!=null && plot.getDownloadLinks().size()==0)
                    haveDownloadLink =false;


                if(!haveDownloadLink && !haveNaghed)
                {
                    pager.setAdapter(new MyPagerAdapterWithoutDownloadLink_Naghed(getSupportFragmentManager()));
                    tabLayout.setupWithViewPager(pager);
                    tabLayout.getTabAt(0).setText("اطلاعات امتیازی");
                    tabLayout.getTabAt(1).setText("اطلاعات فیلم");
                    tabLayout.getTabAt(2).setText("داستان فیلم");

                }
                else  if (haveDownloadLink && haveNaghed) {
                    pager.setAdapter(new MyPagerAdapterAll(getSupportFragmentManager()));
                    tabLayout.setupWithViewPager(pager);
                    tabLayout.getTabAt(0).setText("اطلاعات امتیازی");
                    tabLayout.getTabAt(1).setText("اطلاعات فیلم");
                    tabLayout.getTabAt(2).setText("داستان فیلم");
                    tabLayout.getTabAt(3).setText("نقد فیلم");
                    tabLayout.getTabAt(4).setText("لینک دانلود");
                }
                else if(!haveDownloadLink)
                {
                    pager.setAdapter(new MyPagerAdapterWithoutDownloadLink(getSupportFragmentManager()));
                    tabLayout.setupWithViewPager(pager);
                    tabLayout.getTabAt(0).setText("اطلاعات امتیازی");
                    tabLayout.getTabAt(1).setText("اطلاعات فیلم");
                    tabLayout.getTabAt(2).setText("داستان فیلم");
                    tabLayout.getTabAt(3).setText("نقد فیلم");
                    ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(4).setVisibility(View.GONE);
                }
                else if (!haveNaghed) {
                    pager.setAdapter(new MyPagerAdapterWithoutNaghed(getSupportFragmentManager()));
                    tabLayout.setupWithViewPager(pager);
                    tabLayout.getTabAt(0).setText("اطلاعات امتیازی");
                    tabLayout.getTabAt(1).setText("اطلاعات فیلم");
                    tabLayout.getTabAt(2).setText("داستان فیلم");
                    tabLayout.getTabAt(3).setText("لینک دانلود");
                    ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(3).setVisibility(View.GONE);
                }
            }
            catch (Exception ignored)
            {

            }
            Show();
        }


    }
    private void Show()
    {

        assert movie != null;
            farsi.setText(movie.getTitleFa());
            Name.setText(movie.getTitle());
        Name.setGravity(Gravity.CENTER);
        Name.setSelected(true);
        farsi.setGravity(Gravity.CENTER);
        farsi.setSelected(true);

                MovieImage.setOnClickListener(v -> {

                    Intent i=new Intent(getApplicationContext(),FullScreenImageActivity.class);
                    i.putExtra("BitmapImage",getApplicationContext().getFilesDir().getPath() + "/" + movie.getTitle().trim());
                    i.putExtra("name",movie.getTitle());
                    startActivity(i);
                });
            File file=new File(getApplicationContext().getFilesDir().getPath() + "/" + movie.getTitle().trim());
            Picasso.with(getApplicationContext())
                    .load(file)
                    .memoryPolicy(MemoryPolicy.NO_CACHE,MemoryPolicy.NO_STORE)
                    .transform(new RoundedTransformation(30,5))
                    .error(R.drawable.icon)
                    .fit()
                    .centerCrop()
                    .into(MovieImage);


    }
    private class MyPagerAdapterAll extends FragmentPagerAdapter {

        MyPagerAdapterAll(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {

            switch(pos) {
                case 0: {
                    return firstfragmet.setdataandshow(movie,plot);
                }
                case 1:
                {
                    return ThirdFragment.setdataandshow(movie,plot);
                }
                case 2:
                {
                    return SecondFragment.setdataandshow(plot);

                }
                case 3:
                {
                    return FourFragment.setdataandshow(plot);
                }
                case 4:
                {
                    return FiveFragment.setdataandshow(plot);
                }
                default: return ThirdFragment.setdataandshow(movie,plot);
            }
        }

        public int getCount() {
            return 5;
        }
    }
    private class MyPagerAdapterWithoutNaghed extends FragmentPagerAdapter {

        MyPagerAdapterWithoutNaghed(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {

            switch(pos) {
                case 0: {
                    return firstfragmet.setdataandshow(movie,plot);
                }
                case 1:
                {
                    return ThirdFragment.setdataandshow(movie,plot);
                }
                case 2:
                {
                    return SecondFragment.setdataandshow(plot);

                }

                case 3:
                {
                    return FiveFragment.setdataandshow(plot);
                }
                default: return ThirdFragment.setdataandshow(movie,plot);
            }
        }

        public int getCount() {
            return 4;
        }
    }
    private class MyPagerAdapterWithoutDownloadLink extends FragmentPagerAdapter {

        MyPagerAdapterWithoutDownloadLink(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {

            switch(pos) {
                case 0: {
                    return firstfragmet.setdataandshow(movie,plot);
                }
                case 1:
                {
                    return ThirdFragment.setdataandshow(movie,plot);
                }
                case 2:
                {
                    return SecondFragment.setdataandshow(plot);

                }
                case 3:
                {
                    return FourFragment.setdataandshow(plot);
                }

                default: return ThirdFragment.setdataandshow(movie,plot);
            }
        }

        public int getCount() {
            return 4;
        }
    }
    private class MyPagerAdapterWithoutDownloadLink_Naghed extends FragmentPagerAdapter {

        MyPagerAdapterWithoutDownloadLink_Naghed(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {

            switch(pos) {
                case 0: {
                    return firstfragmet.setdataandshow(movie,plot);
                }
                case 1:
                {
                    return ThirdFragment.setdataandshow(movie,plot);
                }
                case 2:
                {
                    return SecondFragment.setdataandshow(plot);

                }


                default: return ThirdFragment.setdataandshow(movie,plot);
            }
        }

        public int getCount() {
            return 3;
        }
    }


}
