package com.ar.movieinformation;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;

public class aboutus_myket extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_myket);
    }
   public void report(View view)
   {
       String versionsn="";
       String versionsc="";
       PackageManager manager = getApplicationContext().getPackageManager();
       PackageInfo info = null;
       try {
           info = manager.getPackageInfo(
                   getApplicationContext().getPackageName(), 0);
            versionsn = info.versionName;
            versionsc=""+info.versionCode;
       } catch (PackageManager.NameNotFoundException e) {
           e.printStackTrace();
       }
       finally {
           String deviceName = android.os.Build.MODEL;
           String deviceMan = android.os.Build.MANUFACTURER;
           String androidlevel=Build.VERSION.RELEASE;
           Intent intent = new Intent(Intent.ACTION_SEND);
           DisplayMetrics displayMetrics = new DisplayMetrics();
           getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
           int height = displayMetrics.heightPixels;
           int width = displayMetrics.widthPixels;
           String deviceResolotion= height +" * "+width;
           intent.setType("text/plain");
           intent.putExtra(android.content.Intent.EXTRA_EMAIL,new String[] { "apps.arstudio.development@gmail.com" });
           intent.putExtra(Intent.EXTRA_SUBJECT, "Bug report");
           intent.putExtra(Intent.EXTRA_TEXT, "ابتدا نوع مشکل برنامه را نوشته سپس نام صفحه ای که برنامه در آن به مشکل برخورده است بنویسید و به سایر موارد دست نزنید"+"\n\n\n"+"VersionName : "+versionsn+"\n\n"+"VersionCode: "+versionsc+"\n\n"+"DeviceName: "+deviceName+"\n\n"+"DeviceMan: "+deviceMan+"\n\n"+"DeviceScreenResolation: "+deviceResolotion+"\n\n"+"DeviceAndroidLevel: "+androidlevel);
           startActivity(Intent.createChooser(intent, "Send Email"));
       }
   }
}


