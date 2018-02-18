package com.ar.movieinformation;

/**
 * Created by alireza on 29/08/2017.
 */

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class FiveFragment extends Fragment {
    getset handle=new getset();
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.INTERNET},1);
        final int W_sd= ContextCompat.checkSelfPermission(getContext(),Manifest.permission.WRITE_EXTERNAL_STORAGE);
        final int R_sd= ContextCompat.checkSelfPermission(getContext(),Manifest.permission.READ_EXTERNAL_STORAGE);
        final int internet=ContextCompat.checkSelfPermission(getContext(),Manifest.permission.INTERNET);
        final String hd_string=getArguments().getString("download_hd_link");
        String fhd_string=getArguments().getString("download_fhd_link");
        String subtitle_string=getArguments().getString("download_subtitle_link");
        boolean Isdoubled=getArguments().getBoolean("Isdobled");
        if(hd_string ==null && fhd_string==null) {
            View v = inflater.inflate(R.layout.notfound_layout, container, false);
            TextView naghed_text = (TextView) v.findViewById(R.id.notfound_text);
            naghed_text.setText("لینک های دانلود فیلم یافت نشد");
            return v;
        }
        else
        {
            View v = inflater.inflate(R.layout.download_link_page, container, false);
            Button hd=(Button)v.findViewById(R.id.Download_hd_link);
            Button fhd=(Button)v.findViewById(R.id.Download_fhd_link);
            Button subtitle=(Button)v.findViewById(R.id.Download_subtitle_link);
            ImageView hd_notfound=(ImageView)v.findViewById(R.id.notfound_hd);
            ImageView fhd_notfound=(ImageView)v.findViewById(R.id.notfound_fhd);
            ImageView subtitle_notfound=(ImageView)v.findViewById(R.id.notfound_subtitle);
            TextView hd_notfound_txt=(TextView)v.findViewById(R.id.notfound_hd_text);
            TextView fhd_notfound_txt=(TextView)v.findViewById(R.id.notfound_fhd_text);
            TextView subtitle_notfound_txt=(TextView)v.findViewById(R.id.notfound_subtitle_text);
            if(fhd_string !=null && fhd_string.length()<2 ) {
                fhd.setVisibility(View.INVISIBLE);
                fhd_notfound_txt.setVisibility(View.VISIBLE);
                fhd_notfound.setVisibility(View.VISIBLE);
            }
            if(subtitle_string!=null &&subtitle_string.length()<2) {
                subtitle.setVisibility(View.INVISIBLE);
                subtitle_notfound.setVisibility(View.VISIBLE);
                subtitle_notfound_txt.setVisibility(View.VISIBLE);
                if (Isdoubled) {
                    subtitle_notfound.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.about));
                    subtitle_notfound_txt.setText("فیلم دوبله شده است ");
                    subtitle_notfound_txt.setTextSize(15f);
                }
            }
            if(hd_string!=null && hd_string.length()<2) {
                hd.setVisibility(View.INVISIBLE);
                hd_notfound.setVisibility(View.VISIBLE);
                hd_notfound_txt.setVisibility(View.VISIBLE);
            }
            final AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
            builder.setCancelable(true);
            hd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkconectivity()) {
                        if (W_sd == PackageManager.PERMISSION_DENIED && R_sd == PackageManager.PERMISSION_DENIED) {
                            AlertDialog.Builder a=new AlertDialog.Builder(getContext());
                            a.setTitle("اطلاعات فیلم");
                            a.setCancelable(false);
                            a.setMessage("برای دانلود فیلم،برنامه نیاز به دسترسی به خواندن حافظه و اینترنت دارد");
                            a.setPositiveButton(" دسترسی دادن به برنامه", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.INTERNET},1);
                                }
                            });
                            a.show();
                        }
                        final Intent intent = new Intent(Intent.ACTION_VIEW);
                        final String[] textSize = {""};
                            new getSize().execute(hd_string);
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                int size=handle.getdata();
                                String textsize="";
                                if(size/1000000>1) {
                                    textsize = "مگابایت";
                                    size=size/1000000;
                                }
                                else if(size/1000>1) {
                                    textsize = "کیلو بایت";
                                    size=size/1000;
                                }
                                if(size<0)
                                    textSize[0] =  "حجم فیلم با این کیفیت: بیشتر از 2 گیگابایت";
                                else if(size==0)
                                    textSize[0] ="";
                                else
                                    textSize[0] =  "حجم فیلم با این کیفیت: "+size+textsize;
                                String source="";
                                assert hd_string != null;
                                if(hd_string.contains("avadl.uploadet.ir"))
                                     source="مرجع دانلود فیلم : آوا دانلود";
                                else if(hd_string.contains("sv4avadl.uploadet.ir"))
                                    source="مرجع دانلود فیلم : آوا دانلود";
                                 else if(hd_string.contains("ariamovie"))
                                    source="مرجع دانلود فیلم : آریا مووی";
                                else if(hd_string.contains("dubfa"))
                                    source="مرجع دانلود فیلم : دوبفا";
                                else if(hd_string.contains("film2movie.co"))
                                    source="مرجع دانلود فیلم : فیلم تو مووی";
                                else if(hd_string.contains("film2movie.co"))
                                    source="مرجع دانلود فیلم : فیلم تو مووی";
                                builder.setMessage("آیا مایل به دانلود این فیلم با کیفیت اچ دی هستید؟\n" + textSize[0]+"\n\n"+source);
                                intent.setData(Uri.parse(hd_string));
                                builder.setTitle("دانلود فیلم");
                                builder.setPositiveButton("بله", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        startActivity(intent);
                                    }
                                });
                                builder.setNegativeButton("خیر", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                });
                                builder.show();
                                handle.Resetdata();
                            }
                        }, 2000);
                    }
                    else
                        Toast.makeText(getContext(), "لطفا برای دریافت اطلاعات به اینترنت متصل شوید", Toast.LENGTH_LONG).show();
                }
            });
            final String finalFhd_string = fhd_string;
            fhd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkconectivity()) {
                        if (W_sd == PackageManager.PERMISSION_DENIED && R_sd == PackageManager.PERMISSION_DENIED) {
                            AlertDialog.Builder a=new AlertDialog.Builder(getContext());
                            a.setTitle("اطلاعات فیلم");
                            a.setCancelable(false);
                            a.setMessage("برای دانلود فیلم،برنامه نیاز به دسترسی به خواندن حافظه و اینترنت دارد");
                            a.setPositiveButton(" دسترسی دادن به برنامه", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.INTERNET},1);
                                }
                            });
                            a.show();
                        }
                        final Intent intent = new Intent(Intent.ACTION_VIEW);
                        final String[] textSize = {""};
                        new getSize().execute(finalFhd_string);
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                int size=handle.getdata();
                                String textsize="";
                                if(size/1000000>1) {
                                    textsize = "مگابایت";
                                    size=size/1000000;
                                }
                                else if(size/1000>1) {
                                    textsize = "کیلو بایت";
                                    size=size/1000;
                                }
                                if(size<0)
                                textSize[0] =  "حجم فیلم با این کیفیت: بیشتر از 2 گیگابایت";
                                else if(size==0)
                                    textSize[0] =  "";
                                else
                                    textSize[0] =  "حجم فیلم با این کیفیت: "+size+textsize;
                                String source="";
                                assert hd_string != null;
                                if(hd_string.contains("avadl.uploadet.ir"))
                                    source="مرجع دانلود فیلم : آوا دانلود";
                                else if(hd_string.contains("sv4avadl.uploadet.ir"))
                                    source="مرجع دانلود فیلم : آوا دانلود";
                                else if(hd_string.contains("ariamovie"))
                                    source="مرجع دانلود فیلم : آریا مووی";
                                else if(hd_string.contains("dubfa"))
                                    source="مرجع دانلود فیلم : دوبفا";
                                else if(hd_string.contains("film2movie.co"))
                                    source="مرجع دانلود فیلم : فیلم تو مووی";
                                else if(hd_string.contains("film2movie.co"))
                                    source="مرجع دانلود فیلم : فیلم تو مووی";
                                builder.setMessage("آیا مایل به دانلود این فیلم با کیفیت اچ دی هستید؟\n" + textSize[0]+"\n\n"+source);
                                intent.setData(Uri.parse(finalFhd_string));
                                builder.setTitle("دانلود فیلم");
                                builder.setPositiveButton("بله", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        startActivity(intent);
                                    }
                                });
                                builder.setNegativeButton("خیر", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                });
                                builder.show();
                                handle.Resetdata();
                            }
                        }, 2000);

                    }
                    else Toast.makeText(getContext(), "لطفا برای دریافت اطلاعات به اینترنت متصل شوید", Toast.LENGTH_LONG).show();
                }

            });
            final String finalSubtitle_string = subtitle_string;
            subtitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkconectivity()) {
                        if (W_sd == PackageManager.PERMISSION_DENIED && R_sd == PackageManager.PERMISSION_DENIED) {
                            AlertDialog.Builder a=new AlertDialog.Builder(getContext());
                            a.setTitle("اطلاعات فیلم");
                            a.setCancelable(false);
                            a.setMessage("برای دانلود فیلم،برنامه نیاز به دسترسی به خواندن حافظه و اینترنت دارد");
                            a.setPositiveButton(" دسترسی دادن به برنامه", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.INTERNET},1);
                                }
                            });
                            a.show();
                        }
                        final Intent intent = new Intent(Intent.ACTION_VIEW);
                        new getSize().execute(finalSubtitle_string);
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                int size = handle.getdata();
                                if (size < 1000) {
                                    builder.setMessage("برای دانلود زیر نویس سایت مرجع زیرنویس باز می شود،از داخل آن زیرنویس را انتخاب و دانلود کنید.\n"+"آیا مایل به باز کردن سایت هستید؟");
                                    intent.setData(Uri.parse(finalSubtitle_string));
                                    builder.setTitle("دانلود زیرنویس فیلم");
                                    builder.setPositiveButton("بله", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            startActivity(intent);
                                        }
                                    });
                                    builder.setNegativeButton("خیر", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    });
                                    builder.show();
                                    handle.Resetdata();
                                } else {
                                    String textsize = "";
                                    String textSize = "";
                                    if (size / 1000000 > 1) {
                                        textsize = "مگابایت";
                                        size = size / 1000000;
                                    } else if (size / 1000 > 1) {
                                        textsize = "کیلو بایت";
                                        size = size / 1000;
                                    }
                                     if(size==0)
                                        textSize = "";
                                    else
                                     textSize = "حجم زیرنویس این فیلم: " + size + textsize;
                                    builder.setMessage("آیا مایل به دانلود زیر نویس این فیلم  هستید؟\n\n" + textSize);
                                    intent.setData(Uri.parse(finalSubtitle_string));
                                    builder.setTitle("دانلود زیرنویس فیلم");
                                    builder.setPositiveButton("بله", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            startActivity(intent);
                                        }
                                    });
                                    builder.setNegativeButton("خیر", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    });
                                    builder.show();
                                    handle.Resetdata();
                                }
                            }
                        }, 2000);
                    }
                    else  Toast.makeText(getContext(), "لطفا برای دریافت اطلاعات به اینترنت متصل شوید", Toast.LENGTH_LONG).show();
                }
            });
            return v;

        }
    }

    public static FiveFragment setdataandshow(String download_hd_link,String download_fhd_link,String download_subtitle_link,boolean Isdobled ) {

        FiveFragment f = new FiveFragment();
        Bundle b = new Bundle();
        b.putString("download_hd_link",download_hd_link);
        b.putString("download_fhd_link",download_fhd_link);
        b.putString("download_subtitle_link",download_subtitle_link);
        b.putBoolean("Isdobled",Isdobled);
        f.setArguments(b);
        return f;
    }
    private boolean checkconectivity()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
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
    private class getSize extends AsyncTask<String,String,String>
    {
        int size=0;
        @Override
        protected String doInBackground(String... params) {
            URL url = null;
            try {
                url = new URL(params[0]);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            HttpURLConnection urlConnection = null;
            try {
                assert url != null;
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();
                size = urlConnection.getContentLength();
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                assert urlConnection != null;
                urlConnection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            handle.setdata(size);
        }
    }
    private class getset
    {
        private int data=0;
        void setdata(int d){this.data=d;}
        int getdata(){return this.data;}
        void Resetdata(){this.data=0;}
    }
    /*
    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        SharedPreferences getshared= getActivity().getSharedPreferences("movieinfosh", Context.MODE_PRIVATE);
        float size=Integer.parseInt(getshared.getString("font_size","18"));
        FontChangeCrawler fontChanger = new FontChangeCrawler(getActivity().getAssets(), "fonts/"+getshared.getString("font_type","SERIF")+".ttf",size);        fontChanger.replaceFonts((ViewGroup) this.getView());
    }
    */
}
