package com.ar.movieinformation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
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
import android.graphics.drawable.Drawable;
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
import android.view.View;
import android.widget.CheckBox;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
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
import java.util.Calendar;
import java.util.Date;

public class DownloadData extends AppCompatActivity {
    CheckBox Downloadtext, DownloadPIC, DownloadFA;
    ProgressDialog progressDialog;
    int counter = 0;
    int counter1 = 0;
    String checkHTML = "";
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
                            new prossesonFarsidata().execute("http://www.naghdefarsi.com/movies/top-movies.html");
                            //new Checkname_other_site().execute("http://m.imdb.com/chart/top");
                           // new prossesondata().execute("http://www.imdb.com/chart/top");
                        }
                    });
                    builder.setNegativeButton("خیر", (dialog, which) -> Toast.makeText(getApplicationContext(), "برای نمایش لیست فیلم ها باید اطلاعات دریافت شود ", Toast.LENGTH_LONG).show());
                    builder.show();
                } else
                    Toast.makeText(getApplicationContext(), "دانلود اطلاعات متنی انگلیسی ضروری است", Toast.LENGTH_LONG).show();
            }
        }
    }



   /* private class prossesondata extends AsyncTask<String, String, String> {
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

                Movie_String_data_HTMLsave=org.apache.commons.io.IOUtils.toString(inputMoviedata);
                inputMoviedata.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if(Movie_String_data_HTMLsave!=null)
            workonStringDataAndSavetoDataBase(Movie_String_data_HTMLsave);
        }
    }
    */

   /* private class Checkname_other_site extends AsyncTask<String, String, String> {
        String Movie_String_data_HTMLsave = null;

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
                Movie_String_data_HTMLsave=org.apache.commons.io.IOUtils.toString(inputMoviedata);
                inputMoviedata.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s)
        {
            if(Movie_String_data_HTMLsave!=null)
            checkHTML = Movie_String_data_HTMLsave;
        }
    }
*/
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

    @SuppressLint("StaticFieldLeak")
    private class prossesonPIC extends AsyncTask<String, String, String> {
        String name;
        @Override
        protected String doInBackground(String... url) {
            final String filename = url[0];
            name=filename;
            String year=url[1];
            String userAgent = "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.116 Safari/537.36";
            String farsi="naghedfarsi";
            String googleurl = "https://www.google.com/search?q="+filename.replaceAll(" ","+").trim()+"+"+year+farsi+"&site=imghp&tbs=isz:m&tbm=isch&source=lnt&sa=X&ved=0ahUKEwjrnuy1vJjYAhVBEmMKHUWHCioQpwUIIA&biw=1280&bih=642&dpr=3";
            try {
                Document doc = Jsoup.connect(googleurl).userAgent(userAgent).referrer("https://www.google.com/").get();
                Elements elements = doc.select("div.rg_meta");
                JSONObject jsonObject;
                for (Element element : elements) {
                    if (element.childNodeSize() > 0) {
                        jsonObject = (JSONObject) new JSONParser().parse(element.childNode(0).toString());
                       return (String) jsonObject.get("ou");
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            DownloadCoverFromGoogle(s,name);
            }
        }
    private void DownloadCoverFromGoogle(final String url, final String Filename)
    {
        counter1++;
        Toast.makeText(getApplicationContext(),url+" "+Filename+"downloading",Toast.LENGTH_SHORT).show();
        Picasso.with(getApplicationContext()).load(url).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom loadedFrom) {
                try {
                    FileOutputStream fos = new FileOutputStream(getApplicationContext().getFilesDir().getPath() + "/" + Filename.trim());
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 75, fos);
                    fos.flush();
                    fos.close();
                    Toast.makeText(getApplicationContext(),url+" "+Filename+"downloaded",Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    currnetprogress++;
                    RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.noti);
                    contentView.setTextViewText(R.id.status_text, currnetprogress + " از " + AsyncTaskcounter);
                    contentView.setTextViewText(R.id.titlenoti, "اطلاعات فیلم");
                    contentView.setImageViewBitmap(R.id.status_icon, BitmapFactory.decodeResource(getApplicationContext().getResources(),
                            R.mipmap.ic_launcher));
                    contentView.setTextViewText(R.id.textnoti, "در حال دریافت پوستر ها...");
                    contentView.setProgressBar(R.id.status_progress, AsyncTaskcounter, currnetprogress, false);
                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    Intent intent = new Intent();
                    PendingIntent pendingIntent = PendingIntent.getActivity(DownloadData.this, 1, intent, 0);
                    builder.setAutoCancel(false);
                    //   builder.setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(),
                    //         R.mipmap.ic_launcher));
                    builder.setOngoing(true);
                    builder.setContentTitle("اطلاعات فیلم");
                    builder.setSmallIcon(R.mipmap.ic_launcher);
                    builder.setContentIntent(pendingIntent);
                    //builder.setCustomBigContentView(contentView);
                    builder.setCustomContentView(contentView);
                    builder.build();
                    Notification notification1 = builder.getNotification();
                    notificationManager.notify("Movie app", 0, notification1);
                    if (AsyncTaskcounter == currnetprogress && !checkpic) {
                        checkpic = true;
                        // Toast.makeText(getApplicationContext(), counter1+" پوستر جدید دانلود شد ", Toast.LENGTH_SHORT).show();
                        Notification.Builder builder = new Notification.Builder(getApplicationContext());
                        builder.setAutoCancel(true);
                        builder.setOngoing(false);
                        builder.setSmallIcon(R.mipmap.ic_launcher);
                        builder.setContentTitle("اطلاعات فیلم");
                        builder.setContentText("تمام اطلاعات دریافت شد");
                        builder.setContentIntent(pendingIntent);
                        builder.setColor(getApplicationContext().getResources()
                                .getColor(R.color.backgroundunSwith));
                        builder.build();
                        Notification notification = builder.getNotification();
                        notificationManager.notify("Movie app", 0, notification);
                        Toast.makeText(getApplicationContext(), "پوستر ها دانلود شد", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }
            @Override
            public void onBitmapFailed(Drawable drawable) {

            }

            @Override
            public void onPrepareLoad(Drawable drawable) {

            }
        });
    }
    int prosses = 0;
    private void workonStringDataAndSavetoDataBase(String data) {
        final String finaldata = data;
        Thread movie_thred = new Thread(new Runnable() {
            @TargetApi(Build.VERSION_CODES.N)
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void run() {
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
                String FindRank = "<td class=\"titleColumn\">";
                String FINDyear = "<span class=\"secondaryInfo\">";
                String Fullimdb = "<td class=\"ratingColumn imdbRating\">\n";
                String MainTitle = "<td class=\"posterColumn\">";
                String key = "div class=\"col-xs-12 col-md-6\">\n" +
                        "    <div class=\"media\">";
                String ActorsandActress = "", IMDB_RANK_simple = "", IMDB_RANK_full = "", Ranksave = "", Picsave = "", NameSave = "", Yearsave = "", IMDBsave = "", DIRName = "";
                int pos = 0, EndPos = 0, startpos = 0;
                DBTemp = movieDB.getWritableDatabase();
                while (true) {
                    //NameSave = FINDNAME(FindRank, finaldata, pos, startpos);
                    Picsave = FINDPIC(MainTitle, finaldata, pos, startpos);
                    Yearsave = FINDYear(FINDyear, finaldata, pos, startpos);
                    Ranksave = FINDRANK(FindRank, finaldata, pos, startpos);
                    DIRName = FINDDIR(FindRank, finaldata, pos, startpos);
                    IMDBsave = FINDIMDB(MainTitle, finaldata, pos, startpos);
                    ActorsandActress = FindActorANDActress(FindRank, finaldata, pos, startpos);
                    IMDB_RANK_full = FINDIMDB_rank_full(Fullimdb, finaldata, pos, startpos);
                    IMDB_RANK_simple = FINDIMDB_rank_simple(Fullimdb, finaldata, pos, startpos);
                    contentValues.put("Movie_name", NameSave);
                    contentValues.put("Actors_Actress", ActorsandActress);
                    contentValues.put("IMDB_RANK_full", IMDB_RANK_full);
                    contentValues.put("IMDB_RANK_simple", IMDB_RANK_simple);
                    contentValues.put("Movie_year", Yearsave);
                    contentValues.put("MovieDirector_name", DIRName);
                    contentValues.put("Movie_Rank", Ranksave);
                    contentValues.put("Movie_picture_link", Picsave);
                    contentValues.put("Movie_IMDB_link", IMDBsave);
                    DBTemp.insert("MOVIEDATA", null, contentValues);
                    // DBTemp.update("MOVIEDATA",contentValues,"Movie_Rank="+(Integer.parseInt(Ranksave)),null);
                    startpos = finaldata.indexOf(MainTitle, startpos + 1);
                    progressDialog.setProgress(prosses++);
                    if (Integer.parseInt(Ranksave) == 250)
                        break;
                }
                int checkpos = 0;
                String name = "", rank = "";
                ContentValues contentValues2 = new ContentValues();
                while (true) {
                    name = MovieName_otherSite(checkHTML, key, checkpos);
                    rank = MovieRank_otherSite(checkHTML, key, checkpos);
                    contentValues2.put("Movie_name", name);
                    checkpos = checkHTML.indexOf(key, checkpos + 1);
                    DBTemp.update("MOVIEDATA", contentValues2, "Movie_Rank=" + (rank), null);
                    if (Integer.parseInt(rank) == 250)
                        break;
                }
                prossesonFarsi(farsi);
                progressDialog.dismiss();
                DBTemp.close();
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
                notificationManager.notify("Movie app", 0, notification);
                AlertDialog.Builder alert = new AlertDialog.Builder(DownloadData.this);
                alert.setMessage("اطلاعات متنی دانلود شد");
                alert.setPositiveButton("باشه", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (DownloadPIC.isChecked()) {
                            Toast.makeText(getApplicationContext(), "در حال دریافت پوستر ها،لطفا صبر کنید...", Toast.LENGTH_SHORT).show();
                        }
                        Intent intent = new Intent(DownloadData.this, movie_list.class);
                        startActivity(intent);
                        if (!DownloadPIC.isChecked() && !DownloadFA.isChecked()) {
                            finish();
                        }
                    }
                });
                alert.setCancelable(false);
                alert.show();
                if (DownloadPIC.isChecked())
                    ProseseonPICs(1);
                //if(DownloadFA.isChecked())
            }
        });
        movie_thred.run();
    }

    private static String FINDRANK(String FindRank, String Maindata, int pos, int Startpos) {
        String Ranksave = "";
        pos = Maindata.indexOf(FindRank, Startpos);
        String temp = Maindata.substring(pos + FindRank.length(), pos + FindRank.length() + 16);
        for (int i = 0; i < temp.length(); i++)
            if ((temp.charAt(i) <= '9') && (temp.charAt(i) >= '0'))
                Ranksave += temp.charAt(i);
        return Ranksave;
    }

    private static String FINDIMDB_rank_full(String FindRank, String Maindata, int pos, int Startpos) {
        String Ranksave = "";
        pos = Maindata.indexOf(FindRank, Startpos);
        String temp = Maindata.substring(pos + FindRank.length() + 27, pos + FindRank.length() + 70);
        for (int i = 0; i < temp.length(); i++)
            if ((temp.charAt(i) != '"'))
                Ranksave += temp.charAt(i);
            else
                break;
        return Ranksave;
    }

    private static String FINDIMDB_rank_simple(String FindRank, String Maindata, int pos, int Startpos) {
        String Ranksave = "";
        pos = Maindata.indexOf(FindRank, Startpos);
        String temp = Maindata.substring(pos + FindRank.length() + 27, pos + FindRank.length() + 30);
        for (int i = 0; i < temp.length(); i++)
            if ((temp.charAt(i) != '"'))
                Ranksave += temp.charAt(i);
            else
                break;
        return Ranksave;
    }

    private static String FINDPIC(String FindPic, String Maindata, int pos, int Startpos) {
        String Picsave = "";
        pos = Maindata.indexOf(FindPic, Startpos);
        String temp = Maindata.substring(pos + FindPic.length() + 400, pos + FindPic.length() + 600);
        int POS = temp.indexOf("img src=\"");
        for (int i = POS + 9; i < temp.length(); i++)
            if (temp.charAt(i) != '"')
                Picsave += temp.charAt(i);
            else
                break;
        return Picsave;
    }

    private static String MovieName_otherSite(String main, String key, int pos) {
        int Pos = main.indexOf(key, pos);
        String name = "";
        String temp = main.substring(Pos + key.length() + 700, Pos + key.length() + 1000);
        String findname = "<span class=\"unbold\">";
        String temp2 = temp.substring(temp.indexOf(findname) + findname.length(), temp.indexOf(findname) + findname.length() + 60);
        for (int i = temp2.indexOf("</span>") + 8; i < temp2.length(); i++) {
            if (temp2.charAt(i) != '<')
                name += temp2.charAt(i);
            else
                break;
        }
        return name.trim();
    }

    private static String MovieRank_otherSite(String main, String key, int pos) {
        int Pos = main.indexOf(key, pos);
        String rank = "";
        String temp = main.substring(Pos + key.length() + 700, Pos + key.length() + 1000);
        String findname = "<span class=\"unbold\">";
        String temp2 = temp.substring(temp.indexOf(findname) + findname.length(), temp.indexOf(findname) + findname.length() + 10);
        for (int i = 0; i < temp2.length(); i++) {
            if (temp2.charAt(i) != '<')
                if (temp2.charAt(i) >= '0' && temp2.charAt(i) <= '9')
                    rank += temp2.charAt(i);
                else
                    break;
        }
        return rank;
    }

    private static String FINDNAME(String FindName, String Maindata, int pos, int Startpos) {
        String Namesave = "";
        pos = Maindata.indexOf(FindName, Startpos);
        String temp = Maindata.substring(pos + FindName.length() + 220, pos + FindName.length() + 350);
        int POS = temp.indexOf(" >");
        for (int i = POS + 2; i < temp.length(); i++) {
            if (temp.charAt(i) != '<')
                Namesave += temp.charAt(i);
            else
                break;
        }
        return Namesave;
    }

    private static String FindActorANDActress(String FindDIR, String Maindata, int pos, int Startpos) {
        String Acirsave = "";
        pos = Maindata.indexOf(FindDIR, Startpos);
        String temp = Maindata.substring(pos + FindDIR.length() + 181, pos + FindDIR.length() + 280);
        int DIRpos = temp.indexOf("(dir.)");
        for (int i = DIRpos + 8; i < temp.length(); i++) {
            if (temp.charAt(i) != '"')
                if (temp.charAt(i) == ',')
                    Acirsave += " -";
                else
                    Acirsave += temp.charAt(i);
            if (temp.charAt(i) == '"')
                break;
        }
        return Acirsave;
    }

    private static String FINDDIR(String FindDIR, String Maindata, int pos, int Startpos) {
        String Dirsave = "";
        pos = Maindata.indexOf(FindDIR, Startpos);
        String temp = Maindata.substring(pos + FindDIR.length() + 180, pos + FindDIR.length() + 230);
        int POS = temp.indexOf("=\"");
        for (int i = POS + 2; i < temp.length(); i++) {
            if (temp.charAt(i) != ' ' && temp.charAt(i) != '"' && temp.charAt(i) != '(')
                Dirsave += temp.charAt(i);
            if (temp.charAt(i) == '(')
                break;
        }
        return Dirsave;
    }

    private static String FINDYear(String FindYear, String Maindata, int pos, int Startpos) {
        String Yearsave = "";
        pos = Maindata.indexOf(FindYear, Startpos);
        String temp = Maindata.substring(pos + FindYear.length() + 1, pos + FindYear.length() + 5);
        for (int i = 0; i < temp.length(); i++)
            if ((temp.charAt(i) <= '9') && (temp.charAt(i) >= '0'))
                Yearsave += temp.charAt(i);
        return Yearsave;
    }

    private static String FINDIMDB(String FindIMDB, String Maindata, int pos, int Startpos) {
        String IMDbsave = "";
        pos = Maindata.indexOf(FindIMDB, Startpos);
        String temp = Maindata.substring(pos + FindIMDB.length() + 200, pos + FindIMDB.length() + 600);
        int POS = temp.indexOf("<a href=\"");
        for (int i = POS + 9; i < temp.length(); i++) {
            if (temp.charAt(i) != '"')
                IMDbsave += temp.charAt(i);
            else
                break;
        }
        return "http://www.imdb.com" + IMDbsave;
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

    private void ProseseonPICs(int code) {
        SQLiteDatabase temp;
        database movieDB = new database(getApplicationContext(), "Movie_db", null, 1);
        temp = movieDB.getReadableDatabase();
        Cursor cursor1 = temp.query("MOVIEFARSIDATA2", new String[]{"Movie_ENname", "Movie_time", "Movie_Style", "Movie_Award", "MovieStory", "MovieQUPIC", "Movie_Naghed", "Download_movie_link_720p", "Download_movie_link_1080p", "Download_movie_subtitle_link", "isdubled"}, null, null, null, null, null);
        cursor1.moveToNext();
        while (cursor1.moveToNext()) {
            if (!cursor1.getString(cursor1.getColumnIndex("MovieQUPIC")).equals("")) {
                File file = new File(getApplicationContext().getFilesDir().getPath() + "/" + cursor1.getString(cursor1.getColumnIndex("Movie_ENname")).trim());
                if (!file.exists()) {
                    AsyncTaskcounter++;
                    String url=cursor1.getString(cursor1.getColumnIndex("MovieQUPIC"));
                    final String name=cursor1.getString(cursor1.getColumnIndex("Movie_ENname"));
                    Picasso.with(getApplicationContext()).load(url).into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom loadedFrom) {
                            try {
                                FileOutputStream fos = new FileOutputStream(getApplicationContext().getFilesDir().getPath() + "/" + name.trim());
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
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
                                notificationManager.notify("Movie app", 0, notification1);
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

                        @Override
                        public void onBitmapFailed(Drawable drawable) {

                        }

                        @Override
                        public void onPrepareLoad(Drawable drawable) {

                        }
                    });
                }
            }
        }
        Cursor cursor = temp.query("MOVIEDATA", new String[]{"Movie_Rank", "IMDB_RANK_simple", "Movie_year", "Movie_name", "Movie_farsi_name", "MovieDirector_name", "Actors_Actress", "IMDB_RANK_full", "Movie_IMDB_link", "Movie_picture_link"}, null, null, null, null, null);
        cursor.moveToFirst();
        while (cursor.moveToNext()) {
            File file = new File(getApplicationContext().getFilesDir().getPath() + "/" + cursor.getString(cursor.getColumnIndex("Movie_name")).trim());
            if (!file.exists()) {
                AsyncTaskcounter++;
                new prossesonPIC().execute(cursor.getString(cursor.getColumnIndex("Movie_name")),cursor.getString(cursor.getColumnIndex("Movie_year")));
            }
        }

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
            notificationManager.notify("Movie app", 0, notification);
            Toast.makeText(getApplicationContext(), "پوستر ها دانلود شد", Toast.LENGTH_SHORT).show();
            finish();
        }
        temp.close();
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
        mNotificationManager.cancelAll();
        super.onDestroy();
    }
}
