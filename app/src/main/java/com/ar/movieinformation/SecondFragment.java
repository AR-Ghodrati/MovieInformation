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
import android.widget.TextView;

public class SecondFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        String storystring=getArguments().getString("story");
        if (storystring == null) {
            View v = inflater.inflate(R.layout.notfound_layout, container, false);
            TextView story=(TextView)v.findViewById(R.id.notfound_text);
            storystring = "داستان فیلم یافت نشد";
            story.setText(storystring);
            return v;
        }
        else {
            if(storystring.length()>10) {
                View v = inflater.inflate(R.layout.story, container, false);
                TextView story = (TextView) v.findViewById(R.id.Storytext);
                story.setText("\n" + storystring.trim() + "...");
                story.setGravity(Gravity.CENTER);
                return v;
            }
            else {
                View v = inflater.inflate(R.layout.notfound_layout, container, false);
                TextView story=(TextView)v.findViewById(R.id.notfound_text);
                storystring = "داستان فیلم یافت نشد";
                story.setText(storystring);
                return v;
            }
        }
    }
    public static SecondFragment setdataandshow(String story) {

        SecondFragment f = new SecondFragment();
        Bundle b = new Bundle();
        b.putString("story", story);
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
