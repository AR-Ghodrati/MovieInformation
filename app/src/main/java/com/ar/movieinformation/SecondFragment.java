package com.ar.movieinformation;

/**
 * Created by alireza on 29/08/2017.
 */

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ar.movieinformation.OMDB.Model.ShortPlot;
import com.squareup.picasso.Picasso;

import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

import static android.content.Context.MODE_PRIVATE;

public class SecondFragment extends Fragment {
    RelativeLayout EnStoryBorder,faStoryBorder;
    ImageView FaStoryPic,EnStoryPic;
    TextView EnStorytext,FaStorytext;
    Activity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.e("onAttachF2","Called");
        this.activity=activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        String EnStory=getArguments().getString("EnStory");
        String FaStory=getArguments().getString("FaStory");

        boolean IsTranslated=getArguments().getBoolean("IsTranslated");

                View v = inflater.inflate(R.layout.story, container, false);
               // ShowCase1(v);
                   EnStoryBorder=(RelativeLayout)v.findViewById(R.id.EnStoryBorder);
                   faStoryBorder=(RelativeLayout)v.findViewById(R.id.faStoryBorder);
                   FaStoryPic=(ImageView) v.findViewById(R.id.FaStoryPic);
                    EnStoryPic=(ImageView) v.findViewById(R.id.EnStoryPic);
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
                    else {
                        if (IsTranslated) {
                            FaStoryPic.setImageResource(R.drawable.googletranslate);
                            //ShowCase3(v);
                        }
                        else
                        {
                         Picasso.with(getContext())
                                .load(R.drawable.logo_nadsfaf)
                                .transform(new RoundedTransformation(80, 5))
                                .into(FaStoryPic);
                          //  ShowCase2(v);
                        }
                        FaStorytext.setText(FaStory.trim() + "...");

                        FaStorytext.setGravity(Gravity.CENTER);
                    }


                return v;


    }

    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        Log.e("setMenuVisibilityF2",""+visible);
        if (visible && activity!=null) {
            SharedPreferences preferences = activity.getSharedPreferences("movieinfosh", MODE_PRIVATE);
            if(!preferences.getBoolean("ShowCaseLASTFRAG",false))
            ShowCase();
        }
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
    void ShowCase()
    {
        SharedPreferences.Editor editor = activity.getSharedPreferences("movieinfosh", MODE_PRIVATE).edit();

        new MaterialTapTargetPrompt.Builder(activity)
                .setTarget(EnStoryPic)
                .setPrimaryText("داستان فیلم به زبان انگلیسی")
                .setSecondaryText(" داستان فیلم از سایت نقد فارسی یا ترجمه شده داستان فیلم با مترجم گوگل در پایین داستان به زبان انگلیسی قرار دارد")
                .setAutoDismiss(true)
                .setBackButtonDismissEnabled(true)
                .setFocalColour(getResources().getColor(R.color.descriptionTextColor))
                .setBackgroundColour(getResources().getColor(R.color.outerCircleColor))
                .setFocalRadius(105f)
                .setPromptStateChangeListener((prompt, state) -> {
                    if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED)
                    {
                        editor.putBoolean("ShowCaseLASTFRAG",true);
                        editor.apply();
                    }
                    else if(state == MaterialTapTargetPrompt.STATE_DISMISSED)
                    {
                        editor.putBoolean("ShowCaseLASTFRAG",true);
                        editor.apply();
                    }
                }).show();


    }

}
