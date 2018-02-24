package com.ar.movieinformation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DownloadData extends AppCompatActivity {
    CheckBox Downloadtext, DownloadPIC, DownloadFA;
    ProgressDialog progressDialog;
    NotificationCompat.Builder builder;
    Notification.Builder buldernoti;
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.downloaddata);
        ActivityCompat.requestPermissions(DownloadData.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.INTERNET},1);
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void DownloadDataBTN(final View view) {
        int W_sd = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                int R_sd = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
                if (W_sd == PackageManager.PERMISSION_DENIED && R_sd == PackageManager.PERMISSION_DENIED) {
                    AlertDialog.Builder a=new AlertDialog.Builder(this);
                    a.setTitle("اطلاعات فیلم");
                    a.setCancelable(false);
                    a.setMessage("برای بروزرسانی اطلاعات،برنامه نیاز به دسترسی به خواندن حافظه و اینترنت دارد");
                    a.setPositiveButton(" دسترسی دادن به برنامه", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(DownloadData.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.INTERNET},1);
                        }
                    });
                    a.show();
                }
                else {
                    Downloadtext = (CheckBox) findViewById(R.id.DownloadText);
                    DownloadPIC = (CheckBox) findViewById(R.id.DownloadPIC);
                    DownloadFA = (CheckBox) findViewById(R.id.DownloadTextFA);

                    if (!checkconectivity())
                        Toast.makeText(getApplicationContext(), "لطفا برای دریافت اطلاعات به اینترنت متصل شوید", Toast.LENGTH_LONG).show();
                    else if (checkconectivity()) {
                if (Downloadtext.isChecked()) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("دانلود اطلاعات");
                    if (Downloadtext.isChecked() && DownloadPIC.isChecked())
                        builder.setMessage("آیا برای دانلود اطلاعات متنی و عکس ها اطمینان دارید؟" + "\n" + "(لطفا در هنگام دانلود اینترنت را قطع نکنید)");
                    else if (Downloadtext.isChecked())
                        builder.setMessage("آیا برای دانلود اطلاعات متنی اطمینان دارید؟" + "\n" + "(لطفا در هنگام دانلود اینترنت را قطع نکنید)");
                    builder.setCancelable(true);
                    builder.setPositiveButton("بله", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent finishmovielist = new Intent();
                            finishmovielist.setAction("finish_movielist");
                            sendBroadcast(finishmovielist);

                           // new prossesondata().execute("http://www.imdb.com/chart/top");
                           SharedPreferences.Editor shared = getSharedPreferences("movieinfosh", MODE_PRIVATE).edit();
                            String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                            SharedPreferences getstatus = getSharedPreferences("movieinfosh", MODE_PRIVATE);
                            Calendar c = Calendar.getInstance();
                            shared.putInt("DBupdate_version_Year", c.get(Calendar.YEAR));
                            shared.putInt("DBupdate_version_Day", c.get(Calendar.DAY_OF_YEAR));
                            shared.putString("DBupdate_version", currentDateTimeString);
                            shared.putInt("DBnew_Version", getstatus.getInt("DBnew_Version", 0) + 1);
                            shared.apply();
                            progressDialog = new ProgressDialog(DownloadData.this);
                            progressDialog.setTitle("لطفا صبر کنید");
                            progressDialog.setMessage("در حال دریافت اطلاعات متنی...");
                            progressDialog.setCancelable(false);
                            progressDialog.setIndeterminate(true);
                            progressDialog.show();
                            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                            buldernoti = new Notification.Builder(getApplicationContext());
                            Intent intent = new Intent();
                            PendingIntent pendingIntent = PendingIntent.getActivity(DownloadData.this, 1, intent, 0);
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
                    });
                    builder.setNegativeButton("خیر", (dialog, which) -> Toast.makeText(getApplicationContext(), "برای نمایش لیست فیلم ها باید اطلاعات دریافت شود ", Toast.LENGTH_LONG).show());
                    builder.show();
                } else
                    Toast.makeText(getApplicationContext(), "دانلود اطلاعات متنی انگلیسی ضروری است", Toast.LENGTH_LONG).show();
            }
        }
    }

    private String farsi="";
    private class prossesonFarsidata extends AsyncTask<String, String, String> {
        String Movie_String_data_HTMLsave =null;

        @Override
        protected String doInBackground(String... url) {
            final String Top_movie_url = url[0];
            String datareader = "";
            int c = 0;
            try {
                URL movieurl = new URL(Top_movie_url);
                HttpURLConnection urlConnection = (HttpURLConnection) movieurl.openConnection();
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader inputMoviedata = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                /*while ((datareader = inputMoviedata.readLine()) != null) {
                    Movie_String_data_HTMLsave += datareader + "\n";
                    int y = (250 * prosses++) / 20996;
                    progressDialog.setProgress(y);

                }
                */
                Movie_String_data_HTMLsave=org.apache.commons.io.IOUtils.toString(inputMoviedata);
                inputMoviedata.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            //prossesonFarsi(Movie_String_data_HTMLsave);
            if(Movie_String_data_HTMLsave!=null)
            farsi=Movie_String_data_HTMLsave;
        }
    }

    boolean check1 = false;

    private class prossesonFarsiSite extends AsyncTask<String, String, String> {
        String Movie_String_data_HTMLsave = "";
        int counterSite = 0;
        @Override
        protected String doInBackground(String... url) {
            final String Top_movie_url = url[0];
            String datareader = "";
            int c = 0;
            try {
                counterSite++;
                URL movieurl = new URL(Top_movie_url);
                HttpURLConnection urlConnection = (HttpURLConnection) movieurl.openConnection();
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader inputMoviedata = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                while ((datareader = inputMoviedata.readLine()) != null) {
                    Movie_String_data_HTMLsave += datareader + "\n";
                }
                inputMoviedata.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            counterSite--;
            prossesonFarsiSite(Movie_String_data_HTMLsave);
            if (counterSite ==0 && !check1) {
                check1 = true;
                finish();
            }
        }
    }

    boolean checkpic = false;
    int AsyncTaskcounter = 0;
    int currnetprogress = 0;



     int prosses = 0;
    AlertDialog.Builder Downloadalert;
    @SuppressLint("StaticFieldLeak")
    private  class PutDataOnDB extends AsyncTask<Void,Void,Void>
    {



        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... voids) {
            ContentValues contentValues = new ContentValues();
            SQLiteDatabase DBTemp;
            database movieDB = new database(getApplicationContext(), "Movie_db", null, 1);
            DBTemp = movieDB.getReadableDatabase();
            database db = new database(getApplicationContext(), "Movie_db", null, 1);
            SQLiteDatabase temp = db.getReadableDatabase();
            ContentValues contentValues1 = new ContentValues();
            ContentValues contentValuesfarsi = new ContentValues();
            String Movie_Ranks, IMDB_RANK_simples, IMDB_RANK_fulls, enname;
            Cursor cursor = temp.query("MOVIEDATA", new String[]{"Movie_Rank", "IMDB_RANK_simple", "Movie_year", "Movie_name", "Movie_farsi_name", "MovieDirector_name", "Actors_Actress", "IMDB_RANK_full", "Movie_IMDB_link", "Movie_picture_link"}, null, null, null, null, null);
            DBTemp.execSQL("delete from " + "MOVIEDATAOLD");
            cursor.moveToFirst();
            while (cursor.moveToNext()) {
                Movie_Ranks = cursor.getString(cursor.getColumnIndex("Movie_Rank"));
                IMDB_RANK_simples = cursor.getString(cursor.getColumnIndex("IMDB_RANK_simple"));
                IMDB_RANK_fulls = cursor.getString(cursor.getColumnIndex("IMDB_RANK_full"));
                enname = cursor.getString(cursor.getColumnIndex("Movie_name"));
                contentValues1.put("IMDB_RANK_full", IMDB_RANK_fulls);
                contentValues1.put("IMDB_RANK_simple", IMDB_RANK_simples);
                contentValues1.put("Movie_Rank", Movie_Ranks);
                contentValues1.put("Movie_name", enname);
                contentValuesfarsi.put("Movie_Rank",Movie_Ranks);
                contentValuesfarsi.put("Movie_name",enname);
                contentValuesfarsi.put("Movie_farsi_name",cursor.getString(cursor.getColumnIndex("Movie_farsi_name")));
                temp.insert("MOVIEDATAOLD", null, contentValues1);
            }
            temp.close();
            DBTemp.execSQL("delete from " + "MOVIEDATA");
            DBTemp.close();

            String NameSave = "";
            int rank=-1;

            DBTemp = movieDB.getWritableDatabase();
            int DIRINDEX = 0;
            final String IMDBKEY="http://www.imdb.com";
            final Document document;
            // Downloader(webSiteURL);
            try {
                document = Jsoup.connect("http://www.imdb.com/chart/top").get();
                for (Element row : document.select("table.chart.full-width tr")) {
                    Elements a = row.select(".titleColumn a");
                    Elements Poster = row.select(".posterColumn");
                    final String poster = Poster.select("img").attr("src").replaceAll("\\._V1_(.*?)\\.jpg", "._V1_UY1024_CR1,50,1024.jpg");
                    final String MovieLink =a.attr("href");
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
                    contentValues.put("Movie_name", NameSave);
                    contentValues.put("Actors_Actress", CASTS);
                    contentValues.put("IMDB_RANK_full", Rating);
                    contentValues.put("IMDB_RANK_simple", rating);
                    contentValues.put("Movie_year", year);
                    contentValues.put("MovieDirector_name", DIRNAME);
                    contentValues.put("Movie_Rank",""+rank++);
                    contentValues.put("Movie_picture_link", poster);
                    contentValues.put("Movie_IMDB_link",IMDBKEY+ MovieLink);
                    DBTemp.insert("MOVIEDATA", null, contentValues);



                    Log.e("Movie_name",NameSave);
                    Log.e("Actors_Actress",CASTS);
                    Log.e("IMDB_RANK_simple",rating);
                    Log.e("IMDB_RANK_full",Rating);
                    Log.e("Movie_year",year);
                    Log.e("MovieDirector_name",DIRNAME);
                    Log.e("Movie_Rank",rank+"");
                    Log.e("Movie_picture_link",poster);
                    Log.e("Movie_IMDB_link",IMDBKEY+ MovieLink);



                }
                final Document documentName;
                rank=0;
                documentName = Jsoup.connect("https://m.imdb.com/chart/top").get();
                for (Element rowName : documentName.select("div.col-xs-12.col-md-6")) {
                    String defultName=rowName.select("h4").text();
                    String[]spilited=defultName.split(" ");
                    StringBuilder FinalName = new StringBuilder();
                    if(spilited.length>1) {
                        for (int i = 1; i < spilited.length - 1; i++) {
                            FinalName.append(spilited[i]);
                            FinalName.append(" ");
                        }
                        String name = FinalName.toString();
                        ContentValues contentValues2 = new ContentValues();
                        contentValues2.put("Movie_name", name);
                        DBTemp.update("MOVIEDATA", contentValues2, "Movie_Rank=" + (rank++), null);
                    }
                }

            } catch (IOException |ArrayIndexOutOfBoundsException |NullPointerException | StringIndexOutOfBoundsException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),"خطایی در بروزرسانی رخ داد",Toast.LENGTH_LONG).show();
                Log.e("PutDBExeption",e.toString());
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
            PendingIntent pendingIntent = PendingIntent.getActivity(DownloadData.this, 1, intent, 0);
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
            if (DownloadPIC.isChecked()) {
                builder.setOngoing(true);
                //   builder.setCustomBigContentView(contentView);
                builder.setCustomContentView(contentView);
            } else {
                builder.setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(),
                        R.mipmap.ic_launcher));
                builder.setContentTitle("اطلاعات فیلم");
                builder.setContentText("اطلاعات دریافت شد");
                builder.setOngoing(false);
            }
            builder.setContentIntent(pendingIntent);
            builder.build();
            Notification notification = builder.getNotification();
            assert notificationManager != null;
            notificationManager.notify("Movie app", 0, notification);
            Downloadalert.setMessage("اطلاعات متنی دانلود شد");
            Downloadalert.setPositiveButton("باشه", (dialog, which) -> {
                if (DownloadPIC.isChecked()) {
                    Toast.makeText(getApplicationContext(), "در حال دریافت پوستر ها،لطفا صبر کنید...", Toast.LENGTH_SHORT).show();
                }

                dialog.dismiss();
                Intent intent1 = new Intent(DownloadData.this, movie_list.class);
                startActivity(intent1);
                if (!DownloadPIC.isChecked() && !DownloadFA.isChecked()) {
                    finish();
                }
            });
            Downloadalert.setCancelable(false);
            Downloadalert.show();
            if (DownloadPIC.isChecked())
                LoadPic();
            //if(DownloadFA.isChecked())
        }
    }



    private static String findLink(String findLinkFarsiStory, String Maindata, int Startpos) {
        String data = "";
        int pos = Maindata.indexOf(findLinkFarsiStory, Startpos);
        String temp = Maindata.substring(pos + findLinkFarsiStory.length(), pos + findLinkFarsiStory.length() + 100);
        for (int i = 0; i < temp.length(); i++) {
            if (temp.charAt(i) != '"')
                data += temp.charAt(i);
            else
                break;
        }
        return "http://www.naghdefarsi.com" + data;
    }

    private static String findFarsiname(String findLinkFarsiStory, String Maindata, int Startpos) {
        String data = "";
        int pos = Maindata.indexOf(findLinkFarsiStory, Startpos);
        String temp = Maindata.substring(pos + findLinkFarsiStory.length(), pos + findLinkFarsiStory.length() + 400);
        int POS = temp.indexOf("(");
        for (int i = POS + 1; i < temp.length(); i++) {
            if (temp.charAt(i) != ')') {
                if (!((temp.charAt(i) >= 'a' && temp.charAt(i) <= 'z') || (temp.charAt(i) >= 'A' && temp.charAt(i) <= 'Z')))
                    data += temp.charAt(i);
            } else
                break;
        }
        return data;
    }

    @NonNull
    private static String findEnname(String findLinkFarsiStory, String Maindata, int Startpos) {
        String data = "";
        int pos = Maindata.indexOf(findLinkFarsiStory, Startpos);
        String temp = Maindata.substring(pos + findLinkFarsiStory.length(), pos + findLinkFarsiStory.length() + 400);
        int POS = temp.indexOf("title=\"");
        for (int i = POS + 19; i < temp.length(); i++) {
            if (temp.charAt(i) != '(') {
                if (!(temp.charAt(i) >= 'ا' && temp.charAt(i) <= 'ی'))
                    data += temp.charAt(i);
            } else
                break;
        }
        return data.substring(1);
    }

    @NonNull
    private static String findAward(String findLinkFarsiStory, String Maindata) {
        StringBuilder data = new StringBuilder();
        int Startpos = 0;
        if (Maindata.length() == 0)
            return "اطلاعات یافت نشد";
        int pos = Maindata.indexOf(findLinkFarsiStory);
        if (pos == -1) {
            data.append("فیلم جایزه خاصی نبرده است");
            return data.toString();
        } else {
            String temp = Maindata.substring(pos + findLinkFarsiStory.length(), pos + findLinkFarsiStory.length() + 2000);
            if (!temp.contains("خلاصه داستان")) {
                data.append("فیلم جایزه خاصی نبرده است");
                return data.toString();
            } else {
                String temp2 = temp.substring(0, temp.indexOf("خلاصه داستان"));
                boolean a = false;
                boolean b = false;
                for (int i = 0; i < temp2.length(); i++) {
                    if (data.indexOf("برنده اسکار") > 0 && !a) {
                        data.append(" : ");
                        a = true;
                    } else if (data.indexOf(" نامزد اسکار") > 0 && !b) {
                        data.append(" : ");
                        b = true;
                    } else if (temp2.charAt(i) >= 'آ' && temp2.charAt(i) <= 'ی' || temp2.charAt(i) == ' ' || temp.charAt(i) == '.' || temp.charAt(i) == '،')
                        data.append(temp2.charAt(i));
                }

                return data.toString();
            }
        }
    }

    @NonNull
    private static String findStory(String findLinkFarsiStory, String Maindata) {
        StringBuilder data = new StringBuilder();
        int Startpos = 0;
        int pos = Maindata.indexOf(findLinkFarsiStory);
        if (Maindata.length() == 0)
            return "اطلاعات یافت نشد";
        if (pos < 0) {
            pos = Maindata.indexOf("خلاصه داستان");
            String temp = Maindata.substring(pos + 15, pos + findLinkFarsiStory.length() + 3000);
            for (int i = 0; i < temp.indexOf("<a href"); i++) {
                if ((temp.charAt(i) >= 'آ' && temp.charAt(i) <= 'ی') || (temp.charAt(i) >= '۰' && temp.charAt(i) <= '۹') || (temp.charAt(i) >= '0' && temp.charAt(i) <= '9') || temp.charAt(i) == ' ' || temp.charAt(i) == ')' || temp.charAt(i) == '(' || temp.charAt(i) == '.' || temp.charAt(i) == '،')
                    data.append(temp.charAt(i));
            }
            return data.toString();
        } else {
            String temp = Maindata.substring(pos + findLinkFarsiStory.length(), pos + findLinkFarsiStory.length() + 3000);
            for (int i = 0; i < temp.indexOf("<a href"); i++) {
                if ((temp.charAt(i) >= 'آ' && temp.charAt(i) <= 'ی') || (temp.charAt(i) >= '۰' && temp.charAt(i) <= '۹') || (temp.charAt(i) >= '0' && temp.charAt(i) <= '9') || temp.charAt(i) == ' ' || temp.charAt(i) == ')' || temp.charAt(i) == '(' || temp.charAt(i) == '.' || temp.charAt(i) == '،')
                    data.append(temp.charAt(i));
            }
            return data.toString();
        }
    }

    private static String findTime(String findLinkFarsiStory, String Maindata) {
        StringBuilder data = new StringBuilder();
        int pos = Maindata.indexOf(findLinkFarsiStory);
        if (Maindata.length() == 0)
            return "اطلاعات یافت نشد";
        String temp = Maindata.substring(pos - 4, pos);
        for (int i = 0; i < temp.length(); i++) {
            if ((temp.charAt(i) >= '0' && temp.charAt(i) <= '9') || (temp.charAt(i) >= '۰' && temp.charAt(i) <= '۹'))
                data.append(temp.charAt(i));
        }
        if (data.length() == 0) {

            temp = Maindata.substring(pos - 150, pos - 100);
            for (int i = 0; i < temp.length(); i++) {
                if ((temp.charAt(i) >= '0' && temp.charAt(i) <= '9') || (temp.charAt(i) >= '۰' && temp.charAt(i) <= '۹'))
                    data.append(temp.charAt(i));
            }
            if (data.length() == 0) {
                temp = Maindata.substring(pos - 50, pos - 20);
                for (int i = 0; i < temp.length(); i++) {
                    if ((temp.charAt(i) >= '0' && temp.charAt(i) <= '9') || (temp.charAt(i) >= '۰' && temp.charAt(i) <= '۹'))
                        data.append(temp.charAt(i));
                }
                return data.toString();
            } else
                return data.substring(6);
        }
        return data.toString();
    }

    @NonNull
    private static String findStyle(String findLinkFarsiStory, String Maindata) {
        StringBuilder data = new StringBuilder();
        int Startpos = 0;
        int pos = Maindata.indexOf(findLinkFarsiStory);
        if (Maindata.length() == 0)
            return "اطلاعات یافت نشد";
        String temp = Maindata.substring(pos - 350, pos - 50);
        int POs1 = temp.indexOf("</span> <span style=\"font-family: tahoma,arial,helvetica,sans-serif; color: #333333;\">");
        if (POs1 > 0) {
            for (int i = POs1 + 20; i < temp.length(); i++) {
                if (temp.charAt(i) != '<') {
                    if ((temp.charAt(i) >= 'آ' && temp.charAt(i) <= 'ی' || temp.charAt(i) == '،' || temp.charAt(i) == ' '))
                        data.append(temp.charAt(i));
                    else if (temp.charAt(i) == '-')
                        data.append(" ");
                } else
                    break;
            }
        } else {
            POs1 = temp.indexOf("<span style=\"color: #000000;\">");
            if (POs1 > 0) {
                for (int i = POs1 + 30; i < temp.length(); i++) {
                    if (temp.charAt(i) != '<') {
                        if ((temp.charAt(i) >= 'آ' && temp.charAt(i) <= 'ی' || temp.charAt(i) == '،' || temp.charAt(i) == ' ' || temp.charAt(i) == '-'))
                            data.append(temp.charAt(i));
                    } else
                        break;
                }
            } else {
                POs1 = temp.indexOf("<span style=\"color: #333333;\">");
                if (POs1 > 0) {
                    for (int i = POs1 + 30; i < temp.length(); i++) {
                        if (temp.charAt(i) != '<') {
                            if ((temp.charAt(i) >= 'آ' && temp.charAt(i) <= 'ی' || temp.charAt(i) == '،' || temp.charAt(i) == ' '))
                                data.append(temp.charAt(i));
                        }
                    }
                }
            }
        }
        return data.toString();
    }

    private static String findPICQU(String findLinkFarsiStory, String Maindata) {
        StringBuilder data = new StringBuilder();
        if (Maindata.length() == 0)
            return "اطلاعات یافت نشد";
        int pos = Maindata.indexOf(findLinkFarsiStory);
        if (pos < 0) {
            pos = Maindata.indexOf("<p><img src=\"");
            if (pos > 0) {
                for (int i = pos + 13; i < Maindata.length(); i++) {
                    if (Maindata.charAt(i) != '"')
                        data.append(Maindata.charAt(i));
                    else
                        break;
                }
                return "http://www.naghdefarsi.com" + data.toString();
            } else {
                if (Maindata.indexOf("<p><span style=\"font-size: small;\"><img src=\"") > 0) {
                    for (int i = Maindata.indexOf("<p><span style=\"font-size: small;\"><img src=\"") + 45; i < Maindata.length(); i++) {
                        if (Maindata.charAt(i) != '"')
                            data.append(Maindata.charAt(i));
                        else
                            break;
                    }
                    return "http://www.naghdefarsi.com" + data.toString();
                }
            }
        } else {
            for (int i = pos + 29; i < Maindata.length(); i++) {
                if (Maindata.charAt(i) != '"')
                    data.append(Maindata.charAt(i));
                else
                    break;
            }
            return "http://www.naghdefarsi.com" + data.toString();
        }
        if (Maindata.indexOf("<div id=\"sbox-content\" style=\"visibility: visible; opacity: 1;\"><img src=\"") > 0) {
            for (int i = Maindata.indexOf("<div id=\"sbox-content\" style=\"visibility: visible; opacity: 1;\"><img src=\"") + 74; i < Maindata.length(); i++) {
                if (Maindata.charAt(i) != '"')
                    data.append(Maindata.charAt(i));
                else
                    break;
            }
            return "http://www.naghdefarsi.com" + data.toString();
        } else if (Maindata.indexOf("<div style=\"visibility: visible; opacity: 1;\"><img class=\"decoded\" src=\"") > 0) {
            for (int i = Maindata.indexOf("<div style=\"visibility: visible; opacity: 1;\"><img class=\"decoded\" src=\"") + 72; i < Maindata.length(); i++) {
                if (Maindata.charAt(i) != '"')
                    data.append(Maindata.charAt(i));
                else
                    break;
            }
            return "http://www.naghdefarsi.com" + data.toString();
        } else if (Maindata.indexOf("<div id=\"sbox-content\" style=\"visibility: visible; opacity: 1;\"><img class=\"decoded\" src=\"") > 0) {
            for (int i = Maindata.indexOf("<div id=\"sbox-content\" style=\"visibility: visible; opacity: 1;\"><img class=\"decoded\" src=\"") + 90; i < Maindata.length(); i++) {
                if (Maindata.charAt(i) != '"')
                    data.append(Maindata.charAt(i));
                else
                    break;
            }
            return "http://www.naghdefarsi.com" + data.toString();
        }
        return "";
    }

    private static String FINDENFARSISITE(String FindName, String Maindata) {
        String Namesave = "";
        int pos = Maindata.indexOf(FindName, 0);
        if (Maindata.length() == 0)
            return "اطلاعات یافت نشد";
        String temp = Maindata.substring(pos + FindName.length(), pos + FindName.length() + 350);
        for (int i = 0; i < temp.length(); i++) {
            if (temp.charAt(i) != ',' && temp.charAt(i) != '(') {
                if (!(temp.charAt(i) >= 'آ' && temp.charAt(i) <= 'ی'))
                    Namesave += temp.charAt(i);
            } else
                break;
        }
        String namesave1 = "";
        pos = Maindata.indexOf("<meta name=\"title\" content=\"", 0);
        String temp1 = Maindata.substring(pos + 28, pos + FindName.length() + 350);
        for (int i = 0; i < temp1.length(); i++) {
            if (temp1.charAt(i) != ',' && temp1.charAt(i) != '(') {
                if (!(temp1.charAt(i) >= 'آ' && temp1.charAt(i) <= 'ی'))
                    namesave1 += temp1.charAt(i);
            } else
                break;
        }
        if (Namesave.length() != 0) {
            if (Namesave.length() >= namesave1.length())
                return namesave1;
            else
                return Namesave;
        } else
            return namesave1.trim();
    }

    private void prossesonFarsi(final String data) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase DBTemp;
                database movieDB = new database(getApplicationContext(), "Movie_db", null, 1);
                String link;
                boolean checkDatabase = false;
                DBTemp = movieDB.getReadableDatabase();
                DBTemp.execSQL("delete from " + "MOVIEFARSIDATA1");
                Cursor cursor = DBTemp.query("MOVIEDATA", new String[]{"Movie_Rank", "IMDB_RANK_simple", "Movie_year", "Movie_name", "Movie_farsi_name", "MovieDirector_name", "Actors_Actress", "IMDB_RANK_full", "Movie_IMDB_link", "Movie_picture_link"}, null, null, null, null, null);
                String[] movieen = new String[251];
                int counteren = 0;
                cursor.moveToNext();
                while (cursor.moveToNext()) {
                    movieen[counteren] = cursor.getString(cursor.getColumnIndex("Movie_name"));
                    counteren++;
                }
                DBTemp.close();
                String Farsiname;
                String ENname;
                int pos = 0;
                int count = 0;
                DBTemp = movieDB.getWritableDatabase();
                ContentValues in = new ContentValues();
                while (true) {
                    if (count == 251)
                        break;
                    String findLinkFarsiStory = "<div style=\"text-align: right;\"><span style=\"font-size: small;\"><a href=\"";
                    link = findLink(findLinkFarsiStory, data, pos);
                    Farsiname = findFarsiname(findLinkFarsiStory, data, pos);
                    ENname = findEnname(findLinkFarsiStory, data, pos);
                    for (int i = 0; i < movieen.length; i++) {
                        if (ENname.substring(0, ENname.length() - 1).equalsIgnoreCase(movieen[i])) {
                            ContentValues c = new ContentValues();
                            c.put("Movie_farsi_name", Farsiname);
                            DBTemp.update("MOVIEDATA", c, "Movie_Rank=" + (i + 1), null);
                        }
                    }
                    in.put("Movie_farsi_name", Farsiname);
                    in.put("link", link);
                    in.put("ENmoviename", ENname.substring(0, ENname.length() - 1));
                    DBTemp.insert("MOVIEFARSIDATA1", null, in);
                    pos = data.indexOf(findLinkFarsiStory, pos + 5);
                    if (DownloadFA.isChecked()) {
                        Cursor cursor1 = DBTemp.query("MOVIEFARSIDATA2", new String[]{"Movie_ENname", "Movie_time", "Movie_Style", "Movie_Award", "MovieStory", "MovieQUPIC", "Movie_Naghed", "Download_movie_link_720p", "Download_movie_link_1080p", "Download_movie_subtitle_link", "isdubled"}, null, null, null, null, null);
                        for (int i = 0; cursor1.moveToNext(); i++) {
                            if (cursor1.getString(cursor1.getColumnIndex("Movie_ENname")).equalsIgnoreCase(movieen[i])) {
                                checkDatabase = true;
                                break;
                            }
                        }
                        if (!checkDatabase)
                            new prossesonFarsiSite().execute(link);
                        checkDatabase = false;
                    }
                    count++;
                }
                Intent sendbroadcastFA = new Intent();
                sendbroadcastFA.setAction("FADownloded");
                sendBroadcast(sendbroadcastFA);
                DBTemp.close();
            }
        });
        thread.run();
    }

    private void prossesonFarsiSite(final String data) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                ContentValues c = new ContentValues();
                SQLiteDatabase DBTemp;
                database movieDB = new database(getApplicationContext(), "Movie_db", null, 1);
                DBTemp = movieDB.getWritableDatabase();
                String Story, Award, time, Style, pic, enname;
                String awrd = "جوایز";
                String story = "<span style=\"color: #333333;\">خلاصه داستان";
                String times = "دقیقه";
                String EnnameforParsisite = "<meta name=\"keywords\" content=\"";
                String qulityPIc = "<p><img class=\"decoded\" src=\"";
                Award = findAward(awrd, data);
                c.put("Movie_Award", Award);
                time = findTime(times, data);
                c.put("Movie_time", time);
                Style = findStyle(times, data);
                c.put("Movie_Style", Style);
                Story = findStory(story, data);
                c.put("MovieStory", Story);
                pic = findPICQU(qulityPIc, data);
                c.put("MovieQUPIC", pic);
                enname = FINDENFARSISITE(EnnameforParsisite, data);
                c.put("Movie_ENname", enname);
                DBTemp.insert("MOVIEFARSIDATA2", null, c);
                DBTemp.close();
            }
        });
        thread.run();
    }

    private void LoadPic()
    {
        List<Pair<String,String>>DownloadList=new ArrayList<>();
        SQLiteDatabase temp;
        database movieDB = new database(getApplicationContext(), "Movie_db", null, 1);
        temp = movieDB.getReadableDatabase();
        Cursor cursor = temp.query("MOVIEDATA", new String[]{"Movie_Rank", "IMDB_RANK_simple", "Movie_year", "Movie_name", "Movie_farsi_name", "MovieDirector_name", "Actors_Actress", "IMDB_RANK_full", "Movie_IMDB_link", "Movie_picture_link"}, null, null, null, null, null);
        cursor.moveToFirst();
        cursor.moveToNext();
        while (cursor.moveToNext()) {
            if (!cursor.getString(cursor.getColumnIndex("Movie_picture_link")).equals("")) {
                File file = new File(getApplicationContext().getFilesDir().getPath() + "/" + cursor.getString(cursor.getColumnIndex("Movie_name")).trim());
                if (!file.exists()) {
                    ++AsyncTaskcounter;
                    String url = cursor.getString(cursor.getColumnIndex("Movie_picture_link"));
                    final String name = cursor.getString(cursor.getColumnIndex("Movie_name"));
                    Log.e("AsyncTaskcounter", "MovieName:" + name + "  PICLink:" + url);
                    DownloadList.add(new Pair<>(name,url));
                }
            }
        }

        DownloadPic(DownloadList,0);

        temp.close();
        if (currnetprogress == AsyncTaskcounter) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            Notification.Builder builder = new Notification.Builder(getApplicationContext());
            Intent intent = new Intent();
            PendingIntent pendingIntent = PendingIntent.getActivity(DownloadData.this, 1, intent, 0);
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
            Toast.makeText(getApplicationContext(), "پوستر ها دانلود شد", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

   private void DownloadPic(List<Pair<String,String>>List,final int index)
    {
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
                               resource.compress(Bitmap.CompressFormat.PNG, 100, fos);
                               fos.flush();
                               fos.close();
                           } catch (Exception e) {
                               e.printStackTrace();
                           } finally {
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
                               PendingIntent pendingIntent = PendingIntent.getActivity(DownloadData.this, 1, intent, 0);
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
                                   Toast.makeText(getApplicationContext(), "پوستر ها دانلود شد", Toast.LENGTH_SHORT).show();
                                   finish();
                               }
                           }
                       }
                   });
       }
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
    @Override
    protected void onDestroy() {
        if(progressDialog!=null)
        progressDialog.dismiss();
        if(buldernoti!=null)
        buldernoti.setOngoing(false);

        NotificationManager mNotificationManager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);
        assert mNotificationManager != null;
        mNotificationManager.cancelAll();
        super.onDestroy();
    }
}
