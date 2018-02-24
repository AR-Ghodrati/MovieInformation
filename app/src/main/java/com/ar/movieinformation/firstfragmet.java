package com.ar.movieinformation;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ar.movieinformation.OMDB.Model.Rating;
import com.ar.movieinformation.OMDB.Model.ShortPlot;

import java.util.List;

import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by alireza on 29/08/2017.
 */

public class firstfragmet extends Fragment {
    TextView Data;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       return inflater.inflate(R.layout.movieinfolayout, container, false);
    }

    @Override
    public void onViewCreated(View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        ImageView rotten = (ImageView) v.findViewById(R.id.tomatoRating_image);
        RelativeLayout metaRating_background = (RelativeLayout) v.findViewById(R.id.metaRating_background);
        TextView metaRating = (TextView) v.findViewById(R.id.metaRating);
        TextView metaRatingView = (TextView) v.findViewById(R.id.metaRatingView);
        TextView tomatoRating = (TextView) v.findViewById(R.id.tomatoRating);
        TextView imdbRating = (TextView) v.findViewById(R.id.imdbRating);
        TextView imdbRating_rank = (TextView) v.findViewById(R.id.imdbRating_rank);

        LinearLayout layout_meta = (LinearLayout) v.findViewById(R.id.layout_meta);
        LinearLayout layout_tomato = (LinearLayout) v.findViewById(R.id.layout_tomato);
        LinearLayout layout_imdb = (LinearLayout) v.findViewById(R.id.layout_imdb);
        LinearLayout layout_imdb_rank = (LinearLayout) v.findViewById(R.id.layout_imdb_rank);



        Data = (TextView) v.findViewById(R.id.cunterofCount);

        ShortPlot movie = (ShortPlot) getArguments().getSerializable("OMDB");
        Data.setText("   امتیاز  کاربران  : " + getArguments().getString("rank") + " از " + getArguments().getString("countrank") + " امتیاز کاربران");
        imdbRating.setText(getArguments().getString("rank"));
        imdbRating_rank.setText(getArguments().getString("IMDBRANK"));
        imdbRating_rank.setGravity(Gravity.CENTER);

        assert movie != null;
        List<Rating> ratings = movie.getRatings();
        boolean Meta = false, Rotten = false;
        if (ratings != null)
            for (int i = 0; i < ratings.size(); i++) {
                if (ratings.get(i).getSource().equalsIgnoreCase("Rotten Tomatoes")) {
                    Rotten = true;
                    tomatoRating.setText(ratings.get(i).getValue());
                    int tomatometer_score = Integer.parseInt(ratings.get(i).getValue().substring(0, ratings.get(i).getValue().length() - 1));
                    if (tomatometer_score > 74)
                        rotten.setImageDrawable(getResources().getDrawable(R.drawable.certified));
                    else if (tomatometer_score > 59)
                        rotten.setImageDrawable(getResources().getDrawable(R.drawable.fresh));
                    else if (tomatometer_score < 60)
                        rotten.setImageDrawable(getResources().getDrawable(R.drawable.rotten));
                } else if (ratings.get(i).getSource().equalsIgnoreCase("Metacritic")) {
                    Meta = true;
                    metaRating.setText(ratings.get(i).getValue());
                    metaRatingView.setText(movie.getMetascore());
                    if (Integer.parseInt(movie.getMetascore()) > 60)
                        metaRating_background.setBackgroundColor(Color.parseColor("#66cc33"));

                    else if (Integer.parseInt(movie.getMetascore()) > 40 && Integer.parseInt(movie.getMetascore()) < 61)
                        metaRating_background.setBackgroundColor(Color.parseColor("#ffcc33"));

                    else
                        metaRating_background.setBackgroundColor(Color.parseColor("#ff0000"));
                }
            }
        if (!Rotten)
            layout_tomato.setVisibility(View.GONE);
        if (!Meta)
            layout_meta.setVisibility(View.GONE);

        //  smalldata.setText(getArguments().getString("rank"));
        // smalldata.setGravity(Gravity.CENTER);
        imdbRating.setGravity(Gravity.CENTER);

        metaRatingView.setOnClickListener(v1 -> {
            Intent meta = new Intent(Intent.ACTION_VIEW);
            meta.setData(Uri.parse("www.metacritic.com/movie/" + movie.getTitle().replaceAll(" ", "-")));
            Intent chooserIntent = Intent.createChooser(meta, "برای باز کردن سایت metacritic یک مرورگر انتخاب کنید:");
            if (meta.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivity(chooserIntent);
            }
        });
        layout_tomato.setOnClickListener(v12 -> {
            Intent tomato = new Intent(Intent.ACTION_VIEW);
            tomato.setData(Uri.parse("https://www.rottentomatoes.com/m/" + movie.getTitle().replaceAll(" ", "_")));
            Intent chooserIntent = Intent.createChooser(tomato, "برای باز کردن سایت rottentomatoes یک مرورگر انتخاب کنید:");
            if (tomato.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivity(chooserIntent);
            }
        });
        layout_imdb.setOnClickListener(v1 -> {
            Intent imdb = new Intent(Intent.ACTION_VIEW);
            imdb.setData(Uri.parse(getArguments().getString("IMDBlink")));
            Intent chooserIntent = Intent.createChooser(imdb, "برای باز کردن سایت IMDB یک مرورگر انتخاب کنید:");
            if (imdb.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivity(chooserIntent);
            }
        });

        SharedPreferences preferences = getActivity().getSharedPreferences("movieinfosh", MODE_PRIVATE);
        if(!preferences.getBoolean("ShowCaseFIRSTFRAG", false))
        {
            ShowCases(v);

        }
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
    public static firstfragmet setdataandshow(Movie movie, ShortPlot plot) {

        firstfragmet f = new firstfragmet();
        Bundle b = new Bundle();
        b.putString("rank", movie.getImdbRating());
        b.putString("IMDBRANK", movie.getTop_250_Rank());
        b.putString("countrank", movie.countranking() + "");
        b.putString("IMDBlink", movie.getIMDBlink());
        b.putSerializable("OMDB", plot);
        f.setArguments(b);
        return f;
    }
    void ShowCases(View v)
    {
        LinearLayout layout_meta = (LinearLayout) v.findViewById(R.id.layout_meta);
        LinearLayout layout_tomato = (LinearLayout) v.findViewById(R.id.layout_tomato);
        LinearLayout layout_imdb = (LinearLayout) v.findViewById(R.id.layout_imdb);
        LinearLayout layout_imdb_rank = (LinearLayout) v.findViewById(R.id.layout_imdb_rank);

        SharedPreferences.Editor editor = getActivity().getSharedPreferences("movieinfosh", MODE_PRIVATE).edit();

        MaterialTapTargetPrompt.Builder four=new MaterialTapTargetPrompt.Builder(getActivity())
                .setTarget(layout_meta)
                .setPrimaryText("امتیاز فیلم از نظر منتقدین Metacritic")
                .setSecondaryText("با لمس کردن آن می توانید وارد سایت Metacritic شوید")
                .setAutoDismiss(true)
                .setBackButtonDismissEnabled(true)
                .setFocalColour(getResources().getColor(R.color.descriptionTextColor))
                .setBackgroundColour(getResources().getColor(R.color.outerCircleColor))
                .setFocalRadius(105f)
                .setPromptStateChangeListener((prompt, state) -> {
                    if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED)
                    {
                        editor.putBoolean("ShowCaseFIRSTFRAG",true);
                        editor.apply();
                    }
                    else if(state == MaterialTapTargetPrompt.STATE_DISMISSED)
                    {
                        editor.putBoolean("ShowCaseFIRSTFRAG",true);
                        editor.apply();
                    }
                });

        MaterialTapTargetPrompt.Builder three=new MaterialTapTargetPrompt.Builder(getActivity())
                .setTarget(layout_tomato)
                .setPrimaryText("امتیاز فیلم از نظر منتقدین RottenTomatoes")
                .setSecondaryText("با لمس کردن آن می توانید وارد سایت RottenTomatoes شوید")
                .setAutoDismiss(true)
                .setBackButtonDismissEnabled(true)
                .setFocalColour(getResources().getColor(R.color.descriptionTextColor))
                .setBackgroundColour(getResources().getColor(R.color.outerCircleColor))
                .setFocalRadius(105f)
                .setPromptStateChangeListener((prompt, state) -> {
                    if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED)
                    {
                        four.show();
                    }
                    else if(state == MaterialTapTargetPrompt.STATE_DISMISSED)
                    {
                        four.show();
                    }
                });

        MaterialTapTargetPrompt.Builder two=new MaterialTapTargetPrompt.Builder(getActivity())
                .setTarget(layout_imdb)
                .setPrimaryText("امتیاز فیلم از نظر سایت  IMDb ")
                .setSecondaryText("با لمس کردن آن می توانید وارد سایت IMDb شوید")
                .setAutoDismiss(true)
                .setBackButtonDismissEnabled(true)
                .setFocalRadius(105f)
                .setFocalColour(getResources().getColor(R.color.descriptionTextColor))
                .setBackgroundColour(getResources().getColor(R.color.outerCircleColor))
                .setPromptStateChangeListener((prompt, state) -> {
                    if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED)
                    {
                        three.show();
                    }
                    else if(state == MaterialTapTargetPrompt.STATE_DISMISSED)
                    {
                        three.show();
                    }
                });

       new MaterialTapTargetPrompt.Builder(getActivity())
                .setTarget(layout_imdb_rank)
                .setPrimaryText("رتبه فیلم")
                .setSecondaryText("رتبه فیلم در 250 فیلم برتر IMDb")
                .setAutoDismiss(true)
                .setBackButtonDismissEnabled(true)
                .setFocalColour(getResources().getColor(R.color.descriptionTextColor))
                .setBackgroundColour(getResources().getColor(R.color.outerCircleColor))
                .setPromptStateChangeListener((prompt, state) -> {
                    if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED)
                    {
                        two.show();
                    }
                    else if(state == MaterialTapTargetPrompt.STATE_DISMISSED)
                    {
                        two.show();
                    }
                })
                .show();





     /*   ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(500);
        config.setContentTextColor(R.color.descriptionTextColor);
        config.setMaskColor(R.color.outerCircleColor);
        config.setDismissTextColor(R.color.targetCircleColor);

        config.setRenderOverNavigationBar(true);
        config.setFadeDuration(200);

        new MaterialShowcaseView.Builder(getActivity())
                .setTarget(mButtonShow)
                .setDismissText("GOT IT")
                .setContentText("This is some amazing feature you should know about")
                .set
                .setDelay(withDelay) // optional but starting animations immediately in onCreate can make them choppy
                .singleUse(SHOWCASE_ID) // provide a unique ID used to ensure it is only shown once
                .show();


        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(getActivity(), "123");

        sequence.setConfig(config);

        sequence.addSequenceItem(layout_imdb_rank,
                "رتبه فیلم در 250 فیلم برتر IMDb", "فهمیدم");

        sequence.addSequenceItem(layout_imdb,
                "امتیاز فیلم از نظر سایت IMDb", "فهمیدم");

        sequence.addSequenceItem(layout_tomato,
                "امتیاز فیلم از نظر منتقدین RottenTomatoes", "فهمیدم");

        sequence.addSequenceItem(layout_meta,
                "امتیاز فیلم از نظر منتقدین Metacritic", "فهمیدم");
        sequence.start();
        */
    }

}
