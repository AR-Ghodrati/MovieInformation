package com.ar.movieinformation;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class Setting extends AppCompatActivity{
    private static final String[] fontnames = new String[] { "main","Far_Naskh",
            "Far_KoodkBd", "Far_EastExtra", "Far_Domrol", "Far_Compset","Far_Bassam","Far_Bardiya"
    ,"Far_BadrBold","Far_Alphabet","Far_AlArabiya","Far_AdvertisingMedium","Far_Elegant","Far_Ekhlass","Far_Abasan","irsans"};
    private static final String[] fontsizes_type = new String[] { "10","13","15","18","20"};
    private static final String[] apptheme_type = new String[] { "مشکی","قهوه ای","روشن"};
    private Typeface myFont;
    Spinner fontsizes;
    Spinner customfonts;
    Spinner apptheme;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_layout);
        fontsizes=(Spinner)findViewById(R.id.fontsize);
        customfonts=(Spinner)findViewById(R.id.selectfonts);
        apptheme=(Spinner)findViewById(R.id.apptheme);
        MyArrayAdapter ma = new MyArrayAdapter(this);
        MyArrayAdapter1 ma1 = new MyArrayAdapter1(this);
        MyArrayAdapter3 ma2 = new MyArrayAdapter3(this);
        SharedPreferences get=getSharedPreferences("movieinfosh",MODE_PRIVATE);
        customfonts.setAdapter(ma);
        for (int i = 0; i < fontnames.length; i++) {
            if(fontnames[i].equals(get.getString("font_type",""))) {
                customfonts.setSelection(i);
                break;
            }
        }
        fontsizes.setAdapter(ma1);
        for (int i = 0; i < fontsizes_type.length; i++) {
            if(fontsizes_type[i].equals(get.getString("font_size",""))) {
                fontsizes.setSelection(i);
                break;
            }
        }
        apptheme.setAdapter(ma2);
        for (int i = 0; i < apptheme_type.length; i++) {
            if(apptheme_type[i].equals(get.getString("font_size",""))) {
                apptheme.setSelection(i);
                break;
            }
        }
    }
    private class MyArrayAdapter extends BaseAdapter {

        private LayoutInflater mInflater;

        MyArrayAdapter(Activity con) {
            // TODO Auto-generated constructor stub
            mInflater = LayoutInflater.from(con);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return fontnames.length;
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            SharedPreferences.Editor editor=getSharedPreferences("movieinfosh",MODE_PRIVATE).edit();
            // TODO Auto-generated method stub
            final ListContent holder;
            View v = convertView;
            myFont = Typeface.createFromAsset(getAssets(), "fonts/"+fontnames[position]+".ttf");
            if (v == null) {
                v = mInflater.inflate(R.layout.my_spinner_style, null);
                holder = new ListContent();
                holder.name = (TextView) v.findViewById(R.id.textView1);
                v.setTag(holder);
            } else {
                holder = (ListContent) v.getTag();
            }
            holder.name.setTypeface(myFont);
            holder.name.setText("" + fontnames[position]);
            editor.putString("font_type",fontnames[position]);
            editor.apply();
            holder.name.setGravity(Gravity.CENTER);
            return v;
        }

    }
    private class MyArrayAdapter1 extends BaseAdapter {

        private LayoutInflater mInflater;

        MyArrayAdapter1(Activity con) {
            // TODO Auto-generated constructor stub
            mInflater = LayoutInflater.from(con);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return fontsizes_type.length;
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            SharedPreferences.Editor editor=getSharedPreferences("movieinfosh",MODE_PRIVATE).edit();
            float size = (int) Double.parseDouble(fontsizes_type[position]);
            final ListContent holder;
            View v = convertView;
            if (v == null) {
                v = mInflater.inflate(R.layout.my_spinner_style, null);
                holder = new ListContent();
                holder.name = (TextView) v.findViewById(R.id.textView1);
                v.setTag(holder);
            } else {
                holder = (ListContent) v.getTag();
            }
            holder.name.setTextSize(size);
            holder.name.setText("" + fontsizes_type[position]);
            editor.putString("font_size",fontsizes_type[position]);
            editor.apply();
            holder.name.setGravity(Gravity.CENTER);
            return v;
        }
    }
    private static class ListContent {

        TextView name;

    }
    private class MyArrayAdapter3 extends BaseAdapter {

        private LayoutInflater mInflater;

        MyArrayAdapter3(Activity con) {
            // TODO Auto-generated constructor stub
            mInflater = LayoutInflater.from(con);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return apptheme_type.length;
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            SharedPreferences.Editor editor = getSharedPreferences("movieinfosh", MODE_PRIVATE).edit();
            // TODO Auto-generated method stub
            final ListContent holder;
            View v = convertView;
            if (v == null) {
                v = mInflater.inflate(R.layout.my_spinner_style, null);
                holder = new ListContent();
                holder.name = (TextView) v.findViewById(R.id.textView1);
                v.setTag(holder);
            } else {
                holder = (ListContent) v.getTag();
            }
            holder.name.setText("" + apptheme_type[position]);
            editor.putString("apptheme_type", apptheme_type[position]);
            editor.apply();
            holder.name.setGravity(Gravity.CENTER);
            return v;
        }
    }
}


