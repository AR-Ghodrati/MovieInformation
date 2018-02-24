package com.ar.movieinformation;

/**
 * Created by alireza on 29/08/2017.
 */

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ar.movieinformation.OMDB.Model.ShortPlot;

public class ThirdFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.movieinfo, container, false);
        ShortPlot Movie=(ShortPlot) getArguments().getSerializable("OMDb");

        TextView year = (TextView) v.findViewById(R.id.YEAR);
        TextView dir = (TextView) v.findViewById(R.id.DIRname);
        TextView cast = (TextView) v.findViewById(R.id.CASTs);
        TextView CAST = (TextView) v.findViewById(R.id.CAST);
        CAST.setText("بازیگران:");
        cast.setSelected(true);
        TextView time=(TextView)v.findViewById(R.id.TIME) ;
        TextView award=(TextView)v.findViewById(R.id.AWARD) ;

        TextView style=(TextView)v.findViewById(R.id.STYLE) ;
     //   TextView wirtername=(TextView)v.findViewById(R.id.wirtername) ;
        TextView productionname=(TextView)v.findViewById(R.id.productionname) ;
        productionname.setSelected(true);
        TextView languagenname=(TextView)v.findViewById(R.id.languagenname) ;
        languagenname.setSelected(true);
        TextView countrynname=(TextView)v.findViewById(R.id.countrynname) ;
        countrynname.setSelected(true);
        TextView awardtext=(TextView)v.findViewById(R.id.textView16) ;
        ImageView movieaward_notfound=(ImageView)v.findViewById(R.id.movieaward_notfound_icon);
        ImageView moviestyle_notfound=(ImageView)v.findViewById(R.id.moviestyle_notfound_icon);
        ImageView movietime_notfound=(ImageView)v.findViewById(R.id.movietime_notfound_icon);
        TextView movieaward_notfound_t=(TextView)v.findViewById(R.id.movieaward_notfound_text) ;
        TextView moviestyle_notfound_t=(TextView)v.findViewById(R.id.moviestyle_notfound_text) ;
        TextView movietime_notfound_t=(TextView)v.findViewById(R.id.movietime_notfound_text) ;
        ImageView awardTranslated=(ImageView)v.findViewById(R.id.awardTranslated);
        ImageView awardpic=(ImageView)v.findViewById(R.id.awardpic) ;
     /*  if(Movie.getWriter()!=null && !Movie.getWriter().equals("N/A"))
            wirtername.setText(Movie.getWriter());
            */
        if(Movie.getProduction()!=null && !Movie.getProduction().equals("N/A"))
            productionname.setText(Movie.getProduction());
        if(Movie.getLanguage()!=null && !Movie.getLanguage().equals("N/A"))
            languagenname.setText(Movie.getLanguage());
        if(Movie.getCountry()!=null && !Movie.getCountry().equals("N/A"))
            countrynname.setText(Movie.getCountry());

        if(Movie.isTranslatedAwards())
            awardTranslated.setVisibility(View.VISIBLE);
        else   awardTranslated.setVisibility(View.GONE);
        awardpic.setImageBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.award),256,256,false));
        year.setText("سال ساخت  :  "+getArguments().getString("year"));
        dir.setText(getArguments().getString("dir"));
        dir.setSelected(true);
        cast.setText(Movie.getActors());
        if(Movie.getRuntime()!=null && !Movie.getRuntime().equals(""))
        time.setText(" مدت زمان فیلم  :  "+Movie.getRuntime().replaceAll("min","")+" دقیقه  ");
        else
        {
            time.setVisibility(View.INVISIBLE);
            movietime_notfound.setVisibility(View.VISIBLE);
            movietime_notfound_t.setVisibility(View.VISIBLE);
        }
        if(Movie.isTranslatedAwards())
        {
            award.setText(Movie.getAwards().replace("برنده دیگر","جایزه دیگر")
            .replace("نامزد دیگر","نامزدی دیگر"));
        }
        else {
            StringBuilder awardS = new StringBuilder();
            boolean a = false;
            boolean a1 = false;
            boolean b = false;
            boolean b1 = false;
            boolean c = false;
            for (int i = 0; i < getArguments().getString("award").length(); i++) {
                if (!getArguments().getString("award").equals(" "))
                    c = true;
                if (awardS.indexOf("برنده اسکار : ") > 0 && !a) {
                    awardS.append("\n\n");
                    awardS.replace(awardS.indexOf("برنده اسکار : ") - 2, awardS.indexOf("برنده اسکار : ") - 1, "\n");
                    a = true;
                } else if (awardS.indexOf("برنده جایزه اسکار ") > 0 && !a1) {
                    awardS.append(":");
                    awardS.append("\n\n");
                    awardS.replace(awardS.indexOf("برنده جایزه اسکار ") - 2, awardS.indexOf("برنده جایزه اسکار ") - 1, "\n");
                    a1 = true;
                }
                if (awardS.indexOf("نامزد اسکار : ") > 0 && !b) {
                    awardS.append("\n\n");
                    awardS.replace(awardS.indexOf("نامزد اسکار : ") - 2, awardS.indexOf("نامزد اسکار : ") - 1, "\n\n");
                    b = true;
                } else if (awardS.indexOf("نامزد جایزه اسکار ") > 0 && !b1) {
                    awardS.append(":");
                    awardS.append("\n\n");
                    awardS.replace(awardS.indexOf("نامزد جایزه اسکار ") - 2, awardS.indexOf("نامزد جایزه اسکار ") - 1, "\n\n");
                    b1 = true;
                }
                if (c && getArguments().getString("award").charAt(i) >= 'آ' && getArguments().getString("award").charAt(i) <= 'ی' || getArguments().getString("award").charAt(i) == ' ' || getArguments().getString("award").charAt(i) == '.' || getArguments().getString("award").charAt(i) == '،' || getArguments().getString("award").charAt(i) == ':')
                    awardS.append(getArguments().getString("award").charAt(i));
            }
            if (getArguments().getString("award").equals("جوایز فیلم یافت نشد")) {
                award.setVisibility(View.INVISIBLE);
                awardtext.setVisibility(View.INVISIBLE);
                movieaward_notfound.setVisibility(View.VISIBLE);
                movieaward_notfound_t.setVisibility(View.VISIBLE);
            } else award.setText(awardS);
        }

        String ss=Movie.getGenre();
            if(ss==null)
            {
                style.setVisibility(View.INVISIBLE);
                moviestyle_notfound.setVisibility(View.VISIBLE);
                moviestyle_notfound_t.setVisibility(View.VISIBLE);
            }
            else
            style.setText("  سبک فیلم :  "+getPesrianGenre(ss.trim()));
        return v;
    }
    private String getPesrianGenre(String genre)
    {
        StringBuilder stringBuilder=new StringBuilder();
        String[]genres=genre.split(",");
        for (String genre1 : genres) {
            switch (genre1.replaceAll(" ",""))
            {
                case "Action":
                    stringBuilder.append("اکشن");
                    stringBuilder.append(",");
                    break;
                case "Drama":
                    stringBuilder.append("درام");
                    stringBuilder.append(",");
                    break;
                case "Fantasy":
                    stringBuilder.append("فانتزی");
                    stringBuilder.append(",");
                    break;
                case "Crime":
                    stringBuilder.append("جنایی");
                    stringBuilder.append(",");
                    break;
                case "Horror":
                    stringBuilder.append("ترسناک");
                    stringBuilder.append(",");
                    break;
                case "Mystery":
                    stringBuilder.append("راز آلود");
                    stringBuilder.append(",");
                    break;
                case "Comedy":
                    stringBuilder.append("کمدی");
                    stringBuilder.append(",");
                    break;
                case "Thriller":
                    stringBuilder.append("هیجانی");
                    stringBuilder.append(",");
                    break;
                case "Romance":
                    stringBuilder.append("رمانتیک");
                    stringBuilder.append(",");
                    break;
                case "Sci-Fi":
                    stringBuilder.append("علمی تخیلی");
                    stringBuilder.append(",");
                    break;
                case "Western":
                    stringBuilder.append("وسترن");
                    stringBuilder.append(",");
                    break;
                case "Documentary":
                    stringBuilder.append("مستند");
                    stringBuilder.append(",");
                    break;
                case "Animation":
                    stringBuilder.append("انیمیشن");
                    stringBuilder.append(",");
                    break;
                case "Film-Noir":
                stringBuilder.append("فیلم سیاه");
                stringBuilder.append(",");
                break;
                case "Biography":
                    stringBuilder.append("زندگی نامه");
                    stringBuilder.append(",");
                    break;
                case "Family":
                    stringBuilder.append("خانوادگی");
                    stringBuilder.append(",");
                    break;
                case "Short":
                    stringBuilder.append("فیلم کوتاه");
                    stringBuilder.append(",");
                    break;
                case "History":
                    stringBuilder.append("تاریخی");
                    stringBuilder.append(",");
                    break;
                case "Musical":
                    stringBuilder.append("موزیکال");
                    stringBuilder.append(",");
                    break;


            }

        }
        return stringBuilder.substring(0, stringBuilder.length() - 1);
    }

    public static ThirdFragment setdataandshow(Movie movie,ShortPlot plot) {


        ThirdFragment f = new ThirdFragment();
        Bundle b = new Bundle();
        b.putString("dir",plot.getDirector());
        b.putString("cast", plot.getActors());
        b.putString("year", movie.getYear());
        b.putString("award", plot.getAwards());
        b.putString("time", plot.getRuntime());
        b.putString("style", plot.getGenre());
        b.putSerializable("OMDb",plot);
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
        FontChangeCrawler fontChanger = new FontChangeCrawler(getActivity().getAssets(), "fonts/"+getshared.getString("font_type","SERIF")+".ttf",size);
        fontChanger.replaceFonts((ViewGroup) this.getView());
    }
    */
}
