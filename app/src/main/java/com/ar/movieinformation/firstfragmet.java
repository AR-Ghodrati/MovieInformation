package com.ar.movieinformation;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by alireza on 29/08/2017.
 */

public class firstfragmet extends Fragment {
    TextView Link,Data,Simpledata,smalldata;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.movieinfolayout, container, false);
        ImageView star=(ImageView)v.findViewById(R.id.Star);
        ImageView rank=(ImageView)v.findViewById(R.id.RankIcon);
        Bitmap bmp = BitmapFactory.decodeResource(getActivity().getResources(),R.drawable.star);
        star.setImageBitmap(Bitmap.createScaledBitmap(bmp,512,512,false));
        Bitmap bmp1 = BitmapFactory.decodeResource(getActivity().getResources(),R.drawable.rank);
        rank.setImageBitmap(Bitmap.createScaledBitmap(bmp1,512,512,false));
        Link=(TextView)v.findViewById(R.id.IMDBlink);
        Data=(TextView)v.findViewById(R.id.cunterofCount);
        Simpledata=(TextView)v.findViewById(R.id.SimpleIMDB);
        smalldata=(TextView)v.findViewById(R.id.Showsmalldata);
        Link.setText(" لینک ای ام دی بی  : "+"\n\n\n"+getArguments().getString("IMDBlink"));
        Data.setText("   امتیاز  کاربران  : "+getArguments().getString("rank")+" از "+getArguments().getString("countrank")+" امتیاز کاربران");
        smalldata.setText(getArguments().getString("IMDBRANK"));
        Simpledata.setText(getArguments().getString("rank"));
        smalldata.setGravity(Gravity.CENTER);
        Simpledata.setGravity(Gravity.CENTER);
        return v;
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
    public static firstfragmet setdataandshow(String rank,String IMDBRANK,String countrank,String IMDBlink) {

        firstfragmet f = new firstfragmet();
        Bundle b = new Bundle();
        b.putString("rank", rank);
        b.putString("IMDBRANK",IMDBRANK);
        b.putString("countrank",countrank);
        b.putString("IMDBlink",IMDBlink);
        f.setArguments(b);
        return f;
    }
}
