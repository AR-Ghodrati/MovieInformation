package com.ar.movieinformation.OMDB;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ar.movieinformation.OMDB.Model.ShortPlot;
import com.google.gson.Gson;

public class AROMDB
{

    private MovieListener movieListeneer;
    private RequestQueue requestQueue;
   public AROMDB(Context context)
   {
       requestQueue= Volley.newRequestQueue(context);
   }
    public static class Builder
    {
        private String ApiKey="48c378c2";
        private String OMDBURL="http://www.omdbapi.com";
        private static String IdKey="i";
        private static String TitleKey="t";
        private static String YearKey="y";
        private static String PlotFullKey="plot=full";


        private String title=null;
        private String id=null;
        private String year=null;
        private boolean IsFullplot=false;

        public Builder BuildWithMovieByTitle(String title,String year)
        {
            this.title=title;
            this.year=year;
            return this;
        }
        public Builder BuildWithMoviebyId(String id)
        {
            this.id=id;
            return this;
        }
        public Builder setFullplot()
        {
            this.IsFullplot=true;
            return this;
        }
        public void preaper()
        {
            String URL;
            if(title!=null && year!=null)
            {
                if(this.IsFullplot)
                 URL=OMDBURL+"/?apikey="+ApiKey+"&"+TitleKey+"="+title.replaceAll(" ","+")+"&"+YearKey+"="+year+"&"+PlotFullKey;
                else  URL=OMDBURL+"/?apikey="+ApiKey+"&"+TitleKey+"="+title.replaceAll(" ","+")+"&"+YearKey+"="+year;

                StringRequest stringRequest=new StringRequest(Request.Method.GET, URL, response -> {
                    ShortPlot fullPlot=new Gson().fromJson(response,ShortPlot.class);
                    if(fullPlot!=null)
                    {

                    }


                }, error -> {

                });
            }

        }
    }
    public interface MovieListener
    {
        void Movie(Movie movie);
    }
}