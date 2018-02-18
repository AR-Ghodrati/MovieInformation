package com.ar.movieinformation;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * Created by alireza on 20/12/2017.
 */

public class custom_reycler_view extends RecyclerView.Adapter<custom_reycler_view.MyViewHolder>{
    private Context context;
    private int COUNT=250;
    private final String[] movienameEN;
    private final String[] movienameFA;
    private final String[] ENpics;
    private boolean state=false;
    private String[]yearss;
    private int mod;
    private boolean []check;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView year,MovieENTitle,MovieFATitle;
        ImageView imageView;
        View v;
        public MyViewHolder(View view) {
            super(view);
             MovieENTitle = (TextView) view.findViewById(R.id.firstLine);
             MovieFATitle = (TextView) view.findViewById(R.id.secondLine);
             v=view.findViewById(R.id.viewyear);
             imageView = (ImageView) view.findViewById(R.id.icon);
             year = (TextView) view.findViewById(R.id.yearsort);
        }
    }
    public custom_reycler_view(
                           String[] movienameENs,String[] movienameFAs,String[]pics,String[]years,boolean[]chek,int model) {
        this.movienameEN = movienameENs;
        this.movienameFA=movienameFAs;
        this.ENpics=pics;
        this.mod=model;
        this.yearss=years;
        this.check=chek;

    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movielist_recycler, parent, false);
        this.context=parent.getContext();
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.MovieENTitle.setText(movienameEN[position]);
        holder.MovieFATitle.setText(movienameFA[position]);
        File file = null;
        if (ENpics[position] != null) {
            file = new File(context.getFilesDir().getPath() + "/" + ENpics[position].trim());
        }
        if(file!=null) {
            if (file.exists()) {
                //Bitmap bmp = BitmapFactory.decodeFile(context.getFilesDir().getPath() + "/" + ENpics[position].trim());
                //holder. imageView.setImageBitmap(Bitmap.createScaledBitmap(bmp, 120, 170, false));
                Picasso.with(context).load(file).into(holder.imageView);
            } else {
                SQLiteDatabase temp;
                database movieDB = new database(context, "Movie_db", null, 1);
                temp = movieDB.getReadableDatabase();
                Cursor cursor = temp.query("MOVIEFARSIDATA2", new String[]{"Movie_ENname", "Movie_time", "Movie_Style", "Movie_Award", "MovieStory", "MovieQUPIC", "Movie_Naghed", "Download_movie_link_720p", "Download_movie_link_1080p", "Download_movie_subtitle_link", "isdubled"}, null, null, null, null, null);
                while (cursor.moveToNext()) {
                    if (cursor.getString(cursor.getColumnIndex("Movie_ENname")).lastIndexOf(0) == ' ') {
                        ENpics[position] = cursor.getString(cursor.getColumnIndex("Movie_ENname")).trim().substring(0, cursor.getString(cursor.getColumnIndex("Movie_ENname")).length() - 1);
                    } else {
                        String name = cursor.getString(cursor.getColumnIndex("Movie_ENname")).trim();
                        if (ENpics[position] != null && ENpics[position].equalsIgnoreCase(name)) {
                            ENpics[position] = name;
                            break;
                        }
                    }
                }
                if (ENpics[position] != null) {
                    File file1 = new File(context.getFilesDir().getPath() + "/" + ENpics[position].trim());
                    if (file1.exists()) {
                       // Bitmap bmp = BitmapFactory.decodeFile(context.getFilesDir().getPath() + "/" + ENpics[position].trim());
                       // holder. imageView.setImageBitmap(Bitmap.createScaledBitmap(bmp, 120, 170, false));
                        Picasso.with(context).load(file1).into(holder.imageView);
                    }
                }
                holder.  v.setVisibility(View.INVISIBLE);
                holder.year.setVisibility(View.INVISIBLE);
            }
        }
        if(mod==1)
            if(check[position]) {
                holder.v.setVisibility(View.VISIBLE);
                holder.year.setVisibility(View.VISIBLE);
                holder. year.setText(yearss[position]);
            }
            else {
                holder.v.setVisibility(View.INVISIBLE);
                holder.year.setVisibility(View.INVISIBLE);
            }
    }

    @Override
    public int getItemCount() {
        return COUNT;
    }
    public void setcount(int count){
        COUNT=count;
    }

}
