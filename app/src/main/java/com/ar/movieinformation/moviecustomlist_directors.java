package com.ar.movieinformation;

/**
 * Created by alireza on 02/09/2017.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Vector;

import static com.ar.movieinformation.movie_list.MovieData;
import static com.ar.movieinformation.movie_list.MovieList;

public class moviecustomlist_directors extends RecyclerView.Adapter<moviecustomlist_directors.MyViewHolder> {

    private int COUNT=0;
    private Vector<String> Dirnames=new Vector<>();
    private Context context;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView MovieENTitle,MovieFATitle,avetext;
        ImageView diricon1,diricon2,diricon3;
        File one,two,three;
        public MyViewHolder(View view) {
            super(view);
             MovieENTitle = (TextView) view.findViewById(R.id.firstLinedir);
             MovieFATitle = (TextView) view.findViewById(R.id.secondLinedir);
             avetext=(TextView)view.findViewById(R.id.avemovierank) ;
             diricon1 = (ImageView) view.findViewById(R.id.icondir);
             diricon2 = (ImageView) view.findViewById(R.id.icondir2);
             diricon3 = (ImageView) view.findViewById(R.id.icondir3);
        }
        void setimageone(File one) {
            this.one=one;
            Picasso.
                    with(context).
                    load(one)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .error(R.drawable.icon)
                    .transform(new RoundedTransformation(45,5))
                    . into(diricon1);
        }
        void setimageTwo(File two) {
            this.two=two;
            Picasso.with(context)
                    .load(two)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .error(R.drawable.icon)
                    .transform(new RoundedTransformation(45,5))
                    .into(diricon2);
        }
        void setimagesThree(File three) {
            this.three=three;
            Picasso
                    .with(context)
                    .load(three)
                    .error(R.drawable.icon)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .transform(new RoundedTransformation(45,5))
                    .into(diricon3);
        }
        void setallVisible()
        {
            diricon1.setVisibility(View.VISIBLE);
            diricon2.setVisibility(View.VISIBLE);
            diricon3.setVisibility(View.VISIBLE);

        }
        void Clear()
        {
            if(one!=null) {
                diricon1.setImageDrawable(null);
                diricon1.invalidate();
                Picasso.with(context).invalidate(one);
            }
            if(two!=null) {
                diricon2.setImageDrawable(null);
                diricon2.invalidate();
                Picasso.with(context).invalidate(two);
            }
            if(three!=null) {
                diricon3.setImageDrawable(null);
                diricon3.invalidate();
                Picasso.with(context).invalidate(three);
            }
        }

    }
    moviecustomlist_directors( Vector<String> dirnames) {
     this.Dirnames=dirnames;
     this.COUNT=dirnames.size();
    }
    public void setcount(int count){
        COUNT=count;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.moviedir_recycler_layout, parent, false);
        this.context=parent.getContext();
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        holder.setallVisible();
        Vector<String> moviesPICS=new Vector<>();
        int Dirmoviescount=0;
        float rank=0;

        holder.MovieENTitle.setText(Dirnames.elementAt(position));
        holder.MovieENTitle.setGravity(Gravity.CENTER);

        for (int i = 0; i <MovieList.size() ; i++) {
            String dirname = MovieData.get(MovieList.get(i).getTitle().trim()).getDirector();
            if (Dirnames.elementAt(position).equals(dirname)) {
                String moviename = MovieList.get(i).getTitle().trim();
                rank+=Double.parseDouble(MovieList.get(i).getImdbRating());
                Dirmoviescount++;
                moviesPICS.addElement(moviename);
        }

        }
        holder.MovieFATitle.setText("تعداد فیلم ها : "+Dirmoviescount);
        holder.avetext.setText(""+String.format("%.1f",rank/Dirmoviescount));
        holder.avetext.setGravity(Gravity.CENTER);
        if(moviesPICS.size()>=1) {
            if (moviesPICS.elementAt(0) != null) {
               File file = new File(context.getFilesDir().getPath() + "/" + moviesPICS.elementAt(0).trim());
                if (file .exists()) {
                       // Picasso.with(context).load(file).into(holder. diricon1);
                      holder.setimageone(file);
                        //Bitmap bmp = BitmapFactory.decodeFile(context.getFilesDir().getPath() + "/" + moviesPICS.elementAt(0).trim());
                        // holder. diricon1.setImageBitmap(Bitmap.createScaledBitmap(bmp, 110, 160, false));
            }
        }
            if(moviesPICS.size()>=2) {
                if (moviesPICS.elementAt(1) != null) {
                    File file = new File(context.getFilesDir().getPath() + "/" + moviesPICS.elementAt(1).trim());
                    if (file.exists()) {
                       // Picasso.with(context).load(file).into(holder. diricon2);
                        holder.setimageTwo(file);
                        //Bitmap bmp = BitmapFactory.decodeFile(context.getFilesDir().getPath() + "/" + moviesPICS.elementAt(1).trim());
                        //holder.  diricon2.setImageBitmap(Bitmap.createScaledBitmap(bmp, 110, 160, false));
                    }
                }
            }
            if(moviesPICS.size()>=3) {
                if (moviesPICS.elementAt(2) != null) {
                  File  file = new File(context.getFilesDir().getPath() + "/" + moviesPICS.elementAt(2).trim());
                    if (file.exists()) {
                      //  Picasso.with(context).load(file2).into(holder.diricon3);
                        holder.setimagesThree(file);
                        // Bitmap bmp = BitmapFactory.decodeFile(context.getFilesDir().getPath() + "/" + moviesPICS.elementAt(2).trim());
                        // holder.diricon3.setImageBitmap(Bitmap.createScaledBitmap(bmp, 110, 160, false));
                    }
                }
            }
            if(moviesPICS.size()==1)
            {
                holder. diricon2.setVisibility(View.INVISIBLE);
                holder.diricon3.setVisibility(View.INVISIBLE);
            }
            else if(moviesPICS.size()==2)
            {
                holder.diricon3.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public void onViewRecycled(MyViewHolder holder) {
        holder.Clear();
        super.onViewRecycled(holder);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
         return COUNT;
    }

}