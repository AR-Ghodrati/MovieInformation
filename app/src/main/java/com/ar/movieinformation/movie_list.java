package com.ar.movieinformation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ar.movieinformation.OMDB.Model.ShortPlot;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.gson.Gson;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;

public class movie_list extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
{
    int mood=0;
    SearchView searchView;
    RecyclerView Movielist;
    SwipeRefreshLayout swipeRefreshLayout;
    int Check=0;
   /* String[] MOVIEEN = new String[250];
    String[]years=new String[250];
    String[] MOVIEFA = new String[250];
    String [] ENpic=new String[250];
    String[]countrank=new String[250];
    boolean[]checkdoublicate=new boolean[250];
    */
   boolean[]checkdoublicate=new boolean[250];
    boolean dirshow=false;
    boolean firstdirshow=false;
    boolean isListVisible=true;
    RecyclerTouchListener listener1;
    RecyclerTouchListener listener2;
    RecyclerTouchListener listener3;
    RecyclerTouchListener listener4;
    RecyclerTouchListener listener5;
    RecyclerTouchListener listener6;
    RecyclerTouchListener listener7;
    RecyclerTouchListener listener8;
    NotificationCompat.Builder builder;
    Notification.Builder buldernoti;
    private RequestQueue requestQueue;
    boolean checkpic = false;
    int AsyncTaskcounter = 0;
    int currnetprogress = 0;
    public static List<Movie> MovieList=new ArrayList<>();
    public static Map<String,Movie> MovieListOld=new HashMap<>();
    public static List<Movie> MovieListTemp=new ArrayList<>();
    public static Map<String,ShortPlot>MovieData=new HashMap<>();

    Gson gson;
    Context context;
    int GetOMDBDATASCOUNTER=0;
    int GetFASTORYCOUNTER=0;
    int GetFATITLECOUNTER=0;
    //for apply Pic Change
    /*
    @Override
    public void setContentView(View view)
    {
        super.setContentView(view);
        SharedPreferences getshared=getSharedPreferences("movieinfosh",MODE_PRIVATE);
        float size=Integer.parseInt(getshared.getString("font_size","18"));
        FontChangeCrawler fontChanger = new FontChangeCrawler(getAssets(), "fonts/"+getshared.getString("font_type","SERIF")+".ttf",size);        fontChanger.replaceFonts((ViewGroup)this.findViewById(android.R.id.content));
    }
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final SharedPreferences getdata=getSharedPreferences("movieinfosh",MODE_PRIVATE) ;
        super.onCreate(savedInstanceState);
        gson=new Gson();
        context=this;
        requestQueue= Volley.newRequestQueue(this);
      //  new LoadData().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        /*
        if(getdata.getString("apptheme_type","AppTheme").equalsIgnoreCase("قهوه ای"))
            setTheme(R.style.AppTheme);
        else if(getdata.getString("apptheme_type","AppTheme").equalsIgnoreCase("مشکی"))
            setTheme(R.style.AppTheme1);
            */
        setContentView(R.layout.activity_movie_list);

