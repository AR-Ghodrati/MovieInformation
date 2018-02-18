package com.ar.movieinformation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Calendar;
import java.util.Locale;
import java.util.Vector;

public class movie_list extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
{
    int mood=0;
    SearchView searchView;
    RecyclerView Movielist;
    int Check=0;
    String[] MOVIEEN = new String[250];
    String[]years=new String[250];
    String[] MOVIEFA = new String[250];
    String [] ENpic=new String[250];
    String[]countrank=new String[250];
    boolean[]checkdoublicate=new boolean[250];
    boolean dirshow=false;
    boolean firstdirshow=false;
    RecyclerTouchListener listener1;
    RecyclerTouchListener listener2;
    RecyclerTouchListener listener3;
    RecyclerTouchListener listener4;
    RecyclerTouchListener listener5;
    RecyclerTouchListener listener6;
    RecyclerTouchListener listener7;
    RecyclerTouchListener listener8;

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
    BroadcastReceiver Finish=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final SharedPreferences getdata=getSharedPreferences("movieinfosh",MODE_PRIVATE) ;
        super.onCreate(savedInstanceState);

        /*
        if(getdata.getString("apptheme_type","AppTheme").equalsIgnoreCase("قهوه ای"))
            setTheme(R.style.AppTheme);
        else if(getdata.getString("apptheme_type","AppTheme").equalsIgnoreCase("مشکی"))
            setTheme(R.style.AppTheme1);
            */
        setContentView(R.layout.activity_movie_list);
        Movielist = (RecyclerView) findViewById(R.id.Movielist);
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
        File DB = new File(getApplicationContext().getDatabasePath("Movie_db").getPath());
        if (DB.exists())
            ReadData();
        else {
            SharedPreferences.Editor editor=  getSharedPreferences("movieinfosh",MODE_PRIVATE).edit();
            editor.putBoolean("isExtracted",false);
            editor.apply();
            Toast.makeText(getApplicationContext(), "برنامه را دوباره بعد از توقف کامل اجرا کنید", Toast.LENGTH_SHORT).show();
        }
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
            builder.setNegativeButton("تمایلی ندارم", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.show();
            editor.putBoolean("IsFirst_AlertShow",true);
            editor.apply();
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction("FADownloded");
        registerReceiver(receiver, filter);
        IntentFilter finish = new IntentFilter();
        finish.addAction("finish_movielist");
        registerReceiver(Finish, finish);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        unregisterReceiver(Finish);
        super.onDestroy();
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
        Thread tread = new Thread(new Runnable() {
            @Override
            public void run() {
                Vector<String>yearvect=new Vector<>();
                database db = new database(getApplicationContext(), "Movie_db", null, 1);
                SQLiteDatabase temp = db.getReadableDatabase();
                int counter = 0;
                String Movie_Rank, Movie_farsiname , Movie_year, Movie_name, MovieDirector_name, Movie_IMDB_link, Movie_picture_link, IMDB_RANK_simple, Actors_Actress, IMDB_RANK_full;
                final Vector<Movie> movies = new Vector<>();
                Cursor cursor = temp.query("MOVIEDATA", new String[]{"Movie_Rank", "IMDB_RANK_simple", "Movie_year", "Movie_name", "Movie_farsi_name", "MovieDirector_name", "Actors_Actress", "IMDB_RANK_full", "Movie_IMDB_link", "Movie_picture_link"}, null, null, null, null, null);
                cursor.moveToFirst();
                cursor.moveToNext();
                while (!cursor.isAfterLast()) {
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
                    tempMovie.setMovie_farsi_name("شیر شاه");
                    else tempMovie.setMovie_farsi_name(Movie_farsiname);
                    tempMovie.setMovie_name(Movie_name);
                    tempMovie.setMovie_Rank(Movie_Rank);
                    tempMovie.setMovie_IMDB_link(Movie_IMDB_link);
                    tempMovie.setMovie_picture_link(Movie_picture_link);
                    tempMovie.setIMDB_RANK_simple(IMDB_RANK_simple);
                    tempMovie.setActors_Actress(Actors_Actress);
                    tempMovie.setIMDB_RANK_full(IMDB_RANK_full);
                    tempMovie.setMovieDirector_name(MovieDirector_name);
                    tempMovie.setMovie_year(Movie_year);
                    tempMovie.setMovie_rank("" + (counter + 1));
                    countrank[counter]=""+tempMovie.countranking();
                    movies.addElement(tempMovie);
                    counter++;
                    cursor.moveToNext();
                }
                if (Check == 0) {
                    for (int i = 0; i < movies.size(); i++) {
                        years[i]=movies.elementAt(i).getMovie_year();
                        ENpic[i]=movies.elementAt(i).getMovie_name();
                        MOVIEEN[i] = (i + 1) + " -" + movies.elementAt(i).getMovie_name();
                        if (movies.elementAt(i).getMovie_name_farsi()!=null) {
                            MOVIEFA[i] = "  " + movies.elementAt(i).getMovie_name_farsi();
                        } else
                            MOVIEFA[i] = "  ";
                    }
                } else if (Check == 1) {
                    for (int i = 0; i < 250; i++)
                        for (int j = i + 1; j < 250; j++)
                            if (movies.elementAt(j).getMovie_year().compareToIgnoreCase(movies.elementAt(i).getMovie_year()) > 0) {
                                Movie t = movies.elementAt(i);
                                movies.setElementAt(movies.elementAt(j), i);
                                movies.setElementAt(t, j);
                            }
                    for (int i = 0; i < movies.size(); i++) {
                        years[i]=movies.elementAt(i).getMovie_year();
                        if(!yearvect.contains(years[i])) {
                            checkdoublicate[i] = true;
                            yearvect.addElement(years[i]);
                        }
                        ENpic[i] = movies.elementAt(i).getMovie_name();
                        MOVIEEN[i] =" "+movies.elementAt(i).getMovie_name();
                        if (movies.elementAt(i).getMovie_name_farsi()!=null) {
                            MOVIEFA[i] = "  " + movies.elementAt(i).getMovie_name_farsi();
                        } else
                            MOVIEFA[i] = "  ";

                    }
                    mood=1;
                }
                else if (Check == 2) {
                    for (int i = 0; i < 250; i++)
                        for (int j = i + 1; j < 250; j++)
                            if (movies.elementAt(j).getMovieDirector_name().compareToIgnoreCase(movies.elementAt(i).getMovieDirector_name()) < 0) {
                                Movie t = movies.elementAt(i);
                                movies.setElementAt(movies.elementAt(j), i);
                                movies.setElementAt(t, j);
                            }
                    for (int i = 0; i < movies.size(); i++) {
                        years[i]=movies.elementAt(i).getMovie_year();
                        ENpic[i]=movies.elementAt(i).getMovie_name();
                        MOVIEEN[i] =" "+movies.elementAt(i).getMovie_name();
                        if (movies.elementAt(i).getMovie_name_farsi()!=null) {
                            MOVIEFA[i] = "  " + movies.elementAt(i).getMovie_name_farsi();
                        } else
                            MOVIEFA[i] = "  ";
                    }
                } else if (Check == 3) {
                    for (int i = 0; i < 250; i++)
                        for (int j = i + 1; j < 250; j++)
                            if (movies.elementAt(j).countranking() > movies.elementAt(i).countranking()) {
                                Movie t = movies.elementAt(i);
                                movies.setElementAt(movies.elementAt(j), i);
                                movies.setElementAt(t, j);
                            }
                    for (int i = 0; i < movies.size(); i++) {
                        years[i]=movies.elementAt(i).getMovie_year();
                        ENpic[i]=movies.elementAt(i).getMovie_name();
                        MOVIEEN[i] = " " + movies.elementAt(i).getMovie_name();
                        if (movies.elementAt(i).getMovie_name_farsi()!=null) {
                            MOVIEFA[i] = "  " + movies.elementAt(i).getMovie_name_farsi();
                        } else
                            MOVIEFA[i] = "  ";
                    }
                } else if (Check == 4) {
                    for (int i = 0; i < 250; i++)
                        for (int j = i + 1; j < 250; j++)
                            if (movies.elementAt(j).getMovie_name().compareToIgnoreCase(movies.elementAt(i).getMovie_name()) < 0) {
                                Movie t = movies.elementAt(i);
                                movies.setElementAt(movies.elementAt(j), i);
                                movies.setElementAt(t, j);
                            }
                    for (int i = 0; i < movies.size(); i++) {
                        years[i]=movies.elementAt(i).getMovie_year();
                        ENpic[i]=movies.elementAt(i).getMovie_name();
                        MOVIEEN[i] = " "+ movies.elementAt(i).getMovie_name();
                        if (movies.elementAt(i).getMovie_name_farsi()!=null) {
                            MOVIEFA[i] = "  " + movies.elementAt(i).getMovie_name_farsi();
                        } else
                            MOVIEFA[i] = "  ";
                    }
                }
                temp.close();
                db.close();
                custom_reycler_view movielist = new custom_reycler_view( MOVIEEN, MOVIEFA,ENpic,years,checkdoublicate,mood);
                movielist.setcount(MOVIEEN.length);
               // Toast.makeText(getApplicationContext(),""+movielist.getItemCount(),Toast.LENGTH_SHORT).show();
                Movielist.setAdapter(movielist);
               // Movielist.removeOnItemTouchListener();
                listener1=new RecyclerTouchListener(getApplicationContext(), Movielist, new ClickListner() {
                    @Override
                    public void onClick(View view, int position) {
                        Intent gotopage = new Intent(movie_list.this, fullmovieinfo.class);
                        gotopage.putExtra("id", movies.elementAt(position).getMovie_rank());
                        gotopage.putExtra("name", "");
                        gotopage.putExtra("Movie_name_farsi", movies.elementAt(position).getMovie_name_farsi());
                        gotopage.putExtra("Movie_name", movies.elementAt(position).getMovie_name());
                        gotopage.putExtra("Movie_Rank", movies.elementAt(position).getMovie_Rank());
                        gotopage.putExtra("Movie_year", movies.elementAt(position).getMovie_year());
                        gotopage.putExtra("MovieDirector_name", movies.elementAt(position).getMovieDirector_name());
                        gotopage.putExtra("Movie_IMDB_link", movies.elementAt(position).getMovie_IMDB_link());
                        gotopage.putExtra("Movie_picture_link", movies.elementAt(position).getMovie_picture_link());
                        gotopage.putExtra("IMDB_RANK_simple", movies.elementAt(position).getIMDB_RANK_simple());
                        gotopage.putExtra("Actors_Actress", movies.elementAt(position).getActors_Actress());
                        gotopage.putExtra("IMDB_RANK_full", movies.elementAt(position).getIMDB_RANK_full());
                        gotopage.putExtra("count", "" + movies.elementAt(position).countranking());
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
            }
        });
        tread.run();
    }

    private void changelist()
    {
        final SharedPreferences.Editor shared=getSharedPreferences("movieinfosh",MODE_PRIVATE).edit();
        final SharedPreferences getstatus=getSharedPreferences("movieinfosh",MODE_PRIVATE) ;
        final String[] MOVIEEName = new String[250];
        final String[]changerank=new String[250];
        final String[] MOVIEFAname = new String[250];
        final String[]chngeantiaz=new String[250];
        final String[]amtiazshow=new String[250];
        final String[]rankshow=new String[250];
        final String[] Movie_years = new String[250];
        final String[] MovieDirector_names = new String[250];
        final String[] Movie_IMDB_links = new String[250];
        final String[] Actors_Actresss= new String[250];
        final String[] IMDB_RANK_full = new String[250];
        final int[] counterchange = {0};
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                if(!getstatus.getBoolean("Changelist_alert",false)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(movie_list.this);
                    builder.setCancelable(true);
                    String tempfa="( باتوجه به زمان آخرین بروز رسانی بهتر است لیست را هفته ای یک بار بروز کنید)";
                    builder.setMessage(" شما میتوانید جدیدترین تغییرات را با بروز رسانی لیست در این بخش مشاهده کنید"+"\n"+tempfa);
                    builder.setTitle("لیست تغییرات");
                    builder.setPositiveButton("فهمیدم", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            shared.putBoolean("Changelist_alert", true);
                            shared.apply();
                        }
                    });
                    builder.show();
                }
                database db = new database(getApplicationContext(), "Movie_db", null, 1);
                SQLiteDatabase temp = db.getReadableDatabase();
                String Movie_Rank,  IMDB_RANK_simple,enname;
                Cursor cursormain = temp.query("MOVIEDATA", new String[]{"Movie_Rank", "IMDB_RANK_simple", "Movie_year", "Movie_name", "Movie_farsi_name", "MovieDirector_name", "Actors_Actress", "IMDB_RANK_full", "Movie_IMDB_link", "Movie_picture_link"}, null, null, null, null, null);
                Cursor cursor = temp.query("MOVIEDATAOLD", new String[]{"Movie_Rank", "IMDB_RANK_simple", "IMDB_RANK_full","Movie_name"}, null, null, null, null, null);
                cursor.moveToFirst();
                cursormain.moveToFirst();
                while (cursormain.moveToNext()) {
                    enname=cursormain.getString(cursormain.getColumnIndex("Movie_name"));
                    MOVIEEName[counterchange[0]]=enname;
                    Movie_Rank = cursormain.getString(cursormain.getColumnIndex("Movie_Rank"));
                    rankshow[counterchange[0]]=Movie_Rank;
                    IMDB_RANK_simple = cursormain.getString(cursormain.getColumnIndex("IMDB_RANK_simple"));
                    amtiazshow[counterchange[0]]=IMDB_RANK_simple;
                    MovieDirector_names[counterchange[0]]=cursormain.getString(cursormain.getColumnIndex("MovieDirector_name"));
                    Movie_years[counterchange[0]]=cursormain.getString(cursormain.getColumnIndex("Movie_year"));
                    Movie_IMDB_links[counterchange[0]]=cursormain.getString(cursormain.getColumnIndex("Movie_IMDB_link"));
                    Actors_Actresss[counterchange[0]]=cursormain.getString(cursormain.getColumnIndex("Actors_Actress"));
                    IMDB_RANK_full[counterchange[0]]=cursormain.getString(cursormain.getColumnIndex("IMDB_RANK_full"));
                    if(MOVIEEName[counterchange[0]].equals("The Lion King"))
                        MOVIEFAname[counterchange[0]]="شیر شاه";
                    else MOVIEFAname[counterchange[0]]=cursormain.getString(cursormain.getColumnIndex("Movie_farsi_name"));
                    cursor.moveToFirst();
                    while (!cursor.isAfterLast())
                    {
                        if(cursor.getString(cursor.getColumnIndex("Movie_name")).equalsIgnoreCase(enname))
                        {
                            changerank[counterchange[0]]=""+(Double.parseDouble(cursor.getString(cursor.getColumnIndex("Movie_Rank")))-Double.parseDouble(Movie_Rank));
                            chngeantiaz[counterchange[0]]=""+((Double.parseDouble(IMDB_RANK_simple))-(Double.parseDouble(cursor.getString(cursor.getColumnIndex("IMDB_RANK_simple")))));
                            break;
                        }
                        cursor.moveToNext();
                    }
                    counterchange[0]++;
                }
                moviecustomlist_change moviecustomlist_change=new moviecustomlist_change(MOVIEEName,MOVIEFAname,MOVIEEName,rankshow,amtiazshow,changerank,chngeantiaz);
                moviecustomlist_change.setcount(MOVIEEName.length);
                Movielist.setAdapter(moviecustomlist_change);
                listener2=new RecyclerTouchListener(getApplicationContext(), Movielist, new ClickListner() {
                    @Override
                    public void onClick(View view, int position) {
                        Intent gotopage = new Intent(movie_list.this, fullmovieinfo.class);
                        gotopage.putExtra("id", rankshow[position]);
                        gotopage.putExtra("name", "");
                        gotopage.putExtra("Movie_name_farsi", MOVIEFAname[position]);
                        gotopage.putExtra("Movie_name", MOVIEEName[position]);
                        gotopage.putExtra("Movie_Rank", rankshow[position]);
                        gotopage.putExtra("Movie_year", Movie_years[position]);
                        gotopage.putExtra("MovieDirector_name", MovieDirector_names[position]);
                        gotopage.putExtra("Movie_IMDB_link", Movie_IMDB_links[position]);
                        //gotopage.putExtra("Movie_picture_link", Movie_picture_links[position]);
                        gotopage.putExtra("IMDB_RANK_simple", amtiazshow[position]);
                        gotopage.putExtra("Actors_Actress", Actors_Actresss[position]);
                        gotopage.putExtra("IMDB_RANK_full", IMDB_RANK_full[position]);
                        gotopage.putExtra("count", countrank[position]);
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
            }
        });
        thread.run();

    }
    private void Dirshow()
    {
        database db = new database(getApplicationContext(), "Movie_db", null, 1);
        SQLiteDatabase temp = db.getReadableDatabase();
        Cursor cursormain = temp.query("MOVIEDATA", new String[]{"Movie_Rank", "IMDB_RANK_simple", "Movie_year", "Movie_name", "Movie_farsi_name", "MovieDirector_name", "Actors_Actress", "IMDB_RANK_full", "Movie_IMDB_link", "Movie_picture_link"}, null, null, null, null, null);
        final Vector<String>dirnames=new Vector<>();
        while (cursormain.moveToNext()) {
            String dirname=cursormain.getString(cursormain.getColumnIndex("MovieDirector_name"));
            if (!dirnames.contains(dirname))
                dirnames.addElement(dirname);
        }
        final moviecustomlist_directors moviecustomlist_directors=new moviecustomlist_directors(dirnames);
        Movielist.setAdapter(moviecustomlist_directors);
        listener3=new RecyclerTouchListener(getApplicationContext(), Movielist, new ClickListner() {
            @Override
            public void onClick(View view, int position) {
                dirshow=true;
                setTitle("آثار "+dirnames.elementAt(position));
                database db = new database(getApplicationContext(), "Movie_db", null, 1);
                SQLiteDatabase temp = db.getReadableDatabase();
                String Movie_Rank, Movie_farsiname , Movie_year, Movie_name, MovieDirector_name, Movie_IMDB_link, Movie_picture_link, IMDB_RANK_simple, Actors_Actress, IMDB_RANK_full;
                final Vector<Movie> moviesdir = new Vector<>();
                Cursor cursor = temp.query("MOVIEDATA", new String[]{"Movie_Rank", "IMDB_RANK_simple", "Movie_year", "Movie_name", "Movie_farsi_name", "MovieDirector_name", "Actors_Actress", "IMDB_RANK_full", "Movie_IMDB_link", "Movie_picture_link"}, null, null, null, null, null);
                cursor.moveToFirst();
                while (cursor.moveToNext()) {
                    MovieDirector_name = cursor.getString(cursor.getColumnIndex("MovieDirector_name"));
                    if (MovieDirector_name.equals(dirnames.elementAt(position))) {
                        Movie tempMovie = new Movie();
                        Movie_name = cursor.getString(cursor.getColumnIndex("Movie_name"));
                        Movie_Rank = cursor.getString(cursor.getColumnIndex("Movie_Rank"));
                        Movie_year = cursor.getString(cursor.getColumnIndex("Movie_year"));
                        Movie_IMDB_link = cursor.getString(cursor.getColumnIndex("Movie_IMDB_link"));
                        Movie_picture_link = cursor.getString(cursor.getColumnIndex("Movie_picture_link"));
                        IMDB_RANK_simple = cursor.getString(cursor.getColumnIndex("IMDB_RANK_simple"));
                        Actors_Actress = cursor.getString(cursor.getColumnIndex("Actors_Actress"));
                        IMDB_RANK_full = cursor.getString(cursor.getColumnIndex("IMDB_RANK_full"));
                        Movie_farsiname = cursor.getString(cursor.getColumnIndex("Movie_farsi_name"));
                        if (Movie_name.equals("The Lion King"))
                            tempMovie.setMovie_farsi_name("شیر شاه");
                        else tempMovie.setMovie_farsi_name(Movie_farsiname);
                        tempMovie.setMovie_name(Movie_name);
                        tempMovie.setMovie_Rank(Movie_Rank);
                        tempMovie.setMovie_IMDB_link(Movie_IMDB_link);
                        tempMovie.setMovie_picture_link(Movie_picture_link);
                        tempMovie.setIMDB_RANK_simple(IMDB_RANK_simple);
                        tempMovie.setActors_Actress(Actors_Actress);
                        tempMovie.setIMDB_RANK_full(IMDB_RANK_full);
                        tempMovie.setMovieDirector_name(MovieDirector_name);
                        tempMovie.setMovie_year(Movie_year);
                        tempMovie.setMovie_rank(Movie_Rank);
                        moviesdir.addElement(tempMovie);
                    }
                }
                String[] MOVIEEN = new String[moviesdir.size()];
                String[]years=new String[moviesdir.size()];
                String[] MOVIEFA = new String[moviesdir.size()];
                String [] ENpic=new String[moviesdir.size()];
                for (int i = 0; i < moviesdir.size(); i++) {
                    years[i]=moviesdir.elementAt(i).getMovie_year();
                    ENpic[i]=moviesdir.elementAt(i).getMovie_name();
                    MOVIEEN[i] = (i + 1) + " -" + moviesdir.elementAt(i).getMovie_name();
                    if (moviesdir.elementAt(i).getMovie_name_farsi()!=null) {
                        MOVIEFA[i] = "  " + moviesdir.elementAt(i).getMovie_name_farsi();
                    } else
                        MOVIEFA[i] = "  ";
                }
                temp.close();
                db.close();
                moviecustomlist movielist = new moviecustomlist(MOVIEEN, MOVIEFA,ENpic,years,checkdoublicate,0);
                movielist.setcount(moviesdir.size());
                Movielist.setAdapter(movielist);
                listener4=new RecyclerTouchListener(getApplicationContext(), Movielist, new ClickListner() {
                    @Override
                    public void onClick(View view, int position) {
                        Intent gotopage = new Intent(movie_list.this, fullmovieinfo.class);
                        gotopage.putExtra("id", moviesdir.elementAt(position).getMovie_rank());
                        gotopage.putExtra("name", "");
                        gotopage.putExtra("Movie_name_farsi", moviesdir.elementAt(position).getMovie_name_farsi());
                        gotopage.putExtra("Movie_name", moviesdir.elementAt(position).getMovie_name());
                        gotopage.putExtra("Movie_Rank", moviesdir.elementAt(position).getMovie_Rank());
                        gotopage.putExtra("Movie_year", moviesdir.elementAt(position).getMovie_year());
                        gotopage.putExtra("MovieDirector_name", moviesdir.elementAt(position).getMovieDirector_name());
                        gotopage.putExtra("Movie_IMDB_link", moviesdir.elementAt(position).getMovie_IMDB_link());
                        gotopage.putExtra("Movie_picture_link", moviesdir.elementAt(position).getMovie_picture_link());
                        gotopage.putExtra("IMDB_RANK_simple", moviesdir.elementAt(position).getIMDB_RANK_simple());
                        gotopage.putExtra("Actors_Actress", moviesdir.elementAt(position).getActors_Actress());
                        gotopage.putExtra("IMDB_RANK_full", moviesdir.elementAt(position).getIMDB_RANK_full());
                        gotopage.putExtra("count", "" + moviesdir.elementAt(position).countranking());
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
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
           ReadData();
        }
    };
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
            Check = 1;
            setTitle("لیست براساس سال ساخت");
            android.support.v7.widget.SearchView search=(android.support.v7.widget.SearchView)findViewById(R.id.serachact);
            search.setVisibility(View.VISIBLE);
            ReadData();
        }
        else if(id==R.id.SortRank) {
            Check = 0;
            mood=0;
            setTitle("لیست بر اساس امتیاز");
            android.support.v7.widget.SearchView search=(android.support.v7.widget.SearchView)findViewById(R.id.serachact);
            search.setVisibility(View.VISIBLE);
            ReadData();
        }
        else if(id==R.id.SortpeopleRanking) {
            Check = 3;
            mood=0;
            setTitle("لیست بر اساس تعداد امتیاز");
            android.support.v7.widget.SearchView search=(android.support.v7.widget.SearchView)findViewById(R.id.serachact);
            search.setVisibility(View.VISIBLE);
            ReadData();

        }
        else if(id==R.id.SortNames)
        {
            Check=4;
            mood=0;
            setTitle("لیست بر اساس نام");
            android.support.v7.widget.SearchView search=(android.support.v7.widget.SearchView)findViewById(R.id.serachact);
            search.setVisibility(View.VISIBLE);
            ReadData();

        }
        else if(id==R.id.aboutus)
        {
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
            @Override
            public boolean onQueryTextSubmit(String s) {
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
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
                final Vector<String>EN= new Vector<>();
                final Vector<String>FA= new Vector<>();
                Vector<String>yearvect=new Vector<>();
                String text;
                boolean check=false;
                text=newText.toLowerCase(Locale.getDefault());
                if (text.length() == 0) {
                    if(Check!=5)
                    ReadData();
                    else
                        changelist();

                } else {
                    for (String aMOVIEEN : MOVIEEN) {
                        if (aMOVIEEN.toLowerCase(Locale.getDefault()).contains(text)) {
                            {
                                EN.addElement(aMOVIEEN);
                                check = true;
                            }
                        }
                    }
                    for (String aMOVIEEN : MOVIEFA) {
                        if (aMOVIEEN.toLowerCase(Locale.getDefault()).contains(text)) {
                            FA.addElement(aMOVIEEN);
                        }
                    }
                    if (check) {
                        yearvect.removeAllElements();
                        database db = new database(getApplicationContext(), "Movie_db", null, 1);
                        SQLiteDatabase temp = db.getReadableDatabase();
                        Cursor cursormain = temp.query("MOVIEDATAOLD", new String[]{"Movie_Rank", "IMDB_RANK_simple", "IMDB_RANK_full","Movie_name"}, null, null, null, null, null);
                        Cursor cursor = temp.query("MOVIEDATA", new String[]{"Movie_Rank", "IMDB_RANK_simple", "Movie_year", "Movie_name", "Movie_farsi_name", "MovieDirector_name", "Actors_Actress", "IMDB_RANK_full", "Movie_IMDB_link", "Movie_picture_link"}, null, null, null, null, null);
                        cursor.moveToFirst();
                        String[] en = new String[EN.size()];
                        final String[] fa = new String[EN.size()];
                        final String[] Movie_Rank = new String[EN.size()];
                        final String[] Movie_year = new String[EN.size()];
                        final String[] MovieDirector_name = new String[EN.size()];
                        final String[] Movie_IMDB_link = new String[EN.size()];
                        final String[] Movie_picture_link = new String[EN.size()];
                        final String[] IMDB_RANK_simple = new String[EN.size()];
                        final String[] Actors_Actress = new String[EN.size()];
                        final String[] IMDB_RANK_full = new String[EN.size()];
                        final String[] movie_name = new String[EN.size()];
                        final String[]changeranken=new String[EN.size()];
                        final String[]chngeantiazen=new String[EN.size()];
                        final boolean[] checkyear=new boolean[EN.size()];
                        for (int i = 0; i < EN.size(); i++) {
                            en[i] = EN.elementAt(i);
                            String tempen = en[i].substring(en[i].indexOf(" -") + 2);
                            while (cursor.moveToNext()) {
                                if (cursor.getString(cursor.getColumnIndex("Movie_name")).equalsIgnoreCase(tempen)) {
                                    if(tempen.equals("The Lion King")) fa[i]="شیر شاه";
                                    else  fa[i] = cursor.getString(cursor.getColumnIndex("Movie_farsi_name"));
                                    movie_name[i] = cursor.getString(cursor.getColumnIndex("Movie_name"));
                                    Movie_Rank[i] = cursor.getString(cursor.getColumnIndex("Movie_Rank"));
                                    Movie_year[i] = cursor.getString(cursor.getColumnIndex("Movie_year"));
                                    IMDB_RANK_simple[i] = cursor.getString(cursor.getColumnIndex("IMDB_RANK_simple"));
                                    cursormain.moveToFirst();
                                    while (!cursormain.isAfterLast())
                                    {
                                        if(cursormain.getString(cursormain.getColumnIndex("Movie_name")).equalsIgnoreCase(movie_name[i]))
                                        {
                                            changeranken[i]=""+(Double.parseDouble(cursormain.getString(cursormain.getColumnIndex("Movie_Rank")))-Double.parseDouble( Movie_Rank[i]));
                                            chngeantiazen[i]=""+(Double.parseDouble(IMDB_RANK_simple[i])-Double.parseDouble(cursormain.getString(cursormain.getColumnIndex("IMDB_RANK_simple"))));
                                            break;
                                        }
                                        cursormain.moveToNext();
                                    }
                                    if(!yearvect.contains(Movie_year[i])) {
                                        checkyear[i] = true;
                                        yearvect.addElement(Movie_year[i]);
                                    }
                                    MovieDirector_name[i] = cursor.getString(cursor.getColumnIndex("MovieDirector_name"));
                                    Movie_IMDB_link[i] = cursor.getString(cursor.getColumnIndex("Movie_IMDB_link"));
                                    Movie_picture_link[i] = cursor.getString(cursor.getColumnIndex("Movie_picture_link"));
                                    Actors_Actress[i] = cursor.getString(cursor.getColumnIndex("Actors_Actress"));
                                    IMDB_RANK_full[i] = cursor.getString(cursor.getColumnIndex("IMDB_RANK_full"));
                                    break;
                                }
                            }
                            cursor.moveToFirst();
                        }
                        if(Check!=5) {
                            moviecustomlist movielist = new moviecustomlist(en, fa, movie_name, Movie_year, checkyear, mood);
                            movielist.setcount(EN.size());
                            Movielist.setAdapter(movielist);
                            listener5=new RecyclerTouchListener(getApplicationContext(), Movielist, new ClickListner() {
                                @Override
                                public void onClick(View view, int position) {
                                    Intent gotopage = new Intent(movie_list.this, fullmovieinfo.class);
                                    gotopage.putExtra("id", Movie_Rank[position]);
                                    gotopage.putExtra("name", "");
                                    gotopage.putExtra("Movie_name_farsi", fa[position]);
                                    gotopage.putExtra("Movie_name", movie_name[position]);
                                    gotopage.putExtra("Movie_Rank", Movie_Rank[position]);
                                    gotopage.putExtra("Movie_year", Movie_year[position]);
                                    gotopage.putExtra("MovieDirector_name", MovieDirector_name[position]);
                                    gotopage.putExtra("Movie_IMDB_link", Movie_IMDB_link[position]);
                                    gotopage.putExtra("Movie_picture_link", Movie_picture_link[position]);
                                    gotopage.putExtra("IMDB_RANK_simple", IMDB_RANK_simple[position]);
                                    gotopage.putExtra("Actors_Actress", Actors_Actress[position]);
                                    gotopage.putExtra("IMDB_RANK_full", IMDB_RANK_full[position]);
                                    gotopage.putExtra("count", countrank[position]);
                                    startActivity(gotopage);
                                }

                                @Override
                                public void onLongClick(View view, int position) {

                                }
                            });
                            Movielist.addOnItemTouchListener(listener5);
                        }
                        if(Check==5)
                        {
                            moviecustomlist_change moviecustomlist_change=new moviecustomlist_change(en,fa,movie_name,Movie_Rank,IMDB_RANK_simple,changeranken,chngeantiazen);
                            moviecustomlist_change.setcount(EN.size());
                            Movielist.setAdapter(moviecustomlist_change);
                            listener6=new RecyclerTouchListener(getApplicationContext(), Movielist, new ClickListner() {
                                @Override
                                public void onClick(View view, int position) {
                                    Intent gotopage = new Intent(movie_list.this, fullmovieinfo.class);
                                    gotopage.putExtra("id",Movie_Rank [position]);
                                    gotopage.putExtra("name", "");
                                    gotopage.putExtra("Movie_name_farsi", fa[position]);
                                    gotopage.putExtra("Movie_name", movie_name[position]);
                                    gotopage.putExtra("Movie_Rank", Movie_Rank[position]);
                                    gotopage.putExtra("Movie_year", Movie_year[position]);
                                    gotopage.putExtra("MovieDirector_name", MovieDirector_name[position]);
                                    gotopage.putExtra("Movie_IMDB_link", Movie_IMDB_link[position]);
                                    gotopage.putExtra("Movie_picture_link", Movie_picture_link[position]);
                                    gotopage.putExtra("IMDB_RANK_simple", IMDB_RANK_simple[position]);
                                    gotopage.putExtra("Actors_Actress", Actors_Actress[position]);
                                    gotopage.putExtra("IMDB_RANK_full", IMDB_RANK_full[position]);
                                    gotopage.putExtra("count", countrank[position]);
                                    startActivity(gotopage);
                                }

                                @Override
                                public void onLongClick(View view, int position) {

                                }
                            });
                            Movielist.addOnItemTouchListener(listener6);
                        }
                    }
                    else {
                            yearvect.removeAllElements();
                            database db = new database(getApplicationContext(), "Movie_db", null, 1);
                            SQLiteDatabase temp = db.getReadableDatabase();
                            Cursor cursormains = temp.query("MOVIEDATAOLD", new String[]{"Movie_Rank", "IMDB_RANK_simple", "IMDB_RANK_full","Movie_name"}, null, null, null, null, null);
                            Cursor cursor = temp.query("MOVIEDATA", new String[]{"Movie_Rank", "IMDB_RANK_simple", "Movie_year", "Movie_name", "Movie_farsi_name", "MovieDirector_name", "Actors_Actress", "IMDB_RANK_full", "Movie_IMDB_link", "Movie_picture_link"}, null, null, null, null, null);
                            cursor.moveToFirst();
                            cursormains.moveToFirst();
                            String[] en = new String[FA.size()];
                            final String[] fa = new String[FA.size()];
                            final String[] Movie_Rank = new String[FA.size()];
                            final String[] Movie_year = new String[FA.size()];
                            final String[] MovieDirector_name = new String[FA.size()];
                            final String[] Movie_IMDB_link = new String[FA.size()];
                            final String[] Movie_picture_link = new String[FA.size()];
                            final String[] IMDB_RANK_simple = new String[FA.size()];
                            final String[] Actors_Actress = new String[FA.size()];
                            final String[] IMDB_RANK_full = new String[FA.size()];
                            final String[] movie_name = new String[FA.size()];
                            final boolean[] checkyear = new boolean[FA.size()];
                            String[]changerankens=new String[FA.size()];
                            String[]chngeantiazens=new String[FA.size()];
                            for (int i = 0; i < FA.size(); i++) {
                                fa[i] = FA.elementAt(i);
                                cursor.moveToFirst();
                                while (cursor.moveToNext()) {
                                    if (cursor.getString(cursor.getColumnIndex("Movie_farsi_name")) != null) {
                                        if (cursor.getString(cursor.getColumnIndex("Movie_farsi_name")).equalsIgnoreCase(FA.elementAt(i).substring(2))) {
                                            en[i] = cursor.getString(cursor.getColumnIndex("Movie_Rank")) + " -" + cursor.getString(cursor.getColumnIndex("Movie_name"));
                                            movie_name[i] = cursor.getString(cursor.getColumnIndex("Movie_name"));
                                            Movie_Rank[i] = cursor.getString(cursor.getColumnIndex("Movie_Rank"));
                                            IMDB_RANK_simple[i] = cursor.getString(cursor.getColumnIndex("IMDB_RANK_simple"));
                                            Movie_year[i] = cursor.getString(cursor.getColumnIndex("Movie_year"));
                                            if (!yearvect.contains(Movie_year[i])) {
                                                checkyear[i] = true;
                                                yearvect.addElement(Movie_year[i]);
                                            }
                                            cursormains.moveToFirst();
                                            while (!cursormains.isAfterLast())
                                            {
                                                if(cursormains.getString(cursormains.getColumnIndex("Movie_name")).equalsIgnoreCase(movie_name[i]))
                                                {
                                                    changerankens[i]=""+(Double.parseDouble(cursormains.getString(cursormains.getColumnIndex("Movie_Rank")))-Double.parseDouble( Movie_Rank[i]));
                                                    chngeantiazens[i]=""+(Double.parseDouble(IMDB_RANK_simple[i])-Double.parseDouble(cursormains.getString(cursormains.getColumnIndex("IMDB_RANK_simple"))));
                                                    break;
                                                }
                                                cursormains.moveToNext();
                                            }
                                            cursormains.moveToFirst();
                                            MovieDirector_name[i] = cursor.getString(cursor.getColumnIndex("MovieDirector_name"));
                                            Movie_IMDB_link[i] = cursor.getString(cursor.getColumnIndex("Movie_IMDB_link"));
                                            Movie_picture_link[i] = cursor.getString(cursor.getColumnIndex("Movie_picture_link"));
                                            Actors_Actress[i] = cursor.getString(cursor.getColumnIndex("Actors_Actress"));
                                            IMDB_RANK_full[i] = cursor.getString(cursor.getColumnIndex("IMDB_RANK_full"));
                                            break;
                                        }
                                    }
                                }
                                cursor.moveToFirst();
                            }
                        if (Check != 5) {
                            moviecustomlist movielist = new moviecustomlist( en, fa, movie_name, Movie_year, checkyear, mood);
                            movielist.setcount(FA.size());
                            Movielist.setAdapter(movielist);
                            listener7=new RecyclerTouchListener(getApplicationContext(), Movielist, new ClickListner() {
                                @Override
                                public void onClick(View view, int position) {
                                    Intent gotopage = new Intent(movie_list.this, fullmovieinfo.class);
                                    gotopage.putExtra("id", Movie_Rank[position]);
                                    gotopage.putExtra("name", "");
                                    gotopage.putExtra("Movie_name_farsi", fa[position]);
                                    gotopage.putExtra("Movie_name", movie_name[position]);
                                    gotopage.putExtra("Movie_Rank", Movie_Rank[position]);
                                    gotopage.putExtra("Movie_year", Movie_year[position]);
                                    gotopage.putExtra("MovieDirector_name", MovieDirector_name[position]);
                                    gotopage.putExtra("Movie_IMDB_link", Movie_IMDB_link[position]);
                                    gotopage.putExtra("Movie_picture_link", Movie_picture_link[position]);
                                    gotopage.putExtra("IMDB_RANK_simple", IMDB_RANK_simple[position]);
                                    gotopage.putExtra("Actors_Actress", Actors_Actress[position]);
                                    gotopage.putExtra("IMDB_RANK_full", IMDB_RANK_full[position]);
                                    gotopage.putExtra("count", countrank[position]);
                                    startActivity(gotopage);
                                }

                                @Override
                                public void onLongClick(View view, int position) {

                                }
                            });
                            Movielist.addOnItemTouchListener(listener7);
                        }
                        else
                        {
                            moviecustomlist_change moviecustomlist_change=new moviecustomlist_change(en,fa,movie_name,Movie_Rank,IMDB_RANK_simple,changerankens,chngeantiazens);
                            moviecustomlist_change.setcount(FA.size());
                            Movielist.setAdapter(moviecustomlist_change);
                            listener8=new RecyclerTouchListener(getApplicationContext(), Movielist, new ClickListner() {
                                @Override
                                public void onClick(View view, int position) {
                                    Intent gotopage = new Intent(movie_list.this, fullmovieinfo.class);
                                    gotopage.putExtra("id",Movie_Rank [position]);
                                    gotopage.putExtra("name", "");
                                    gotopage.putExtra("Movie_name_farsi", fa[position]);
                                    gotopage.putExtra("Movie_name", movie_name[position]);
                                    gotopage.putExtra("Movie_Rank", Movie_Rank[position]);
                                    gotopage.putExtra("Movie_year", Movie_year[position]);
                                    gotopage.putExtra("MovieDirector_name", MovieDirector_name[position]);
                                    gotopage.putExtra("Movie_IMDB_link", Movie_IMDB_link[position]);
                                    gotopage.putExtra("Movie_picture_link", Movie_picture_link[position]);
                                    gotopage.putExtra("IMDB_RANK_simple", IMDB_RANK_simple[position]);
                                    gotopage.putExtra("Actors_Actress", Actors_Actress[position]);
                                    gotopage.putExtra("IMDB_RANK_full", IMDB_RANK_full[position]);
                                    gotopage.putExtra("count", countrank[position]);
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
                return false;
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

}

