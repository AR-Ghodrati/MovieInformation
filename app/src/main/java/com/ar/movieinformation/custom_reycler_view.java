package com.ar.movieinformation;

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

/**
 * Created by alireza on 20/12/2017.
 */

public class custom_reycler_view extends RecyclerView.Adapter<custom_reycler_view.MyViewHolder>{
    private Context context;
    private List<Movie> movie;
    private int mod;
    private boolean []check;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView year,MovieENTitle,MovieFATitle;
        ImageView imageView,IsTranlated;
        View v;
        public MyViewHolder(View view) {
            super(view);
             MovieENTitle = (TextView) view.findViewById(R.id.firstLine);
            MovieENTitle.setSelected(true);
             MovieFATitle = (TextView) view.findViewById(R.id.secondLine);
            MovieFATitle.setSelected(true);
             v=view.findViewById(R.id.viewyear);
             imageView = (ImageView) view.findViewById(R.id.icon);
             year = (TextView) view.findViewById(R.id.yearsort);
            IsTranlated= (ImageView) view.findViewById(R.id.IsTranlated);
        }
    }
    public custom_reycler_view(
            List<Movie> movies,boolean[]chek,int model) {

        this.mod=model;
        this.check=chek;
        this.movie=movies;

    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movielist_recycler, parent, false);
        this.context=parent.getContext();
        return new MyViewHolder(itemView);
    }

    @Override
    public void onViewRecycled(MyViewHolder holder) {
        super.onViewRecycled(holder);
        holder.IsTranlated.setVisibility(View.GONE);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.MovieENTitle.setText(movie.get(position).getTitle());
        holder.MovieFATitle.setText(movie.get(position).getTitleFa());
        File file = null;
        if ( movie.get(position).getTitle() != null) {
            file = new File(context.getFilesDir().getPath() + "/" + movie.get(position).getTitle().trim());
        }
        if(file!=null) {
            if (file.exists()) {
                //Bitmap bmp = BitmapFactory.decodeFile(context.getFilesDir().getPath() + "/" + ENpics[position].trim());
                //holder. imageView.setImageBitmap(Bitmap.createScaledBitmap(bmp, 120, 170, false));
                Picasso.with(context)
                        .load(file)
                        .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                        .error(R.drawable.icon)
                        //.resize(240,270)
                        .transform(new RoundedTransformation(45,2))
                        .into(holder.imageView);





            } else {
                holder.v.setVisibility(View.INVISIBLE);
                holder.year.setVisibility(View.INVISIBLE);
            }
        }
        if(mod==1)
            if(check[position]) {
                holder.v.setVisibility(View.VISIBLE);
                holder.year.setVisibility(View.VISIBLE);
                holder.year.setText(movie.get(position).getYear());
            }
            else {
                holder.v.setVisibility(View.INVISIBLE);
                holder.year.setVisibility(View.INVISIBLE);
            }
            if(movie.get(position).isTranslated())
                holder.IsTranlated.setVisibility(View.VISIBLE);

    }

    @Override
    public int getItemCount() {
        return movie.size();
    }

}
