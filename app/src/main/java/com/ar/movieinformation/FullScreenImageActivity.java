package com.ar.movieinformation;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by alireza on 03/11/2017.
 */

public class FullScreenImageActivity extends Activity {
    ImageView imageView ;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_full_screen_image);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        imageView=(ImageView) findViewById(R.id.imgFullScreen);
        Bitmap bitmap = BitmapFactory.decodeFile(getIntent().getExtras().getString("BitmapImage"));
        if(bitmap!=null) {

            imageView.setImageBitmap(bitmap);
            PhotoViewAttacher pAttacher;
            pAttacher = new PhotoViewAttacher(imageView);
            pAttacher.setZoomable(true);
            pAttacher.setZoomTransitionDuration(1000);
            pAttacher.update();


        }
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void SaveIMG(View v) {
        int W_sd = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int R_sd = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (W_sd != PackageManager.PERMISSION_GRANTED && R_sd != PackageManager.PERMISSION_GRANTED) {
            AlertDialog.Builder a = new AlertDialog.Builder(this);
            a.setTitle("اطلاعات فیلم");
            a.setCancelable(false);
            a.setMessage("برای ذخیره پوستر فیلم،برنامه نیاز به دسترسی به خواندن حافظه دارد");
            a.setPositiveButton(" دسترسی دادن به برنامه", new DialogInterface.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(FullScreenImageActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                }
            });
            a.show();
        } else {
            imageView.setDrawingCacheEnabled(true);
            Bitmap b = imageView.getDrawingCache();
            Bitmap bmp = null;
            String title = getIntent().getExtras().getString("name");
            File source = new File(getApplicationContext().getFilesDir().getPath() + "/" + title);
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath() + "/Arstudio");
            String output = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/Arstudio" + "/" + title + ".jpg";
            File out = new File(output);
            if (!file.exists())
                file.mkdir();
            if (file.isDirectory()) {
                if (!out.exists()) {
                    if (source.exists()) {
                        assert title != null;
                        bmp = BitmapFactory.decodeFile(getApplicationContext().getFilesDir().getPath() + "/" + title.trim());
                    }
                    File saveImage = new File(output);
                    try {
                        OutputStream outputStream = new FileOutputStream(saveImage);
                        if (bmp != null)
                            bmp.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                        else b.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                        outputStream.flush();
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        Toast.makeText(getApplicationContext(), "پوستر با موفقیت در پوشه ی " + "حافظه داخلی" + "/DCIM" + "/Arstudio" + " ذخیره شد ", Toast.LENGTH_LONG).show();
                    }
                } else
                    Toast.makeText(getApplicationContext(), "پوستر قبلا ذخیره شده است", Toast.LENGTH_LONG).show();
            }
        }
    }
}
