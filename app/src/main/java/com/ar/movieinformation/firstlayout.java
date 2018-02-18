package com.ar.movieinformation;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class firstlayout extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.firstac);
        final SharedPreferences.Editor editor=  getSharedPreferences("movieinfosh",MODE_PRIVATE).edit();
        final SharedPreferences getstatus=getSharedPreferences("movieinfosh",MODE_PRIVATE) ;
        database db = new database(getApplicationContext(), "Movie_db", null, 1);
        SQLiteDatabase temp = db.getReadableDatabase();
        Cursor cursor = temp.query("MOVIEDATA", new String[]{"Movie_Rank", "IMDB_RANK_simple", "Movie_year", "Movie_name", "Movie_farsi_name", "MovieDirector_name", "Actors_Actress", "IMDB_RANK_full", "Movie_IMDB_link", "Movie_picture_link"}, null, null, null, null, null);
        final int count=cursor.getCount();
        editor.putInt("StartupCount", getstatus.getInt("StartupCount", 0) + 1);
        editor.apply();
            if(count!=251 && getstatus.getBoolean("isExtracted", false)) {
                AlertDialog.Builder alert=new AlertDialog.Builder(this);
                final AlertDialog.Builder alert1=new AlertDialog.Builder(this);
                alert.setCancelable(false);
                alert.setTitle("اطلاعات فیلم");
                alert.setMessage("به نظر می رسد به اطلاعات اصلی برنامه آسیب وارد شده است،برای اصلاح آن اطلاعات اولیه کپی می شود. ");
                alert.setPositiveButton("باشه", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DeleteDatabase();
                        copydatabase();
                        editor.putInt("DBupdate_version_Year",2017);
                        editor.putInt("DBupdate_version_Day",305);
                        editor.apply();
                        alert1.setTitle("اطلاعات فیلم");
                        alert1.setMessage("اطلاعات اولیه کپی شد");
                        alert1.setPositiveButton("باشه", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        alert1.setCancelable(true);
                        alert1.show();
                    }
                });
                alert.show();
            }
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
            if(!getstatus.getBoolean("ShowUpdateText",false)) {
                AlertDialog.Builder showUpdateNoti = new AlertDialog.Builder(this);
                showUpdateNoti.setCancelable(false);
                showUpdateNoti.setTitle("اطلاعات فیلم");
                if (getstatus.getBoolean("isExtracted", false)) {
                    showUpdateNoti.setMessage("ویژگی های نسخه جدید:" + "\n" + "1-اضافه شدن نقد فیلم به فیلم هایی که اطلاعات فارسی دارند" + "\n" + "2-اضافه شدن قابلیت دانلود برخی از فیلم ها" + "\n" + "3-اصلاح در نمایش نام برخی از فیلم ها" + "\n"+"4-اضافه شدن بخش مرتب سازی بر اساس آثار کارگردانان"+"\n"+"5-افزایش سرعت بروزرسانی اطلاعات"+"\n"+ "6-تغییرات در گرافیک برنامه و اضافه شدن چندین ویژگی دیگر و رفع برخی از مشکلات" +  "\n\n" + "برای بهره مندی از ویژگی های جدید باید اطلاعات اصلی بروز شود");
                    final String finalVersionsn = versionsn;
                    final int finalVersionsc = versionsc;
                    showUpdateNoti.setPositiveButton("بروز رسانی اطلاعات", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DeleteDatabase();
                            copydatabase();
                            try {
                                DeletePics();
                                copyPICS();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            finally {
                                editor.putBoolean("ShowUpdateText", true);
                                editor.putString("AppVER", finalVersionsn);
                                editor.putInt("AppCODE", finalVersionsc);
                                editor.apply();
                            }
                        }
                    });
                    showUpdateNoti.show();
                } else {
                    showUpdateNoti.setMessage("ویژگی های نسخه جدید:" + "\n" + "1-اضافه شدن نقد فیلم به فیلم هایی که اطلاعات فارسی دارند" + "\n" + "2-اضافه شدن قابلیت دانلود برخی از فیلم ها" + "\n" + "3-اصلاح در نمایش نام برخی از فیلم ها" + "\n"+"4-اضافه شدن بخش مرتب سازی بر اساس آثار کارگردانان"+"\n"+"5-افزایش سرعت بروزرسانی اطلاعات"+"\n"+ "6-تغییرات در گرافیک برنامه و اضافه شدن چندین ویژگی دیگر و رفع برخی از مشکلات" +  "\n\n" + "برای اجرای برنامه اطلاعات اولیه کپی می شود");
                    final String finalVersionsn1 = versionsn;
                    final int finalVersionsc1 = versionsc;
                    showUpdateNoti.setPositiveButton("باشه", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                                copydatabase();
                                DeleteDatabase();
                                copydatabase();
                            try {
                                copyPICS();
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
                        }
                    });
                    showUpdateNoti.show();
                }
            }
    }
    private void copydatabase() {
        String DB_NAME = "Movie_db";
        File file = new File("/data/data/com.arstudio.movieinformation/databases");
        File DB = new File(getApplicationContext().getDatabasePath(DB_NAME).getPath());
        if (!file.exists())
            file.mkdir();
        String DB_PATH = getApplicationContext().getDatabasePath(DB_NAME).getPath();
        if (!DB.exists()) {
            byte[] buffer = new byte[1024];
            OutputStream myOutput = null;
            int length;
            InputStream myInput = null;
            try {
                myInput = getApplicationContext().getAssets().open("databases/" + DB_NAME);
                myOutput = new FileOutputStream(DB_PATH);
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
        String DB_NAME = "Movie_db";
        File DB = new File(getApplicationContext().getDatabasePath(DB_NAME).getPath());
        if(DB.exists())
            DB.delete();
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
            new UNZIP().execute();
        }
    }
    private class UNZIP extends AsyncTask<String,String,String>
    {
        int prosses=0;
        AlertDialog.Builder alert = new AlertDialog.Builder(firstlayout.this);
        ProgressDialog builder= new ProgressDialog(firstlayout.this);
        @Override
        protected void onPreExecute() {
            builder.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            builder.setTitle("لطفا صبر کنید");
            builder.setMessage("در حال کپی کردن اطلاعات...");
            builder.setCancelable(false);
            builder.setMax(275);
            builder.show();
        }

        @Override
        protected  String doInBackground(String... url) {
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
                    prosses++;
                    builder.setProgress(prosses);
                }
                zin.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            builder.dismiss();
            alert.setMessage("اطلاعات اولیه کپی شد");
            alert.setCancelable(true);
            alert.setPositiveButton("باشه", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            alert.show();
            File delete=new File(getApplicationContext().getFilesDir().getPath()+"/files.zip");
            if(delete.exists())
                delete.delete();
        }
    }
    public void enter(View v)
    {
        Intent intent=new Intent(this,movie_list.class);
        startActivity(intent);
        finish();
    }

}


