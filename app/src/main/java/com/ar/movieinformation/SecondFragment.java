package com.ar.movieinformation;

/**
 * Created by alireza on 29/08/2017.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ar.movieinformation.OMDB.Model.ShortPlot;
import com.squareup.picasso.Picasso;

public class SecondFragment extends Fragment {
    RelativeLayout EnStoryBorder,faStoryBorder;
    ImageView FaStoryPic;
    TextView EnStorytext,FaStorytext;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        String EnStory=getArguments().getString("EnStory");
        String FaStory=getArguments().getString("FaStory");
        boolean IsTranslated=getArguments().getBoolean("IsTranslated");

                View v = inflater.inflate(R.layout.story, container, false);
                   EnStoryBorder=(RelativeLayout)v.findViewById(R.id.EnStoryBorder);
                   faStoryBorder=(RelativeLayout)v.findViewById(R.id.faStoryBorder);
                   FaStoryPic=(ImageView) v.findViewById(R.id.FaStoryPic);
                   EnStorytext=(TextView) v.findViewById(R.id.EnStorytext);
                   FaStorytext=(TextView) v.findViewById(R.id.FaStorytext);
                     if(EnStory==null ||EnStory.equals(""))
                        EnStoryBorder.setVisibility(View.GONE);
                    else
                    {
                        EnStorytext.setText(EnStory);
                        EnStorytext.setGravity(Gravity.CENTER);
                    }
                    if(FaStory==null ||FaStory.equals(""))
                        faStoryBorder.setVisibility(View.GONE);
                    else
                    {
                        if(IsTranslated)
                            FaStoryPic.setImageResource(R.drawable.googletranslate);

                        else Picasso.with(getContext())
                                .load(R.drawable.logo_nadsfaf)
                                .transform(new RoundedTransformation(80,5))
                                .into(FaStoryPic);
                        FaStorytext.setText(FaStory.trim() + "...");

                        FaStorytext.setGravity(Gravity.CENTER);
                    }
                return v;


    }
    public static SecondFragment setdataandshow(ShortPlot movie) {

        SecondFragment f = new SecondFragment();
        Bundle b = new Bundle();
        b.putString("EnStory", movie.getPlot());
        b.putString("FaStory", movie.getFaPlot());
        b.putBoolean("IsTranslated",movie.isTranslatedPlot());
        f.setArguments(b);
        return f;
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
