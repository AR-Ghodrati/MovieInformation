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

import com.ar.movieinformation.OMDB.Model.ShortPlot;

public class FourFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        String nagedData = getArguments().getString("naghed");
        if(nagedData==null) {
            View v = inflater.inflate(R.layout.notfound_layout, container, false);
            TextView naghed_text = (TextView) v.findViewById(R.id.notfound_text);
            naghed_text.setText("نقد فیلم یافت نشد");
            return v;
        }
        else {
            if(nagedData.length()>10) {
                View v = inflater.inflate(R.layout.naghed_layout, container, false);
                TextView naghed_text = (TextView) v.findViewById(R.id.naghed_textView);
                naghed_text.setText(nagedData);
                naghed_text.setGravity(Gravity.CENTER);
                return v;
            }
            else
            {
                View v = inflater.inflate(R.layout.notfound_layout, container, false);
                TextView naghed_text = (TextView) v.findViewById(R.id.notfound_text);
                naghed_text.setText("نقد فیلم یافت نشد");
                return v;
            }
        }
    }

    public static FourFragment setdataandshow(ShortPlot plot) {

        FourFragment f = new FourFragment();
        Bundle b = new Bundle();
        b.putString("naghed",plot.getNaghed());
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
