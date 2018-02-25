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
import java.util.Vector;

public class moviecustomlist extends RecyclerView.Adapter<moviecustomlist.MyViewHolder> {

    private Context context;
    Vector<Movie>movies;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView year,MovieENTitle,MovieFATitle;
        ImageView imageView,transletaedPic;
        View v;
        public MyViewHolder(View view) {
            super(view);
             MovieENTitle = (TextView) view.findViewById(R.id.firstLine);
             MovieFATitle = (TextView) view.findViewById(R.id.secondLine);
             v=view.findViewById(R.id.viewyear);
             imageView = (ImageView) view.findViewById(R.id.icon);
            transletaedPic= (ImageView) view.findViewById(R.id.IsTranlated);
             year = (TextView) view.findViewById(R.id.yearsort);
        }
    }
    public moviecustomlist(
            Vector<Movie>movies) {
        this.movies=movies;

    }

    @Override
    public void onViewRecycled(MyViewHolder holder) {
        super.onViewRecycled(holder);
        holder.transletaedPic.setVisibility(View.GONE);
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
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movielist_recycler, parent, false);
        this.context=parent.getContext();
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.MovieENTitle.setText(movies.elementAt(position).getTitle());
        holder. MovieFATitle.setText(movies.elementAt(position).getTitleFa());
        File file = null;
        if (movies.elementAt(position).getTitle() != null) {
            file = new File(context.getFilesDir().getPath() + "/" + movies.elementAt(position).getTitle().trim());
        }
        if(file!=null) {
            if (file.exists()) {
                Picasso.with(context)
                        .load(file)
                        .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                        .error(R.drawable.icon)
                        .transform(new RoundedTransformation(45,5))
                        .into(holder.imageView);
                //Bitmap bmp = BitmapFactory.decodeFile(context.getFilesDir().getPath() + "/" + ENpics[position].trim());
                //holder.imageView.setImageBitmap(Bitmap.createScaledBitmap(bmp, 120, 170, false));

                holder. v.setVisibility(View.INVISIBLE);
                holder.year.setVisibility(View.INVISIBLE);
            }
        }
        if(movies.get(position).isTranslated())
            holder.transletaedPic.setVisibility(View.VISIBLE);


    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

}