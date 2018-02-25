package com.ar.movieinformation;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.ar.movieinformation.OMDB.Model.ShortPlot;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class firstlayout extends AppCompatActivity{
    SpinKitView spinKitView;
    @SuppressLint("StaticFieldLeak")
    private class ExtractData extends  AsyncTask<Void,Void,Void> {
        SharedPreferences.Editor editor = getSharedPreferences("movieinfosh", MODE_PRIVATE).edit();


        @Override
        protected Void doInBackground(Void... voids) {
            try {

                DeleteDatabase();
                ExtractDatas();
                Log.e("copyCalled", "called");

            } catch (Exception e) {
                Log.e("Exception", e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            new LoadData().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            editor.putBoolean("isExtracted", true);
            editor.apply();

        }
    }

    @SuppressLint("StaticFieldLeak")
    private  class LoadData extends AsyncTask<Void,Void,Void>
    {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                if(isDTATASEXIST()) {
                    Gson gson = new Gson();
                    SharedPreferences DATAS = getApplication().getSharedPreferences("MOVIE_INFO_DATA_PREF", MODE_PRIVATE);
                    Type type = new TypeToken<List<Movie>>() {
                    }.getType();
                    movie_list.MovieList = gson.fromJson(DATAS.getString("MOVIES", null), type);

                    SharedPreferences OLDDATAS = getApplication().getSharedPreferences("MOVIE_INFO_OLD_DATA_PREF", MODE_PRIVATE);
                    Type type2 = new TypeToken<Map<String, Movie>>() {
                    }.getType();
                    movie_list.MovieListOld = gson.fromJson(OLDDATAS.getString("MOVIES", null), type2);

                    SharedPreferences movies = getApplication().getSharedPreferences("MOVIE_INFO_AD_PREF", MODE_PRIVATE);
                    Type type1 = new TypeToken<Map<String, ShortPlot>>() {
                    }.getType();
                    movie_list.MovieData = gson.fromJson(movies.getString("MOVIES", null), type1);
                }
                else InCurrectAlertShow();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.e("LoadNextPage","Called");
            spinKitView.setVisibility(View.GONE);
            if(isDATASCorrect())
            LoadNextPage();
            else InCurrectAlertShow();

        }
    }
    private void InCurrectAlertShow()
    {
        SharedPreferences.Editor editor = getSharedPreferences("movieinfosh", MODE_PRIVATE).edit();
        AlertDialog.Builder alert=new AlertDialog.Builder(this);
        //  final AlertDialog.Builder alert1=new AlertDialog.Builder(this);
        alert.setCancelable(false);
        alert.setTitle("اطلاعات فیلم");
        alert.setMessage("به نظر می رسد به اطلاعات اصلی برنامه آسیب وارد شده است،برای اصلاح آن اطلاعات اولیه کپی می شود. ");
        alert.setPositiveButton("باشه", (dialog, which) -> {
            DeleteDatabase();
            new ExtractData().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            editor.putInt("DBupdate_version_Year",2018);
            editor.putInt("DBupdate_version_Day",50);
            editor.apply();
            //  alert1.setTitle("اطلاعات فیلم");
            // alert1.setMessage("اطلاعات اولیه کپی شد");
            //  alert1.setPositiveButton("باشه", (dialog1, which1) -> {
            // spinKitView.setVisibility(View.GONE);
            //     dialog.dismiss();
            //     dialog1.dismiss();
            //     finish();
            //  });
            //  alert1.setCancelable(true);
            //  alert1.show();
        });
        alert.show();

    }
    private boolean isDTATASEXIST()
    {
        String PREF1_r="MOVIE_INFO_AD_PREF";
        String path="/data/data/"+getApplication().getPackageName()+"/shared_prefs/"+PREF1_r+".xml";
        File PREF1File = new File(path);

        String PREF2_r="MOVIE_INFO_DATA_PREF";
        String path1="/data/data/"+getApplication().getPackageName()+"/shared_prefs/"+PREF2_r+".xml";
        File PREF2File = new File(path1);

        String PREF3_r="MOVIE_INFO_OLD_DATA_PREF";
        String path2="/data/data/"+getApplication().getPackageName()+"/shared_prefs/"+PREF3_r+".xml";
        File PREF3File = new File(path2);

       return PREF1File.exists() && PREF2File.exists() && PREF3File.exists();

    }
    private void ExtractDatas()
    {

        try {
            copyPREF();
            copyPICS();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void LoadNextPage()
    {

        final SharedPreferences.Editor editor= getSharedPreferences("movieinfosh",MODE_PRIVATE).edit();
        final SharedPreferences getstatus=getSharedPreferences("movieinfosh",MODE_PRIVATE) ;
        editor.putInt("StartupCount", getstatus.getInt("StartupCount", 0) + 1);
        editor.apply();

        Intent intent=new Intent(firstlayout.this,movie_list.class);
        startActivity(intent);
        finish();
    }
    private void ShowForFirstTime()
    {
        SharedPreferences getstatus= getSharedPreferences("movieinfosh",MODE_PRIVATE);
        SharedPreferences.Editor editor = getSharedPreferences("movieinfosh", MODE_PRIVATE).edit();
        PackageManager manager = getApplicationContext().getPackageManager();
        PackageInfo info = null;
        String versionsn="";
        int versionsc=0;
        try {
            info = manager.getPackageInfo(
                    getApplicationContext().getPackageName(), 0);
            versionsn = info.versionName;
            versionsc=info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if(isNewVerInstall(versionsc))
        {
            AlertDialog.Builder showUpdateNoti = new AlertDialog.Builder(this);
            showUpdateNoti.setCancelable(false);
            showUpdateNoti.setTitle("اطلاعات فیلم");
                showUpdateNoti.setMessage("ویژگی های نسخه جدید:" + "\n" + "1-اضافه شدن قابلیت نمایش اطلاعات بیشتر از هر فیلم" + "\n" + "2-اضافه شدن داستان انگلیسی هر فیلم و دریافت ترجمه آن از مترجم گوگل در صورت عدم وجود ترجمه در سایت نقد فارسی" + "\n" + "3-اصافه شدن دریافت نام فارسی فیلم ها از مترجم گوگل" + "\n"+"و برطرف شدن برخی از ایرادات برنامه"+  "\n\n" + "برای اجرای برنامه اطلاعات اولیه کپی می شود");
                final String finalVersionsn = versionsn;
                final int finalVersionsc = versionsc;
                showUpdateNoti.setPositiveButton("بروز رسانی اطلاعات", (dialog, which) -> {

                    try {
                        DeleteDatabase();
                        DeletePics();
                        new ExtractData().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    finally {
                        editor.putBoolean("ShowUpdateText", true);
                        editor.putString("AppVER", finalVersionsn);
                        editor.putInt("AppCODE", finalVersionsc);
                        editor.apply();
                    }
                });
                showUpdateNoti.show();
        }
        if(!getstatus.getBoolean("ShowUpdateText",false)) {
            AlertDialog.Builder showUpdateNoti = new AlertDialog.Builder(this);
            showUpdateNoti.setCancelable(false);
            showUpdateNoti.setTitle("اطلاعات فیلم");
            if (getstatus.getBoolean("isExtracted", false)) {
                showUpdateNoti.setMessage("ویژگی های نسخه جدید:" + "\n" + "1-اضافه شدن قابلیت نمایش اطلاعات بیشتر از هر فیلم" + "\n" + "2-اضافه شدن داستان انگلیسی هر فیلم و دریافت ترجمه آن از مترجم گوگل در صورت عدم وجود ترجمه در سایت نقد فارسی" + "\n" + "3-اصافه شدن دریافت نام فارسی فیلم ها از مترجم گوگل" + "\n"+"و برطرف شدن برخی از ایرادات برنامه"+  "\n\n" + "برای اجرای برنامه اطلاعات اولیه کپی می شود");
                final String finalVersionsn = versionsn;
                final int finalVersionsc = versionsc;
                showUpdateNoti.setPositiveButton("بروز رسانی اطلاعات", (dialog, which) -> {

                    try {
                        DeleteDatabase();
                        DeletePics();
                        new ExtractData().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    finally {
                        editor.putBoolean("ShowUpdateText", true);
                        editor.putString("AppVER", finalVersionsn);
                        editor.putInt("AppCODE", finalVersionsc);
                        editor.apply();
                    }
                });
                showUpdateNoti.show();
            } else {
                showUpdateNoti.setMessage("ویژگی های نسخه جدید:" + "\n" + "1-اضافه شدن قابلیت نمایش اطلاعات بیشتر از هر فیلم" + "\n" + "2-اضافه شدن داستان انگلیسی هر فیلم و دریافت ترجمه آن از مترجم گوگل در صورت عدم وجود ترجمه در سایت نقد فارسی" + "\n" + "3-اصافه شدن دریافت نام فارسی فیلم ها از مترجم گوگل" + "\n"+"و برطرف شدن برخی از ایرادات برنامه"+  "\n\n" + "برای اجرای برنامه اطلاعات اولیه کپی می شود");
                final String finalVersionsn1 = versionsn;
                final int finalVersionsc1 = versionsc;
                showUpdateNoti.setPositiveButton("باشه", (dialog, which) -> {
                    try {
                        DeletePics();
                        DeleteDatabase();
                        new ExtractData().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    finally {
                        editor.putString("AppVER", finalVersionsn1);
                        editor.putInt("AppCODE", finalVersionsc1);
                        editor.putBoolean("ShowUpdateText", true);
                        editor.putBoolean("isExtracted",true);
                        editor.apply();
                    }
                });
                showUpdateNoti.show();
            }
        }


    }

    private boolean isDATASCorrect()
    {
        if(movie_list.MovieList!=null&&movie_list.MovieData!=null&& movie_list.MovieListOld!=null)
        Log.e("DataSizes","MovieList:"+movie_list.MovieList.size()+"   MovieData:"
        +movie_list.MovieData.size()+"   MovieListOld:"+movie_list.MovieListOld.size());
        if(movie_list.MovieList!=null && movie_list.MovieList.size()<250
                && movie_list.MovieListOld!=null && movie_list.MovieListOld.size()==250)
        {
            Log.e("CopyOldData","Called");
            movie_list.MovieList.addAll(movie_list.MovieListOld.values());
        }
       return movie_list.MovieList!=null && movie_list.MovieList.size()==250
                && movie_list.MovieData!=null && movie_list.MovieData.size()>=250
                && movie_list.MovieListOld!=null && movie_list.MovieListOld.size()==250
                 && isDTATASEXIST();


    }
    private boolean isNewVerInstall(int VerCode) {
        SharedPreferences reader = getSharedPreferences("movieinfosh", MODE_PRIVATE);
        int AppCODE = reader.getInt("AppCODE", -1);
        return AppCODE != -1 && VerCode > AppCODE;

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //   StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        // StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.firstac);
        spinKitView = (SpinKitView) findViewById(R.id.loading);
        SharedPreferences reader = getSharedPreferences("movieinfosh", MODE_PRIVATE);
        SharedPreferences.Editor editor = getSharedPreferences("movieinfosh", MODE_PRIVATE).edit();
        if (!reader.getBoolean("isExtracted", false)) {
            ShowForFirstTime();
        } else {
            new LoadData().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


        }
    }

    private void copyPREF() {

        String PREF1 = "META_1";
        String PREF1_r="MOVIE_INFO_AD_PREF";
        String path="/data/data/"+this.getPackageName()+"/shared_prefs/"+PREF1_r+".xml";
        File PREF1File = new File(path);

        String PREF2 = "META_2";
        String PREF2_r="MOVIE_INFO_DATA_PREF";
        String path1="/data/data/"+this.getPackageName()+"/shared_prefs/"+PREF2_r+".xml";
        File PREF2File = new File(path1);

        String PREF3 = "META_3";
        String PREF3_r="MOVIE_INFO_OLD_DATA_PREF";
        String path2="/data/data/"+this.getPackageName()+"/shared_prefs/"+PREF3_r+".xml";
        File PREF3File = new File(path2);


       /* if (!PREF1File.exists())
            PREF1File.mkdir();
        if (!PREF2File.exists())
            PREF2File.mkdir();
        if (!PREF3File.exists())
            PREF3File.mkdir();
            */
        String MainPath="/data/data/"+this.getPackageName()+"/shared_prefs";
        File file=new File(MainPath);
        if(!file.exists())
            file.mkdir();

        if (!PREF1File.exists()) {
            byte[] buffer = new byte[1024];
            OutputStream myOutput = null;
            int length;
            InputStream myInput = null;
            try {
                myInput = getApplicationContext().getAssets().open("Meta_Datas/" + PREF1);
                myOutput = new FileOutputStream(path);
                while ((length = myInput.read(buffer)) > 0) {
                    myOutput.write(buffer, 0, length);
                }
                myOutput.flush();
                myOutput.close();
                myInput.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (!PREF2File.exists()) {
            byte[] buffer = new byte[1024];
            OutputStream myOutput = null;
            int length;
            InputStream myInput = null;
            try {
                myInput = getApplicationContext().getAssets().open("Meta_Datas/" + PREF2);
                myOutput = new FileOutputStream(path1);
                while ((length = myInput.read(buffer)) > 0) {
                    myOutput.write(buffer, 0, length);
                }
                myOutput.flush();
                myOutput.close();
                myInput.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (!PREF3File.exists()) {
            byte[] buffer = new byte[1024];
            OutputStream myOutput = null;
            int length;
            InputStream myInput = null;
            try {
                myInput = getApplicationContext().getAssets().open("Meta_Datas/" + PREF3);
                myOutput = new FileOutputStream(path2);
                while ((length = myInput.read(buffer)) > 0) {
                    myOutput.write(buffer, 0, length);
                }
                myOutput.flush();
                myOutput.close();
                myInput.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    private void DeleteDatabase()
    {
        String path="/data/data/"+this.getPackageName()+"/databases";
        File DB = new File(path);
        try {
            FileUtils.deleteDirectory(DB);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void DeletePREF()
    {
        String PREF1 = "META_1";
        String path="/data/data/"+this.getPackageName()+"/shared_prefs/"+PREF1;
        File PREF1File = new File(path);

        String PREF2 = "META_2";
        String path1="/data/data/"+this.getPackageName()+"/shared_prefs/"+PREF2;
        File PREF2File = new File(path1);

        String PREF3 = "META_2";
        String path2="/data/data/"+this.getPackageName()+"/shared_prefs/"+PREF3;
        File PREF3File = new File(path2);

        if(PREF1File.exists())
            PREF1File.delete();
        if(PREF2File.exists())
            PREF2File.delete();
        if(PREF3File.exists())
            PREF3File.delete();
    }
    private void DeletePics() throws IOException {
        File file=new File(getApplicationContext().getFilesDir().getPath());
        FileUtils.deleteDirectory(file);
    }
    private void copyPICS() throws IOException {
        File file = new File(getApplicationContext().getFilesDir().getPath());
        if (!file.exists())
            file.mkdir();
        byte[] buffer = new byte[1024];
        OutputStream myOutput = null;
        int length;
        InputStream myInput = null;
        try {
            myInput = getApplicationContext().getAssets().open("files/files");
            myOutput = new FileOutputStream(getApplicationContext().getFilesDir().getPath() + "/" + "files.zip");
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }
            myOutput.flush();
            myOutput.close();
            myInput.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
          UNZIP();


        }
    }

    private void UNZIP()
    {

            String zip=getApplicationContext().getFilesDir().getPath()+"/files.zip";
            String loc=getApplicationContext().getFilesDir().getPath()+"/";
            try  {
                FileInputStream fin = new FileInputStream(zip);
                ZipInputStream zin = new ZipInputStream(fin);
                ZipEntry ze = null;
                byte[] buffer = new byte[1024];
                int length;
                while ((ze = zin.getNextEntry()) != null) {
                    File file=new File(loc + ze.getName());
                    if(!file.exists()) {
                        FileOutputStream fout = new FileOutputStream(loc + ze.getName());
                        while ((length = zin.read(buffer)) > 0) {
                            fout.write(buffer, 0, length);
                        }
                        zin.closeEntry();
                        fout.flush();
                        fout.close();
                    }

                }
                zin.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        File delete=new File(getApplicationContext().getFilesDir().getPath()+"/files.zip");
        if(delete.exists())
            delete.delete();

    }


}


