package com.ar.movieinformation;

/**
 * Created by alireza on 02/09/2017.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

public class moviecustomlist_change extends RecyclerView.Adapter<moviecustomlist_change.MyViewHolder> {

    private Context context;
    private  String[] changerank;
    private  String[] changeamtiaz;
    private List<Movie> movie;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView rankshow,rankchange,amtiazshow,amtiazchange,MovieENTitle,MovieFATitle;
        ImageView changeranki,changeamtiazi,imageView,translatedpic;
        public MyViewHolder(View view) {
            super(view);
            MovieENTitle = (TextView) view.findViewById(R.id.firstLinechange);
            MovieENTitle.setSelected(true);
             MovieFATitle = (TextView) view.findViewById(R.id.secondLinechange);
            MovieFATitle.setSelected(true);
             imageView = (ImageView) view.findViewById(R.id.iconchange);
            rankshow=(TextView)view.findViewById(R.id.rankshow);
            rankchange=(TextView)view.findViewById(R.id.rankghange);
            amtiazchange=(TextView)view.findViewById(R.id.amtiazchange);
            amtiazshow=(TextView)view.findViewById(R.id.amtiazshow);
            changeranki=(ImageView) view.findViewById(R.id.rankchangeicon);
            changeamtiazi=(ImageView) view.findViewById(R.id.amtiazchangeicon);
            translatedpic=(ImageView) view.findViewById(R.id.translatedpic);
        }
    }
    public moviecustomlist_change(
                                List<Movie>  movie, String[] changerank,String[]changeamtiaz) {
        this.movie=movie;
        this.changerank=changerank;
        this.changeamtiaz=changeamtiaz;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.change_layout, parent, false);
        this.context=parent.getContext();

        return new MyViewHolder(itemView);
    }

    @Override
    public void onViewRecycled(MyViewHolder holder) {
        super.onViewRecycled(holder);
        holder.translatedpic.setVisibility(View.GONE);
        Picasso.with(context)
                .load(R.drawable.icon)
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                //.error(R.drawable.icon)
                .fit()
                .centerCrop()
                //.resize(240,300)
                .transform(new RoundedTransformation(20,2))
                .into(holder.imageView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.MovieENTitle.setText(movie.get(position).getTitle());
        holder.MovieFATitle.setText(movie.get(position).getTitleFa());
        holder.rankshow.setText(movie.get(position).getTop_250_Rank());
        holder. rankchange.setText(changerank[position]);
        holder. amtiazshow.setText(movie.get(position).getImdbRating());
        if(changerank[position]!=null&&changeamtiaz[position]!=null) {
            if (Double.parseDouble(changerank[position]) > 0) {
                holder.changeranki.setImageResource(R.drawable.up);
               // Picasso.with(context).load(R.drawable.up).memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                     //   .into( holder.changeranki);
                holder.rankchange.setText("+" + changerank[position]);
            } else if (Double.parseDouble(changerank[position]) < 0)
                holder.changeranki.setImageResource(R.drawable.down);
              //  Picasso.with(context).load(R.drawable.down).memoryPolicy(MemoryPolicy.NO_CACHE).into( holder.changeranki);

            else {
                holder.changeranki.setImageResource(R.drawable.equals);
                //Picasso.with(context).load(R.drawable.equals).memoryPolicy(MemoryPolicy.NO_CACHE).into( holder.changeranki);
                holder.rankchange.setText("بدون تغییر");
            }
            if (Double.parseDouble(changeamtiaz[position]) > 0) {
                holder.changeamtiazi.setImageResource(R.drawable.up);
               // Picasso.with(context).load(R.drawable.up).memoryPolicy(MemoryPolicy.NO_CACHE).into( holder.changeamtiazi);
                holder.amtiazchange.setText("+"+String.format( "%.1f", Double.parseDouble(changeamtiaz[position])));
            }
            else if (Double.parseDouble(changeamtiaz[position]) < 0) {
                holder.changeamtiazi.setImageResource(R.drawable.down);
               // Picasso.with(context).load(R.drawable.down).memoryPolicy(MemoryPolicy.NO_CACHE).into( holder.changeamtiazi);
                holder. amtiazchange.setText(String.format( "%.1f", Double.parseDouble(changeamtiaz[position])));
            }
            else {
                holder.changeamtiazi.setImageResource(R.drawable.equals);
                //Picasso.with(context).load(R.drawable.equals).memoryPolicy(MemoryPolicy.NO_CACHE).into( holder.changeamtiazi);
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
        if(movie.get(position).getTitle()==null)
            Picasso.with(context).load(R.drawable.icon).memoryPolicy(MemoryPolicy.NO_CACHE).into(holder.imageView);
        if (movie.get(position).getTitle() != null) {
            file = new File(context.getFilesDir().getPath() + "/" + movie.get(position).getTitle().trim());
        }
        assert file != null;
        if (file.exists()) {
            Picasso.with(context)
                    .load(file)
                    .error(R.drawable.icon)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .transform(new RoundedTransformation(45,5))
                    .into(holder.imageView);
            //Bitmap bmp = BitmapFactory.decodeFile(context.getFilesDir().getPath() + "/" + ENpics[position].trim());
           // holder.imageView.setImageBitmap(Bitmap.createScaledBitmap(bmp, 110, 160, false));
        } else {
        }
        if(movie.get(position).isTranslated())
            holder.translatedpic.setVisibility(View.VISIBLE);
        else holder.translatedpic.setVisibility(View.GONE);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return  movie.size();
    }

}