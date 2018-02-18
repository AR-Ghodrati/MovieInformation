package com.ar.movieinformation;

/**
 * Created by alireza on 02/09/2017.
 */

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

public class moviecustomlist_change extends RecyclerView.Adapter<moviecustomlist_change.MyViewHolder> {

    private Context context;
    private int COUNT=250;
    private final String[] movienameEN;
    private final String[] movienameFA;
    private final String[] ENpics;
    private  String[] rank;
    private  String[] amtiaz;
    private  String[] changerank;
    private  String[] changeamtiaz;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView rankshow,rankchange,amtiazshow,amtiazchange,MovieENTitle,MovieFATitle;
        ImageView changeranki,changeamtiazi,imageView;
        public MyViewHolder(View view) {
            super(view);
            MovieENTitle = (TextView) view.findViewById(R.id.firstLinechange);
             MovieFATitle = (TextView) view.findViewById(R.id.secondLinechange);
             imageView = (ImageView) view.findViewById(R.id.iconchange);
            rankshow=(TextView)view.findViewById(R.id.rankshow);
            rankchange=(TextView)view.findViewById(R.id.rankghange);
            amtiazchange=(TextView)view.findViewById(R.id.amtiazchange);
            amtiazshow=(TextView)view.findViewById(R.id.amtiazshow);
            changeranki=(ImageView) view.findViewById(R.id.rankchangeicon);
            changeamtiazi=(ImageView) view.findViewById(R.id.amtiazchangeicon);
        }
    }
    public moviecustomlist_change(
                                  String[] movienameENs, String[] movienameFAs, String[]pics, String[]ranks, String[]amtiaz, String[] changerank,String[]changeamtiaz) {
        this.movienameEN = movienameENs;
        this.movienameFA=movienameFAs;
        this.ENpics=pics;
        this.rank=ranks;
        this.amtiaz=amtiaz;
        this.changerank=changerank;
        this.changeamtiaz=changeamtiaz;
    }
    public void setcount(int count){
        COUNT=count;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.change_layout, parent, false);
        this.context=parent.getContext();
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.MovieENTitle.setText(movienameEN[position]);
        holder.MovieFATitle.setText(movienameFA[position]);
        holder.rankshow.setText(rank[position]);
        holder. rankchange.setText(changerank[position]);
        holder. amtiazshow.setText(amtiaz[position]);
        if(changerank[position]!=null&&changeamtiaz[position]!=null) {
            if (Double.parseDouble(changerank[position]) > 0) {
               // holder.changeranki.setImageResource(R.drawable.up);
                Picasso.with(context).load(R.drawable.up).into( holder.changeranki);
                holder.rankchange.setText("+" + changerank[position]);
            } else if (Double.parseDouble(changerank[position]) < 0)
               // holder.changeranki.setImageResource(R.drawable.down);
                Picasso.with(context).load(R.drawable.down).into( holder.changeranki);

            else {
                //holder.changeranki.setImageResource(R.drawable.equals);
                Picasso.with(context).load(R.drawable.equals).into( holder.changeranki);
                holder.rankchange.setText("بدون تغییر");
            }
            if (Double.parseDouble(changeamtiaz[position]) > 0) {
                //holder.changeamtiazi.setImageResource(R.drawable.up);
                Picasso.with(context).load(R.drawable.up).into( holder.changeamtiazi);
                holder.amtiazchange.setText("+"+String.format( "%.1f", Double.parseDouble(changeamtiaz[position])));
            }
            else if (Double.parseDouble(changeamtiaz[position]) < 0) {
                //holder.changeamtiazi.setImageResource(R.drawable.down);
                Picasso.with(context).load(R.drawable.down).into( holder.changeamtiazi);
                holder. amtiazchange.setText(String.format( "%.1f", Double.parseDouble(changeamtiaz[position])));
            }
            else {
               // holder.changeamtiazi.setImageResource(R.drawable.equals);
                Picasso.with(context).load(R.drawable.equals).into( holder.changeamtiazi);
                holder.amtiazchange.setText("بدون تغییر");
            }
        }
        else
        {
            holder.rankchange.setText( " جدید ");
            holder.amtiazchange.setText(" جدید ");
            holder. changeranki.setImageResource(R.drawable.newicon);
            holder.changeamtiazi.setImageResource(R.drawable.newicon);
        }
        File file = null;
        if(ENpics[position]==null)
            Picasso.with(context).load(R.drawable.icon).into(holder.imageView);
        if (ENpics[position] != null) {
            file = new File(context.getFilesDir().getPath() + "/" + ENpics[position].trim());
        }
        assert file != null;
        if (file.exists()) {
            Picasso.with(context).load(file).into(holder.imageView);
            //Bitmap bmp = BitmapFactory.decodeFile(context.getFilesDir().getPath() + "/" + ENpics[position].trim());
           // holder.imageView.setImageBitmap(Bitmap.createScaledBitmap(bmp, 110, 160, false));
        } else {
            SQLiteDatabase temp;
            database movieDB = new database(context, "Movie_db", null, 1);
            temp = movieDB.getReadableDatabase();
            Cursor cursor = temp.query("MOVIEFARSIDATA2", new String[]{"Movie_ENname", "Movie_time", "Movie_Style", "Movie_Award","MovieStory", "MovieQUPIC","Movie_Naghed","Download_movie_link_720p","Download_movie_link_1080p","Download_movie_subtitle_link","isdubled"}, null, null, null, null, null);
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
                    Picasso.with(context).load(file1).into(holder.imageView);
                    // Bitmap bmp = BitmapFactory.decodeFile(context.getFilesDir().getPath() + "/" + ENpics[position].trim());
                   // holder. imageView.setImageBitmap(Bitmap.createScaledBitmap(bmp, 110, 160, false));
                }
            }
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return  COUNT;
    }

}