        Movielist = (RecyclerView) findViewById(R.id.Movielist);
        swipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.swipeRefreshLayout);
        // Refresh items
        swipeRefreshLayout.setOnRefreshListener(this::refreshItems);




        Movielist.setHasFixedSize(true);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getApplicationContext());
        Movielist.setLayoutManager(mLinearLayoutManager);
        Movielist.addItemDecoration(new MyDividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL, 16));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Menu menu=navigationView.getMenu();
        View header=navigationView.getHeaderView(0);
        TextView version=(TextView)header.findViewById(R.id.version);
        PackageManager manager = getApplicationContext().getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(
                    getApplicationContext().getPackageName(), 0);
            String versions = info.versionName;
            version.setText(" نسخه : "+versions);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        SharedPreferences.Editor editor=  getSharedPreferences("movieinfosh",MODE_PRIVATE).edit();
        Calendar c=Calendar.getInstance();
        int yearchange=c.get(Calendar.YEAR)-getdata.getInt("DBupdate_version_Year",2017);
        int daychange=c.get(Calendar.DAY_OF_YEAR)-getdata.getInt("DBupdate_version_Day",305);
        String lastupdate="";
        if(yearchange>0)
             lastupdate = " " +((yearchange*360)+daychange)+"  روز پیش  ";
        else
            if(daychange>0)
             lastupdate = " " +daychange+"  روز پیش  ";
        else
            lastupdate = " "+" امروز ";
        String text="لیست تغییرات فیلم ها ";
        String dta="آخرین بروز رسانی:";
         MenuItem myItem1 = menu.findItem(R.id.changedata);
         myItem1.setTitle(text+"\n"+dta+lastupdate+"\n"+"در تاریخ: "+getdata.getString("DBupdate_version","2017-10-26 12:50"));
        if(!getdata.getBoolean("IsFirst_AlertShow",false))
        {
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setMessage("در صورت تمایل از کارکرد برنامه می توانید بخش درباره برنامه را مشاهده کنید ");
            builder.setTitle("اطلاعات فیلم");
            builder.setCancelable(true);
            builder.setPositiveButton("درباره برنامه", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent=new Intent(movie_list.this,aboutus_myket.class);
                    startActivity(intent);
                }
            });
            builder.setNegativeButton("تمایلی ندارم", (dialog, which) -> {

            });
            builder.show();
            editor.putBoolean("IsFirst_AlertShow",true);
            editor.apply();
        }
        ReadData();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
      //  ReadData();
    }

    void refreshItems() {
        // Load items
        // ...
        //Toast.makeText(getApplicationContext(),"درحال بروز رسانی...",Toast.LENGTH_LONG).show();
        UpdateList();
    }

    void onItemsLoadComplete() {
        // Update the adapter and notify data set changed
        // ...
        Movielist.getAdapter().notifyDataSetChanged();

        // Stop refresh animation
        swipeRefreshLayout.setRefreshing(false);
    }
    @Override
    protected void onDestroy() {
        new SaveData().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        super.onDestroy();
    }
    @SuppressLint("StaticFieldLeak")
    class SaveData extends AsyncTask<Void,Void,Void>
    {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                SharedPreferences.Editor AllData=context.getSharedPreferences("MOVIE_INFO_DATA_PREF",MODE_PRIVATE).edit();
                AllData.putString("MOVIES",gson.toJson(MovieList));
                AllData.apply();

                SharedPreferences.Editor EXTRADATA=context.getSharedPreferences("MOVIE_INFO_AD_PREF",MODE_PRIVATE).edit();
                EXTRADATA.putString("MOVIES",gson.toJson(MovieData));
                EXTRADATA.apply();

                SharedPreferences.Editor editor = context.getSharedPreferences("MOVIE_INFO_OLD_DATA_PREF", MODE_PRIVATE).edit();
                editor.putString("MOVIES", gson.toJson(MovieListOld));
                editor.apply();

            }
            catch (Exception ignored)
            {

            }
            finally {
                Log.e("SaveData","done");
            }

            return null;
        }
    }

    private void ReadData() {
        if(listener1!=null)
            Movielist.removeOnItemTouchListener(listener1);
        if(listener2!=null)
            Movielist.removeOnItemTouchListener(listener2);
        if(listener3!=null)
            Movielist.removeOnItemTouchListener(listener3);
        if(listener4!=null)
            Movielist.removeOnItemTouchListener(listener4);
        if(listener5!=null)
            Movielist.removeOnItemTouchListener(listener5);
        if(listener6!=null)
            Movielist.removeOnItemTouchListener(listener6);
        if(listener7!=null)
            Movielist.removeOnItemTouchListener(listener7);
        if(listener8!=null)
            Movielist.removeOnItemTouchListener(listener8);
        Thread tread = new Thread(() -> {
        /*    for (int i = 0; i <MovieList.size() ; i++) {
                MovieData.get(MovieList.get(i).getTitle().trim()).setDownloadLinks(MovieList.get(i).getDownloadLinks());
                MovieData.get(MovieList.get(i).getTitle().trim()).setDubled(MovieList.get(i).isDubled());
                MovieData.get(MovieList.get(i).getTitle().trim()).setSubTitle(MovieList.get(i).getSubTitle());
               // Log.e("Naged",MovieData.get(MovieList.get(i).getTitle().trim()).getNaghed()+"");
            }
            */
            /*
            MovieList.clear();
            Vector<String>yearvect=new Vector<>();
            database db = new database(getApplicationContext(), "Movie_db", null, 1);
            SQLiteDatabase temp = db.getReadableDatabase();
            int counter = 0;
            String Movie_Rank, Movie_farsiname , Movie_year, Movie_name, MovieDirector_name, Movie_IMDB_link, Movie_picture_link, IMDB_RANK_simple, Actors_Actress, IMDB_RANK_full;
            final Vector<Movie> movies = new Vector<>();
            Cursor cursor = temp.query("MOVIEDATA", new String[]{"Movie_Rank", "IMDB_RANK_simple", "Movie_year", "Movie_name", "Movie_farsi_name", "MovieDirector_name", "Actors_Actress", "IMDB_RANK_full", "Movie_IMDB_link", "Movie_picture_link"}, null, null, null, null, null);
            cursor.moveToFirst();
            cursor.moveToNext();
           /* while (!cursor.isAfterLast()) {
                Movie tempMovie = new Movie();
                Movie_name = cursor.getString(cursor.getColumnIndex("Movie_name"));
                Movie_Rank = cursor.getString(cursor.getColumnIndex("Movie_Rank"));
                Movie_year = cursor.getString(cursor.getColumnIndex("Movie_year"));
                MovieDirector_name = cursor.getString(cursor.getColumnIndex("MovieDirector_name"));
                Movie_IMDB_link = cursor.getString(cursor.getColumnIndex("Movie_IMDB_link"));
                Movie_picture_link = cursor.getString(cursor.getColumnIndex("Movie_picture_link"));
                IMDB_RANK_simple = cursor.getString(cursor.getColumnIndex("IMDB_RANK_simple"));
                Actors_Actress = cursor.getString(cursor.getColumnIndex("Actors_Actress"));
                IMDB_RANK_full = cursor.getString(cursor.getColumnIndex("IMDB_RANK_full"));
                Movie_farsiname= cursor.getString(cursor.getColumnIndex("Movie_farsi_name"));
                if(Movie_name.equals("The Lion King"))
                tempMovie.setTitleFa("شیر شاه");
                else tempMovie.setTitleFa(Movie_farsiname);
                tempMovie.setTitle(Movie_name);
                tempMovie.setTop_250_Rank(Movie_Rank);
                tempMovie.setIMDBlink(Movie_IMDB_link);
                tempMovie.getExtraInfo().setPoster(Movie_picture_link);
                tempMovie.setImdbRating(IMDB_RANK_simple);
                tempMovie.setIMDbRankFull(IMDB_RANK_full);
                tempMovie.getExtraInfo().setDirector(MovieDirector_name);
                tempMovie.setYear(Movie_year);
               // tempMovie.setMovie_rank("" + (counter + 1));
                //countrank[counter]=""+tempMovie.countranking();
                tempMovie.setSavedCountRanking(tempMovie.countranking()+"");
                MovieList.add(tempMovie);
                movies.addElement(tempMovie);
                counter++;
                StringRequest stringRequest;
                cursor.moveToNext();
            }
            Cursor cursor1 = temp.query("MOVIEFARSIDATA1", new String[]{"Movie_farsi_name", "link", "ENmoviename"}, null, null, null, null, null);
            cursor1.moveToFirst();
            int i=0;
            while (!cursor1.isAfterLast())
            {
                String FA=cursor1.getString(cursor1.getColumnIndex("Movie_farsi_name"));
                String En=cursor1.getString(cursor1.getColumnIndex("ENmoviename"));

                for (int j = 0; j < MovieList.size(); j++) {
                    if(MovieList.get(i).getTitle().equalsIgnoreCase(En))
                    {
                        MovieList.get(i).setTitleFa(FA);
                        break;
                    }
                }
                cursor1.moveToNext();
            }

            database db = new database(getApplicationContext(), "Movie_db", null, 1);
            SQLiteDatabase temp = db.getReadableDatabase();
            Cursor cursor2 = temp.query("MOVIEFARSIDATA2", new String[]{"Movie_ENname", "Movie_time", "Movie_Style","Movie_Award", "MovieStory", "MovieQUPIC","Movie_Naghed", "Download_movie_link_720p", "Download_movie_link_1080p","Download_movie_subtitle_link", "isdubled"}, null, null, null, null, null);
            cursor2.moveToFirst();
            int w=0;
            while (!cursor2.isAfterLast())
            {
                String En=cursor2.getString(cursor2.getColumnIndex("Movie_ENname"));
                String Movie_Award=cursor2.getString(cursor2.getColumnIndex("Movie_Award"));
                String MovieStory=cursor2.getString(cursor2.getColumnIndex("MovieStory"));
                String Movie_Naghed=cursor2.getString(cursor2.getColumnIndex("Movie_Naghed"));
                String Download_movie_link_720p=cursor2.getString(cursor2.getColumnIndex("Download_movie_link_720p"));
                String Download_movie_link_1080p=cursor2.getString(cursor2.getColumnIndex("Download_movie_link_1080p"));
                String isdubled=cursor2.getString(cursor2.getColumnIndex("isdubled"));
                String Download_movie_subtitle_link=cursor2.getString(cursor2.getColumnIndex("Download_movie_subtitle_link"));

                for (int k = 0; k < MovieList.size(); k++) {
                    if(MovieList.get(k).getTitle().trim().equalsIgnoreCase(En.trim()))
                    {
                        MovieList.get(k).setNaghed(Movie_Naghed);

                        ArrayList<String>list=new ArrayList<>();
                        list.add(Download_movie_link_720p);
                        list.add(Download_movie_link_1080p);
                        MovieList.get(k).setDownloadLinks(list);
                        MovieList.get(k).setSubTitle(Download_movie_subtitle_link);
                        if(isdubled.equals("false"))
                            MovieList.get(k).setDubled(Boolean.FALSE);
                        else MovieList.get(k).setDubled(Boolean.TRUE);
                        break;
                    }

                }
                if( MovieData.get(En.trim())!=null && Movie_Award!=null && !Movie_Award.equals("") &&  Movie_Award.length()>5) {
                    MovieData.get(En.trim()).setAwards(Movie_Award);
                    Log.e("Movie_Award",Movie_Award);
                }
                if(MovieData.get(En.trim())!=null && MovieStory!=null && !MovieStory.equals("") &&  MovieStory.length()>20) {
                    MovieData.get(En.trim()).setFaPlot(MovieStory);
                    MovieData.get(En.trim()).setPlot(null);
                    Log.e("MovieStory",MovieStory);
                }
                w++;
                cursor2.moveToNext();


            }
          //
             // getOMDBDatas();

         //  */

            //MovieList.remove(MovieList.size()-1);
           // Toast.makeText(getApplicationContext(),MovieList.size()+"",Toast.LENGTH_LONG).show();

            MovieListTemp.clear();
            MovieListTemp.addAll(MovieList);

            Vector<String>yearvect=new Vector<>();
            if (Check == 0) {

            } else if (Check == 1) {
                for (int i = 0; i < MovieListTemp.size(); i++)
                    for (int j = i + 1; j < MovieListTemp.size(); j++)
                        if (MovieListTemp.get(j).getYear().compareToIgnoreCase(MovieListTemp.get(i).getYear()) > 0) {
                            Movie t = MovieListTemp.get(i);
                            MovieListTemp.set(i, MovieListTemp.get(j));
                            MovieListTemp.set(j, t);
                        }
                for (int i = 0; i < MovieListTemp.size(); i++) {
                    if(!yearvect.contains(MovieListTemp.get(i).getYear())) {
                        checkdoublicate[i] = true;
                        yearvect.addElement(MovieListTemp.get(i).getYear());
                    }
                }
                mood=1;
            }
            /*else if (Check == 2) {
                for (int i = 0; i < MovieListTemp.size(); i++)
                    for (int j = i + 1; j < MovieListTemp.size(); j++)
                        if (MovieListTemp.get(j).getExtraInfo().getDirector().compareToIgnoreCase(MovieListTemp.get(j).getExtraInfo().getDirector()) < 0) {
                            Movie t = MovieListTemp.get(i);
                            MovieListTemp.set(i, MovieListTemp.get(j));
                            MovieListTemp.set(j, t);
                        }
            }
            */else if (Check == 3) {
                for (int i = 0; i < MovieListTemp.size(); i++)
                    for (int j = i + 1; j < MovieListTemp.size(); j++)
                        if (MovieListTemp.get(j).countranking() > MovieListTemp.get(i).countranking()) {
                            Movie t = MovieListTemp.get(i);
                            MovieListTemp.set(i, MovieListTemp.get(j));
                            MovieListTemp.set(j, t);
                        }

            } else if (Check == 4) {
                for (int i = 0; i < MovieListTemp.size(); i++)
                    for (int j = i + 1; j < MovieListTemp.size(); j++)
                        if (MovieListTemp.get(j).getTitle().compareToIgnoreCase(MovieListTemp.get(i).getTitle()) < 0) {
                            Movie t = MovieListTemp.get(i);
                            MovieListTemp.set(i, MovieListTemp.get(j));
                            MovieListTemp.set(j, t);
                        }

            }
           // temp.close();
          //  db.close();
           // Toast.makeText(getApplicationContext(),MovieListTemp.size()+"",Toast.LENGTH_LONG).show();
            custom_reycler_view movielist = new custom_reycler_view(MovieListTemp,checkdoublicate,mood);

            AlphaInAnimationAdapter alphaInAnimationAdapter=new AlphaInAnimationAdapter(movielist,0.5f);
            Movielist.setAdapter(alphaInAnimationAdapter);
           // Movielist.removeOnItemTouchListener();
            listener1=new RecyclerTouchListener(getApplicationContext(), Movielist, new ClickListner() {
                @Override
                public void onClick(View view, int position) {

                    Intent gotopage = new Intent(movie_list.this, fullmovieinfo.class);
                    gotopage.putExtra("MOVIE",MovieListTemp.get(position));
                    gotopage.putExtra("PLOT",MovieData.get(MovieListTemp.get(position).getTitle()));
                    startActivity(gotopage);

                }

                @Override
                public void onLongClick(View view, int position) {

                }
            });
            if(listener8!=null)
                Movielist.removeOnItemTouchListener(listener8);
            if(listener2!=null)
                Movielist.removeOnItemTouchListener(listener2);
            if(listener3!=null)
                Movielist.removeOnItemTouchListener(listener3);
            if(listener4!=null)
                Movielist.removeOnItemTouchListener(listener4);
            if(listener5!=null)
                Movielist.removeOnItemTouchListener(listener5);
            if(listener6!=null)
                Movielist.removeOnItemTouchListener(listener6);
            if(listener7!=null)
                Movielist.removeOnItemTouchListener(listener7);
            Movielist.addOnItemTouchListener(listener1);
        });
        tread.run();
    }

    private void changelist()
    {
        final SharedPreferences.Editor shared=getSharedPreferences("movieinfosh",MODE_PRIVATE).edit();
        final SharedPreferences getstatus=getSharedPreferences("movieinfosh",MODE_PRIVATE) ;
        final String[]changerank=new String[MovieList.size()];
        final String[]chngeantiaz=new String[MovieList.size()];
        final int[] counterchange = {0};
        Thread thread=new Thread(() -> {
            if(!getstatus.getBoolean("Changelist_alert",false)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(movie_list.this);
                builder.setCancelable(true);
                String tempfa="( باتوجه به زمان آخرین بروز رسانی بهتر است لیست را هفته ای یک بار بروز کنید)";
                builder.setMessage(" شما میتوانید جدیدترین تغییرات را با بروز رسانی لیست در این بخش مشاهده کنید"+"\n"+tempfa);
                builder.setTitle("لیست تغییرات");
                builder.setPositiveButton("فهمیدم", (dialog, which) -> {
                    shared.putBoolean("Changelist_alert", true);
                    shared.apply();
                });
                builder.show();
            }
            for (int i = 0; i <MovieList.size() ; i++) {
                Movie oldrank=MovieListOld.get(MovieList.get(i).getTitle().trim());
                if(oldrank!=null) {
                    chngeantiaz[i]=""+(Double.parseDouble(MovieList.get(i).getImdbRating())-Double.parseDouble(MovieListOld.get(MovieList.get(i).getTitle().trim()).getImdbRating()));
                    changerank[i] = "" + (Double.parseDouble(MovieList.get(i).getTop_250_Rank()) - Double.parseDouble(MovieListOld.get(MovieList.get(i).getTitle().trim()).getTop_250_Rank()));
                }
                else {
                    chngeantiaz[i]=null;
                    changerank[i] = null;
                }

            }

            moviecustomlist_change moviecustomlist_change=new moviecustomlist_change(MovieList,changerank,chngeantiaz);
            AlphaInAnimationAdapter alphaInAnimationAdapter=new AlphaInAnimationAdapter(moviecustomlist_change,0.5f);
            Movielist.setAdapter(alphaInAnimationAdapter);
            listener2=new RecyclerTouchListener(getApplicationContext(), Movielist, new ClickListner() {
                @Override
                public void onClick(View view, int position) {
                    Intent gotopage = new Intent(movie_list.this, fullmovieinfo.class);
                    gotopage.putExtra("MOVIE",MovieList.get(position));
                    gotopage.putExtra("PLOT",MovieData.get(MovieList.get(position).getTitle()));

                    startActivity(gotopage);
                }

                @Override
                public void onLongClick(View view, int position) {

                }
            });
            if(listener1!=null)
                Movielist.removeOnItemTouchListener(listener1);
            if(listener8!=null)
                Movielist.removeOnItemTouchListener(listener8);
            if(listener3!=null)
                Movielist.removeOnItemTouchListener(listener3);
            if(listener4!=null)
                Movielist.removeOnItemTouchListener(listener4);
            if(listener5!=null)
                Movielist.removeOnItemTouchListener(listener5);
            if(listener6!=null)
                Movielist.removeOnItemTouchListener(listener6);
            if(listener7!=null)
                Movielist.removeOnItemTouchListener(listener7);
            Movielist.addOnItemTouchListener(listener2);
        });
        thread.run();

    }
    private void Dirshow()
    {

        final Vector<String>dirnames=new Vector<>();
        for (int i = 0; i <MovieList.size() ; i++) {
            String dirname=MovieData.get(MovieList.get(i).getTitle().trim()).getDirector();
            if (!dirnames.contains(dirname))
                dirnames.addElement(dirname);
        }

        final moviecustomlist_directors moviecustomlist_directors=new moviecustomlist_directors(dirnames);
        AlphaInAnimationAdapter alphaInAnimationAdapter=new AlphaInAnimationAdapter(moviecustomlist_directors);
        Movielist.setAdapter(alphaInAnimationAdapter);
        listener3=new RecyclerTouchListener(getApplicationContext(), Movielist, new ClickListner() {
            @Override
            public void onClick(View view, int position) {
                dirshow=true;
                setTitle("آثار "+dirnames.elementAt(position));
                final Vector<Movie> moviesdir = new Vector<>();
                for (int i = 0; i <MovieList.size() ; i++) {
                   String MovieDirector_name = MovieData.get(MovieList.get(i).getTitle().trim()).getDirector();
                    if (MovieDirector_name.equals(dirnames.elementAt(position))) {
                        moviesdir.addElement(MovieList.get(i));
                    }
                }




                moviecustomlist movielist = new moviecustomlist(moviesdir);
                AlphaInAnimationAdapter alphaInAnimationAdapter=new AlphaInAnimationAdapter(movielist,0.5f);
                Movielist.setAdapter(alphaInAnimationAdapter);
                listener4=new RecyclerTouchListener(getApplicationContext(), Movielist, new ClickListner() {
                    @Override
                    public void onClick(View view, int position) {
                        Intent gotopage = new Intent(movie_list.this, fullmovieinfo.class);
                        gotopage.putExtra("MOVIE",moviesdir.get(position));
                        gotopage.putExtra("PLOT",MovieData.get(moviesdir.get(position).getTitle()));

                        startActivity(gotopage);
                    }

                    @Override
                    public void onLongClick(View view, int position) {

                    }
                });
                if(listener1!=null)
                    Movielist.removeOnItemTouchListener(listener1);
                if(listener2!=null)
                    Movielist.removeOnItemTouchListener(listener2);
                if(listener3!=null)
                    Movielist.removeOnItemTouchListener(listener3);
                if(listener8!=null)
                    Movielist.removeOnItemTouchListener(listener8);
                if(listener5!=null)
                    Movielist.removeOnItemTouchListener(listener5);
                if(listener6!=null)
                    Movielist.removeOnItemTouchListener(listener6);
                if(listener7!=null)
                    Movielist.removeOnItemTouchListener(listener7);
                Movielist.addOnItemTouchListener(listener4);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        });
        if(listener1!=null)
            Movielist.removeOnItemTouchListener(listener1);
        if(listener2!=null)
            Movielist.removeOnItemTouchListener(listener2);
        if(listener8!=null)
            Movielist.removeOnItemTouchListener(listener8);
        if(listener4!=null)
            Movielist.removeOnItemTouchListener(listener4);
        if(listener5!=null)
            Movielist.removeOnItemTouchListener(listener5);
        if(listener6!=null)
            Movielist.removeOnItemTouchListener(listener6);
        if(listener7!=null)
            Movielist.removeOnItemTouchListener(listener7);
        Movielist.addOnItemTouchListener(listener3);
    }



    boolean doubleBackToExitPressedOnce = false;
    boolean doubleBackToExitPressedOncecheck=false;
    @Override
    public void onBackPressed() {
        if (dirshow) {
            Dirshow();
            setTitle("بر اساس آثار کارگردانان   ");
            dirshow=false;
        }
        else {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(movie_list.this);
            SharedPreferences getstatus = getSharedPreferences("movieinfosh", MODE_PRIVATE);
            final SharedPreferences.Editor setstatus = getSharedPreferences("movieinfosh", MODE_PRIVATE).edit();
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (getstatus.getInt("StartupCount", 0) % 3 == 0 && !getstatus.getBoolean("RateState", false)) {
                doubleBackToExitPressedOncecheck = true;
                alertDialog.setMessage("به برنامه امتیاز می دهید؟");
                alertDialog.setPositiveButton("بله", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (checkconectivity()) {

                        Uri uri = Uri.parse("iranapps://app/com.arstudio.movieinformation?a=comment&r=5");
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setPackage("ir.tgbs.android.iranapp");
                        intent.setData(uri);

                     /*
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        String url = "myket://comment?id=com.arstudio.movieinformation";
                        intent.setData(Uri.parse(url));
                      */
                         /*
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setPackage("com.hrm.android.market");
                            intent.setData(Uri.parse("market://rate?id=" + "com.arstudio.movieinformation"));
                          */
                            if (intent.resolveActivity(getPackageManager()) != null) {
                                startActivity(intent);
                                setstatus.putBoolean("RateState", true);
                                setstatus.apply();
                            } else {

                        Toast.makeText(getApplicationContext(), "برنامه ایران اپس بر روی دستگاه شما نصب نیست  ", Toast.LENGTH_LONG).show();
                          /*
                                Uri uri1 = Uri.parse("http://iranapps.ir/app/com.arstudio.movieinformation?a=comment&r=5");
                        Intent intent1 = new Intent(Intent.ACTION_VIEW);
                        intent1.setData(uri1);
                        startActivity(intent1);
                        */
                               // Toast.makeText(getApplicationContext(), "برنامه اول مارکت بر روی دستگاه شما نصب نیست ", Toast.LENGTH_LONG).show();
                                //Toast.makeText(getApplicationContext(),"برنامه مایکت بر روی دستگاه شما نصب نیست ",Toast.LENGTH_LONG).show();
                            }
                        } else
                            Toast.makeText(movie_list.this, "برای امتیاز دادن به اینترنت متصل شوید", Toast.LENGTH_LONG).show();
                    }
                });
                alertDialog.setNegativeButton("بعدا امتیاز می دهم", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                /*alertDialog.setNeutralButton("سایر برنامه ها", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        String url = "http://www.myket.ir/developer/dev-41623/apps";
                        intent.setData(Uri.parse(url));
                        if (intent.resolveActivity(getPackageManager()) != null) {
                            startActivity(intent);
                        }
                    }
                });
                 */
                alertDialog.show();
            } else doubleBackToExitPressedOncecheck = false;
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                if (doubleBackToExitPressedOnce && !doubleBackToExitPressedOncecheck) {
                    super.onBackPressed();
                    return;
                }
                this.doubleBackToExitPressedOnce = true;
                if (!doubleBackToExitPressedOncecheck)
                    Toast.makeText(this, "برای خارج شدن از برنامه دوباره کلیک کنید", Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 2000);
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override

    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.

        SharedPreferences getstatus=getSharedPreferences("movieinfosh",MODE_PRIVATE) ;
        int id = item.getItemId();

        if(id==R.id.UpdateList)
        {
            Intent intent=new Intent(this,DownloadData.class);
            startActivity(intent);
        }
        else if(id==R.id.SortYears) {
            isListVisible=false;
            Check = 1;
            setTitle("لیست براساس سال ساخت");
            android.support.v7.widget.SearchView search=(android.support.v7.widget.SearchView)findViewById(R.id.serachact);
            search.setVisibility(View.VISIBLE);
            ReadData();
        }
        else if(id==R.id.SortRank) {
            isListVisible=true;
            Check = 0;
            mood=0;
            setTitle("لیست بر اساس امتیاز");
            android.support.v7.widget.SearchView search=(android.support.v7.widget.SearchView)findViewById(R.id.serachact);
            search.setVisibility(View.VISIBLE);
            ReadData();
        }
        else if(id==R.id.SortpeopleRanking) {
            isListVisible=false;
            Check = 3;
            mood=0;
            setTitle("لیست بر اساس تعداد امتیاز");
            android.support.v7.widget.SearchView search=(android.support.v7.widget.SearchView)findViewById(R.id.serachact);
            search.setVisibility(View.VISIBLE);
            ReadData();

        }
        else if(id==R.id.SortNames)
        {
            isListVisible=false;
            Check=4;
            mood=0;
            setTitle("لیست بر اساس نام");
            android.support.v7.widget.SearchView search=(android.support.v7.widget.SearchView)findViewById(R.id.serachact);
            search.setVisibility(View.VISIBLE);
            ReadData();

        }
        else if(id==R.id.aboutus)
        {
            isListVisible=false;
            setTitle("درباره برنامه");
            Intent intent=new Intent(this,aboutus_myket.class);
            startActivity(intent);
        }
        else if(id==R.id.changedata)
        {
            if(getstatus.getBoolean("Changelist_alert",false))
            Toast.makeText(getApplicationContext(),"برای مشاهده آخرین تغییرات لیست را بروز کنید",Toast.LENGTH_LONG).show();
            mood=0;
            Check=5;
            setTitle("لیست تغییرات فیلم ها");
            android.support.v7.widget.SearchView search=(android.support.v7.widget.SearchView)findViewById(R.id.serachact);
            search.setVisibility(View.VISIBLE);
            Movielist.removeOnItemTouchListener(listener1);
           changelist();
        }

        else if(id==R.id.sortdir)
        {
            isListVisible=false;
            setTitle("بر اساس آثار کارگردانان     ");
            firstdirshow=true;
            android.support.v7.widget.SearchView search=(android.support.v7.widget.SearchView)findViewById(R.id.serachact);
            search.setVisibility(View.INVISIBLE);
            Check=6;
            Dirshow();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
      public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        MenuItem searchItem = menu.findItem(R.id.serachact);
        searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.serachact));
        MenuItemCompat.setOnActionExpandListener(searchItem, new SearchViewExpandListener(this));
        MenuItemCompat.setActionView(searchItem, searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            Vector<Movie> SeachObj = new Vector<>();
            @Override
            public boolean onQueryTextSubmit(String s) {
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                if (listener1 != null)
                    Movielist.removeOnItemTouchListener(listener1);
                if (listener2 != null)
                    Movielist.removeOnItemTouchListener(listener2);
                if (listener3 != null)
                    Movielist.removeOnItemTouchListener(listener3);
                if (listener4 != null)
                    Movielist.removeOnItemTouchListener(listener4);
                if (listener5 != null)
                    Movielist.removeOnItemTouchListener(listener5);
                if (listener6 != null)
                    Movielist.removeOnItemTouchListener(listener6);
                if (listener7 != null)
                    Movielist.removeOnItemTouchListener(listener7);
                if (listener8 != null)
                    Movielist.removeOnItemTouchListener(listener8);
                SeachObj.clear();
                String text;
                boolean check = false;
                text = newText.toLowerCase(Locale.getDefault());
                if (text.length() == 0) {
                    if (Check != 5)
                        ReadData();
                    else
                        changelist();
                } else {
                    for (int i = 0; i < MovieListTemp.size(); i++) {
                        if (MovieListTemp.get(i).getTitle().trim().toLowerCase(Locale.getDefault()).contains(text)) {
                            {
                                SeachObj.addElement(MovieListTemp.get(i));
                                check = true;
                            }
                        }
                    }

                    for (int i = 0; i < MovieListTemp.size(); i++) {
                        String faName = MovieListTemp.get(i).getTitleFa();
                        if (faName != null && faName.trim().toLowerCase(Locale.getDefault()).contains(text)) {
                            {
                                SeachObj.addElement(MovieListTemp.get(i));
                            }
                        }
                    }
                        if (check) {
                            if (Check != 5) {
                                moviecustomlist movielist = new moviecustomlist(SeachObj);
                                Movielist.setAdapter(movielist);
                                listener5 = new RecyclerTouchListener(getApplicationContext(), Movielist, new ClickListner() {
                                    @Override
                                    public void onClick(View view, int position) {
                                        Intent gotopage = new Intent(movie_list.this, fullmovieinfo.class);
                                        gotopage.putExtra("MOVIE",SeachObj.get(position));
                                        gotopage.putExtra("PLOT",MovieData.get(SeachObj.get(position).getTitle()));
                                        startActivity(gotopage);
                                    }

                                    @Override
                                    public void onLongClick(View view, int position) {

                                    }
                                });
                                Movielist.addOnItemTouchListener(listener5);
                            }
                            if (Check == 5) {
                                final String[]changerank=new String[MovieListTemp.size()];
                                final String[]chngeantiaz=new String[MovieListTemp.size()];
                                for (int j = 0; j <MovieListTemp.size() ; j++) {
                                    Movie oldrank=MovieListOld.get(MovieList.get(j).getTitle().trim());
                                    if(oldrank!=null) {
                                        chngeantiaz[j]=""+(Double.parseDouble(MovieList.get(j).getImdbRating())-Double.parseDouble(MovieListOld.get(MovieList.get(j).getTitle().trim()).getImdbRating()));
                                        changerank[j] = "" + (Double.parseDouble(MovieList.get(j).getTop_250_Rank()) - Double.parseDouble(MovieListOld.get(MovieList.get(j).getTitle().trim()).getTop_250_Rank()));
                                    }
                                    else {
                                        chngeantiaz[j]=null;
                                        changerank[j] = null;
                                    }
                                }
                                moviecustomlist_change moviecustomlist_change = new moviecustomlist_change(SeachObj,changerank,chngeantiaz);
                                Movielist.setAdapter(moviecustomlist_change);
                                listener6 = new RecyclerTouchListener(getApplicationContext(), Movielist, new ClickListner() {
                                    @Override
                                    public void onClick(View view, int position) {
                                        Intent gotopage = new Intent(movie_list.this, fullmovieinfo.class);
                                        gotopage.putExtra("MOVIE",SeachObj.get(position));
                                        gotopage.putExtra("PLOT",MovieData.get(SeachObj.get(position).getTitle()));
                                        startActivity(gotopage);
                                    }

                                    @Override
                                    public void onLongClick(View view, int position) {

                                    }
                                });
                                Movielist.addOnItemTouchListener(listener6);
                            }
                        } else {
                            if (Check != 5) {
                                moviecustomlist movielist = new moviecustomlist(SeachObj);
                                AlphaInAnimationAdapter alphaInAnimationAdapter=new AlphaInAnimationAdapter(movielist);
                                Movielist.setAdapter(alphaInAnimationAdapter);
                                Movielist.setAdapter(movielist);
                                listener7 = new RecyclerTouchListener(getApplicationContext(), Movielist, new ClickListner() {
                                    @Override
                                    public void onClick(View view, int position) {
                                        Intent gotopage = new Intent(movie_list.this, fullmovieinfo.class);
                                        gotopage.putExtra("MOVIE",SeachObj.get(position));
                                        gotopage.putExtra("PLOT",MovieData.get(SeachObj.get(position).getTitle()));
                                        startActivity(gotopage);
                                    }

                                    @Override
                                    public void onLongClick(View view, int position) {

                                    }
                                });
                                Movielist.addOnItemTouchListener(listener7);
                            } else {
                                final String[]changerank=new String[MovieListTemp.size()];
                                final String[]chngeantiaz=new String[MovieListTemp.size()];
                                for (int j = 0; j <MovieListTemp.size() ; j++) {
                                    Movie oldrank=MovieListOld.get(MovieList.get(j).getTitle().trim());
                                    if(oldrank!=null) {
                                        chngeantiaz[j]=""+(Double.parseDouble(MovieList.get(j).getImdbRating())-Double.parseDouble(MovieListOld.get(MovieList.get(j).getTitle().trim()).getImdbRating()));
                                        changerank[j] = "" + (Double.parseDouble(MovieList.get(j).getTop_250_Rank()) - Double.parseDouble(MovieListOld.get(MovieList.get(j).getTitle().trim()).getTop_250_Rank()));
                                    }
                                    else {
                                        chngeantiaz[j]=null;
                                        changerank[j] = null;
                                    }
                                }
                                moviecustomlist_change moviecustomlist_change = new moviecustomlist_change(SeachObj,changerank,chngeantiaz);
                                AlphaInAnimationAdapter alphaInAnimationAdapter=new AlphaInAnimationAdapter(moviecustomlist_change);
                                Movielist.setAdapter(alphaInAnimationAdapter);
                                listener8 = new RecyclerTouchListener(getApplicationContext(), Movielist, new ClickListner() {
                                    @Override
                                    public void onClick(View view, int position) {
                                        Intent gotopage = new Intent(movie_list.this, fullmovieinfo.class);
                                        gotopage.putExtra("MOVIE",SeachObj.get(position));
                                        gotopage.putExtra("PLOT",MovieData.get(SeachObj.get(position).getTitle()));
                                        startActivity(gotopage);
                                    }

                                    @Override
                                    public void onLongClick(View view, int position) {

                                    }
                                });
                                Movielist.addOnItemTouchListener(listener8);
                            }
                        }
                    }
                    return true;
                }

        });

        return true;
    }

    private boolean checkconectivity() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo[] = connectivityManager.getAllNetworkInfo();
        boolean check = false;
        for (NetworkInfo aNetinfo : netinfo) {
            if (aNetinfo.getState() == NetworkInfo.State.CONNECTED) {
                check = true;
                break;
            }
        }
        return check;
    }
    private class SearchViewExpandListener implements MenuItemCompat.OnActionExpandListener {

        private Context context;

        SearchViewExpandListener(Context c) {
            context = c;
        }

        @Override
        public boolean onMenuItemActionExpand(MenuItem item) {
            ((AppCompatActivity) context).getSupportActionBar().setDisplayShowHomeEnabled(true);
            return false;
        }

        @Override
        public boolean onMenuItemActionCollapse(MenuItem item) {
            ((AppCompatActivity) context).getSupportActionBar().setDisplayShowHomeEnabled(false);
            return false;
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void UpdateList() {

        int W_sd = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int R_sd = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (W_sd == PackageManager.PERMISSION_DENIED && R_sd == PackageManager.PERMISSION_DENIED) {
            AlertDialog.Builder a = new AlertDialog.Builder(this);
            a.setTitle("اطلاعات فیلم");
            a.setCancelable(false);
            a.setMessage("برای بروزرسانی اطلاعات،برنامه نیاز به دسترسی به خواندن حافظه و اینترنت دارد");
            a.setPositiveButton(" دسترسی دادن به برنامه", (dialog, which) ->
                    ActivityCompat.requestPermissions(movie_list.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.INTERNET}, 1));
            a.show();
        } else {
            if (!checkconectivity()) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getApplicationContext(), "لطفا برای بروزرسانی اطلاعات به اینترنت متصل شوید", Toast.LENGTH_LONG).show();
            }else if (checkconectivity()) {
                SharedPreferences.Editor shared = getSharedPreferences("movieinfosh", MODE_PRIVATE).edit();
                String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                SharedPreferences getstatus = getSharedPreferences("movieinfosh", MODE_PRIVATE);
                Calendar c = Calendar.getInstance();
                shared.putInt("DBupdate_version_Year", c.get(Calendar.YEAR));
                shared.putInt("DBupdate_version_Day", c.get(Calendar.DAY_OF_YEAR));
                shared.putString("DBupdate_version", currentDateTimeString);
                shared.putInt("DBnew_Version", getstatus.getInt("DBnew_Version", 0) + 1);
                shared.apply();
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                buldernoti = new Notification.Builder(getApplicationContext());
                Intent intent = new Intent();
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, 0);
                buldernoti.setAutoCancel(false);
                buldernoti.setOngoing(false);
                buldernoti.setSmallIcon(R.mipmap.ic_launcher);
                buldernoti.setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(),
                        R.mipmap.ic_launcher));
                buldernoti.setContentTitle("اطلاعات فیلم");
                buldernoti.setContentText("در حال دریافت اطلاعات متنی ...");
                buldernoti.setContentIntent(pendingIntent);
                buldernoti.build();
                Notification notification = buldernoti.getNotification();
                assert notificationManager != null;
                notificationManager.notify("Movie app", 0, notification);
                new PutDataOnDB().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
                //new Checkname_other_site().execute("http://m.imdb.com/chart/top");
            }
        }
    }
    Runnable ErrorRunable= () -> {
        if(!checkconectivity())
        Toast.makeText(movie_list.this,"دستگاه به اینترنت دسترسی ندارد",Toast.LENGTH_LONG).show();
        else
        Toast.makeText(movie_list.this,"خطایی در بروزرسانی رخ داد",Toast.LENGTH_LONG).show();
    };
    @SuppressLint("StaticFieldLeak")
    private  class PutDataOnDB extends AsyncTask<Void,Void,Void>
    {

        List<Movie>TempMovies=new ArrayList<>();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... voids) {

            MovieListOld.clear();
            for (int i = 0; i <MovieList.size() ; i++) {
                MovieListOld.put(MovieList.get(i).getTitle().trim(),MovieList.get(i));
            }


            Map<String,String>FANAMEs=new HashMap<>();
            Map<String,String>IsTranslated=new HashMap<>();

            for (int i = 0; i <MovieList.size() ; i++) {
                FANAMEs.put(MovieList.get(i).getTitle().trim(), MovieList.get(i).getTitleFa());
                IsTranslated.put(MovieList.get(i).getTitle().trim(), Boolean.toString(MovieList.get(i).isTranslated()));
            }


            //Log.e("FANAMEs",FANAMEs.size()+"");




            int rank = 0;
            String NameSave = "";
            try {
                final Document documentName;
                documentName = Jsoup.connect("https://m.imdb.com/chart/top").get();

                //MovieList.clear();

                for (Element rowName : documentName.select("div.col-xs-12.col-md-6")) {
                    String defultName = rowName.select("h4").text();
                    String[] spilited = defultName.split(" ");
                    StringBuilder FinalName = new StringBuilder();
                    if (spilited.length > 1) {
                        for (int i = 1; i < spilited.length - 1; i++) {
                            FinalName.append(spilited[i]);
                            FinalName.append(" ");
                        }
                        String name = FinalName.toString().trim();
                        Movie movie=new Movie();
                        movie.setTitle(name);
                        TempMovies.add(movie);
                    }
                }
             rank = 0;
            int DIRINDEX = 0;
            boolean isFirst=true;  // first is empty
            final String IMDBKEY = "http://www.imdb.com";
            final Document document;
            // Downloader(webSiteURL);

                document = Jsoup.connect("http://www.imdb.com/chart/top").get();
                for (Element row : document.select("table.chart.full-width tr")) {
                    Elements a = row.select(".titleColumn a");
                    Elements Poster = row.select(".posterColumn");
                    final String poster = Poster.select("img").attr("src").replaceAll("\\._V1_(.*?)\\.jpg", "._V1_UY1024_CR1,50,1024.jpg");
                    final String MovieLink = a.attr("href");
                    final String DIRNAME_Casts = a.attr("title");
                    final String rating = row.select(".imdbRating").text();
                    final String yeartemp = row.select(".titleColumn span").text().trim();
                    final String year = yeartemp.replaceAll("[^\\.0123456789]", "");
                    final String Rating = row.select(".imdbRating strong").attr("title");
                    String[] DIR_CASTS = DIRNAME_Casts.split(",");
                    final String DIRNAME = DIR_CASTS[DIRINDEX].trim().replace("(dir.)", "");

                    StringBuilder Casts = new StringBuilder();
                    for (int i = 1; i < DIR_CASTS.length; i++) {
                        Casts.append(DIR_CASTS[i].trim());
                        Casts.append(",");
                    }
                    final String CASTS = Casts.toString();


                    Log.e("Movie_name", NameSave);
                    Log.e("Actors_Actress", CASTS);
                    Log.e("IMDB_RANK_simple", rating);
                    Log.e("IMDB_RANK_full", Rating);
                    Log.e("Movie_year", year);
                    Log.e("MovieDirector_name", DIRNAME);
                    Log.e("Movie_Rank", rank+1 + "");
                    Log.e("Movie_picture_link", poster);
                    Log.e("Movie_IMDB_link", IMDBKEY + MovieLink);
                    if(!isFirst) {
                        TempMovies.get(rank).setTop_250_Rank(rank + 1 + "");
                        TempMovies.get(rank).setIMDBlink(IMDBKEY + MovieLink);
                        TempMovies.get(rank).setPoster(poster);
                        TempMovies.get(rank).setImdbRating(rating);
                        TempMovies.get(rank).setIMDbRankFull(Rating);
                        //movie.getExtraInfo().setDirector(DIRNAME);
                        TempMovies.get(rank).setYear(year);
                        //movie.setSavedCountRanking(movie.countranking() + "");

                        rank++;

                    }
                    isFirst=false;
                }

                MovieList.clear();
                MovieList.addAll(TempMovies);
                TempMovies.clear();

                Log.e("getOMDBDatas", "called");
                for (int i = 0; i <MovieList.size() ; i++) {
                        MovieList.get(i).setTitleFa(FANAMEs.get(MovieList.get(i).getTitle()));
                        MovieList.get(i).setTranslated(Boolean.parseBoolean(IsTranslated.get(MovieList.get(i).getTitle())));
                       // Log.e("MovieListFa", MovieList.get(i).getTitleFa()+"") ;
                }

                getOMDBDatas();






            } catch (Exception e) {
                runOnUiThread(ErrorRunable);
                Log.e("PutDataOnDBException",e.toString());
            }

            return null;

        }
            @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //new prossesonFarsidata().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,"http://www.naghdefarsi.com/movies/top-movies.html");
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            builder = new NotificationCompat.Builder(getApplicationContext());
            Intent intent = new Intent();
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 1, intent, 0);
            builder.setAutoCancel(false);
            builder.setContentTitle("اطلاعات فیلم");
            builder.setSmallIcon(R.mipmap.ic_launcher);
            RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.noti);
            contentView.setImageViewBitmap(R.id.status_icon, BitmapFactory.decodeResource(getApplicationContext().getResources(),
                    R.mipmap.ic_launcher));
            contentView.setTextViewText(R.id.status_text, currnetprogress + " از " + AsyncTaskcounter);
            contentView.setTextViewText(R.id.titlenoti, "اطلاعات فیلم");
            contentView.setTextViewText(R.id.textnoti, "در حال دریافت پوستر ها...");
            contentView.setProgressBar(R.id.status_progress, AsyncTaskcounter, currnetprogress, false);
                builder.setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(),
                        R.mipmap.ic_launcher));
                builder.setContentTitle("اطلاعات فیلم");
                builder.setContentText("اطلاعات دریافت شد");
                builder.setOngoing(false);
            builder.setContentIntent(pendingIntent);
            builder.build();
            Notification notification = builder.getNotification();
            assert notificationManager != null;
            notificationManager.notify("Movie app", 0, notification);
                LoadPic();
        }
    }

    private void DownalodFaAwards()
    {
        for (int i = 0; i <MovieList.size() ; i++) {
            //  Log.e("getFaAwards",(i+1)+"-"+MovieData.get(MovieList.get(i).getTitle().trim()).getAwards());
            String s=MovieData.get(MovieList.get(i).getTitle().trim()).getAwards();
            Pattern RTL_CHARACTERS =
                    Pattern.compile("[\u0600-\u06FF\u0750-\u077F\u0590-\u05FF\uFE70-\uFEFF]");
            Matcher matcher = RTL_CHARACTERS.matcher(s);
            if(!matcher.find()){
                Log.e("DownalodFaAwards",MovieList.get(i).getTitle().trim());
                Log.e("AWard",s);
                getMovieFarsiAwards(MovieData.get(MovieList.get(i).getTitle().trim()).getAwards(), MovieList.get(i).getTitle().trim());
            }
        }
    }
    private void DownloadFaStory()
    {
        for (int i = 0; i <MovieList.size() ; i++) {
         //   Log.e("getFaPlot",(i+1)+"-"+MovieData.get(MovieList.get(i).getTitle().trim()).getFaPlot());
            if(MovieData.get(MovieList.get(i).getTitle().trim()).getFaPlot()==null) {
                Log.e("DownloadFaStoryTitle",MovieList.get(i).getTitle().trim());
                GetFASTORYCOUNTER++;
                getplotTranslate(MovieList.get(i).getTitle().trim(), MovieData.get(MovieList.get(i).getTitle().trim()).getPlot());
            }
        }
    }
    private void DownloadFaTitle()
    {

        for (int i = 0; i <MovieList.size() ; i++) {
            //Log.e("FABAME",(i+1)+"-"+MovieList.get(i).getTitleFa());
            if(MovieList.get(i).getTitleFa()==null || MovieList.get(i).getTitleFa().equals("")) {
                Log.e("DownloadFaTitle",MovieList.get(i).getTitle().trim());
                MovieList.get(i).setTranslated(true);
                GetFATITLECOUNTER++;
                getMovieFarsiName(MovieList.get(i).getTitle().trim(),i);
            }
        }
    }

    private void getOMDBDatas()

    {
        StringRequest stringRequest;
        for (int i = 0; i < MovieList.size(); i++) {
            if((stringRequest=SendRequset(MovieList.get(i).getTitle(),MovieList.get(i).getYear()))!=null) {
                GetOMDBDATASCOUNTER++;
                requestQueue.add(stringRequest);
        }
        }
    }
    private void LoadPic()
    {
        List<Pair<String,String>>DownloadList=new ArrayList<>();
        for (int i = 0; i <MovieList.size() ; i++) {
            File file = new File(getApplicationContext().getFilesDir().getPath() + "/" + MovieList.get(i).getTitle().trim());
            if (!file.exists()) {
                ++AsyncTaskcounter;
                String url = MovieList.get(i).getPoster();
                final String name =  MovieList.get(i).getTitle().trim();
                Log.e("AsyncTaskcounter", "MovieName:" + name + "  PICLink:" + url);
                DownloadList.add(new Pair<>(name,url));
        }
        }
        if(DownloadList.size()==0)
            onItemsLoadComplete();
        else
        {
            Toast.makeText(getApplicationContext(), "در حال دریافت پوستر ها،لطفا صبر کنید...", Toast.LENGTH_SHORT).show();
            Movielist.getAdapter().notifyDataSetChanged();
            DownloadPic(DownloadList,0);
        }

        if (currnetprogress == AsyncTaskcounter) {
            onItemsLoadComplete();
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            Notification.Builder builder = new Notification.Builder(getApplicationContext());
            Intent intent = new Intent();
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, 0);
            builder.setAutoCancel(true);
            builder.setOngoing(false);
            builder.setSmallIcon(R.mipmap.ic_launcher);
            builder.setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(),
                    R.mipmap.ic_launcher));
            builder.setColor(getApplicationContext().getResources()
                    .getColor(R.color.backgroundunSwith));
            builder.setContentTitle("اطلاعات فیلم");
            builder.setContentText("تمام اطلاعات دریافت شد");
            builder.setContentIntent(pendingIntent);
            builder.build();
            Notification notification = builder.getNotification();
            assert notificationManager != null;
            notificationManager.notify("Movie app", 0, notification);
            if(DownloadList.size()!=0)
            Toast.makeText(getApplicationContext(), "پوستر ها دانلود شد", Toast.LENGTH_SHORT).show();
        }
    }
    private int findFirstChangePos(String name)
    {

        for (int i = 0; i <MovieList.size(); i++) {
            if(name.equalsIgnoreCase(MovieList.get(i).getTitle()))
                return i;
        }
        return -1;
    }
    private void DownloadPic(final List<Pair<String,String>> List, final int index)
    {

        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = 8;
        if(List.size()>index) {
            Glide
                    .with(getApplicationContext())
                    .load(List.get(index).second)
                    .asBitmap()
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            try {
                                Log.e("onResourceReady", "MovieName:" + List.get(index).first + "AsyncTaskcounter:" + AsyncTaskcounter + "   currnetprogress:" + currnetprogress);
                                FileOutputStream fos = new FileOutputStream(getApplicationContext().getFilesDir().getPath() + "/" + List.get(index).first.trim());
                                resource.compress(Bitmap.CompressFormat.JPEG, 60, fos);
                                fos.flush();
                                fos.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                int pos=-1;
                                if(isListVisible)
                                    if((pos=findFirstChangePos(List.get(index).first))!=-1)
                                        Movielist.getAdapter().notifyItemChanged(pos);

                                        Log.e("findFirstChangePos",""+pos);
                                currnetprogress++;
                                RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.noti);
                                contentView.setImageViewBitmap(R.id.status_icon, BitmapFactory.decodeResource(getApplicationContext().getResources(),
                                        R.mipmap.ic_launcher));
                                contentView.setTextViewText(R.id.status_text, currnetprogress + " از " + AsyncTaskcounter);
                                contentView.setTextViewText(R.id.titlenoti, "اطلاعات فیلم");
                                contentView.setTextViewText(R.id.textnoti, "در حال دریافت پوستر ها...");
                                contentView.setProgressBar(R.id.status_progress, AsyncTaskcounter, currnetprogress, false);
                                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                Intent intent = new Intent();
                                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 1, intent, 0);
                                builder.setAutoCancel(false);
                                builder.setOngoing(true);
                                builder.setContentIntent(pendingIntent);
                                builder.setContentTitle("اطلاعات فیلم");
                                builder.setSmallIcon(R.mipmap.ic_launcher);
                                //builder.setCustomBigContentView(contentView);
                                builder.setCustomContentView(contentView);
                                builder.build();
                                Notification notification1 = builder.getNotification();
                                assert notificationManager != null;
                                notificationManager.notify("Movie app", 0, notification1);

                                DownloadPic(List, index + 1);

                                if (AsyncTaskcounter == currnetprogress && !checkpic) {
                                    swipeRefreshLayout.setRefreshing(false);
                                    checkpic = true;
                                    //Toast.makeText(getApplicationContext(), counter1+" پوستر جدید دانلود شد ", Toast.LENGTH_SHORT).show();
                                    Notification.Builder builder = new Notification.Builder(getApplicationContext());
                                    builder.setAutoCancel(true);
                                    builder.setOngoing(false);
                                    builder.setSmallIcon(R.mipmap.ic_launcher);
                                    builder.setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(),
                                            R.mipmap.ic_launcher));
                                    builder.setContentTitle("اطلاعات فیلم");
                                    builder.setContentText("تمام اطلاعات دریافت شد");
                                    builder.setColor(getApplicationContext().getResources()
                                            .getColor(R.color.backgroundunSwith));
                                    builder.setContentIntent(pendingIntent);
                                    builder.build();
                                    Notification notification = builder.getNotification();
                                    notificationManager.notify("Movie app", 0, notification);
                                    if(List.size()!=0)
                                    Toast.makeText(getApplicationContext(), "پوستر ها دانلود شد", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
        }
    }
   /* private boolean needToDownload(String MovieName)
    {
        for (int i = 0; i <MovieListUpdate.size() ; i++) {
            if(MovieListUpdate.get(i).getExtraInfo().getPlot()==null
                    ||MovieListUpdate.get(i).getExtraInfo().getGenre()==null
                    )
                return true;
        }
        return false;
    }
    */
    private StringRequest SendRequset(String MovieName,String MovieYear) {
        String ApiKey = "48c378c2";
        String OMDBURL = "http://www.omdbapi.com";
        String IdKey = "i";
        String TitleKey = "t";
        String YearKey = "y";
        String PlotFullKey = "&plot=full";
        if (!MovieData.containsKey(MovieName)) {
        String URL = OMDBURL + "/?apikey=" + ApiKey + "&" + TitleKey + "=" + MovieName.replaceAll(" ", "+") + "&" + YearKey + "=" + MovieYear;//+"&plot=full";
        Log.e("URL",URL);
            return new StringRequest(Request.Method.GET, URL, response -> {
                ShortPlot shortPlot = new Gson().fromJson(response, ShortPlot.class);
                if (shortPlot != null && !shortPlot.getResponse().equalsIgnoreCase("False")) {

                    Log.e("MovieOMBDName",shortPlot.getTitle());
                    Log.e("GetOMDBDATASCOUNTER",GetOMDBDATASCOUNTER+"");

                    GetOMDBDATASCOUNTER--;
                    MovieData.put(MovieName,shortPlot);
                   if(GetOMDBDATASCOUNTER==0)
                   {
                       Movielist.getAdapter().notifyDataSetChanged();
                       DownloadFaStory();
                   }
                    /*for (int i = 0; i <MovieList.size(); i++) {
                        if(MovieList.get(i).getTitle().equalsIgnoreCase(MovieName))
                            if((MovieList.get(i).getTitleFa()==null)) {
                                getMovieFarsiName(MovieName,i);
                                break;
                            }
                    }
                    */


                }
            }, error -> Log.e("responseError", error.toString()));
       }
         return null;
    }


    private void getplotTranslate(String MovieName,String plot)
    {
        String URL="https://api.beyond-dev.ir/translate?from=en&to=fa&text="+plot.replaceAll(" ","+")+"&simple=normal";
        Log.e("URLMovieFarsiStory",URL);
        StringRequest stringRequest=new StringRequest(Request.Method.GET,URL,response ->
        {
            if(!response.equals(""))
            {
                try {
                    GetFASTORYCOUNTER--;
                    Log.e("GetFASTORYCOUNTER",GetFASTORYCOUNTER+"");
                    if(GetFASTORYCOUNTER==0) {
                        DownloadFaTitle();
                        Movielist.getAdapter().notifyDataSetChanged();
                    }
                    MovieData.get(MovieName).setFaPlot(response);
                    MovieData.get(MovieName).setTranslatedPlot(true);
                }
                catch (Exception e)
                {
                    GetFASTORYCOUNTER--;
                    Log.e("plotTranslateException",e.toString());
                }
            }
        },error -> Log.e("TranslateError",error.toString()));
        requestQueue.add(stringRequest);
    }
    private void getMovieFarsiName(String EnName,int index)
    {
        String URL="https://api.beyond-dev.ir/translate?from=en&to=fa&text="+EnName.replaceAll(" ","+")+"&simple=normal";
       Log.e("URLMovieFarsiName",URL);
        StringRequest stringRequest=new StringRequest(Request.Method.GET,URL,response ->
        {
            Log.e("getFarsiNameResponse",response);
            if(!response.equals(""))
            {
                try {

                    MovieList.get(index).setTitleFa(response);
                    Log.e("GetFATITLECOUNTER", GetFATITLECOUNTER + "");
                    GetFATITLECOUNTER--;
                    if (GetFATITLECOUNTER == 0) {
                        DownalodFaAwards();
                        Movielist.getAdapter().notifyDataSetChanged();
                    }

                }
                catch (Exception e)
                {
                    GetFATITLECOUNTER--;
                    Log.e("plotTranslateException",e.toString());
                }
               // Movielist.getAdapter().notifyDataSetChanged();

                Log.e("TrasletedPlot",response);

            }
        },error -> Log.e("TranslateError",error.toString()));

        requestQueue.add(stringRequest);
    }
    private void getMovieFarsiAwards(String Awards,String MovieName)
    {
        String URL="https://api.beyond-dev.ir/translate?from=en&to=fa&text="+Awards.replaceAll("&"," and ").replaceAll(" ","+")+"&simple=normal";
        Log.e("URLMovieFarsiAwards",URL);
        StringRequest stringRequest=new StringRequest(Request.Method.GET,URL,response ->
        {
            Log.e("getFarsiAwardsResponse",response);
            if(!response.equals(""))
            {
                try {
                    MovieData.get(MovieName).setAwards(response);
                    MovieData.get(MovieName).setTranslatedAwards(true);
                    Movielist.getAdapter().notifyDataSetChanged();
                }
                catch (Exception e)
                {
                    Log.e("plotTranslateException",e.toString());
                }
                // Movielist.getAdapter().notifyDataSetChanged();

                Log.e("TrasletedPlot",response);

            }
        },error -> Log.e("TranslateError",error.toString()));

        requestQueue.add(stringRequest);
    }


}

