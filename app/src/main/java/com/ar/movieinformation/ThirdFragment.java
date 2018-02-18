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
public class ThirdFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.movieinfo, container, false);
        TextView year = (TextView) v.findViewById(R.id.YEAR);
        TextView dir = (TextView) v.findViewById(R.id.DIRname);
        TextView cast = (TextView) v.findViewById(R.id.CAST);
        TextView time=(TextView)v.findViewById(R.id.TIME) ;
        TextView award=(TextView)v.findViewById(R.id.AWARD) ;
        TextView style=(TextView)v.findViewById(R.id.STYLE) ;
        TextView awardtext=(TextView)v.findViewById(R.id.textView16) ;
        ImageView movieaward_notfound=(ImageView)v.findViewById(R.id.movieaward_notfound_icon);
        ImageView moviestyle_notfound=(ImageView)v.findViewById(R.id.moviestyle_notfound_icon);
        ImageView movietime_notfound=(ImageView)v.findViewById(R.id.movietime_notfound_icon);
        TextView movieaward_notfound_t=(TextView)v.findViewById(R.id.movieaward_notfound_text) ;
        TextView moviestyle_notfound_t=(TextView)v.findViewById(R.id.moviestyle_notfound_text) ;
        TextView movietime_notfound_t=(TextView)v.findViewById(R.id.movietime_notfound_text) ;
        ImageView awardpic=(ImageView)v.findViewById(R.id.awardpic) ;
        awardpic.setImageBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.award),256,256,false));
        year.setText("سال ساخت  :  "+getArguments().getString("year"));
        dir.setText(getArguments().getString("dir")+"  :  نام کارگردان  ");
        cast.setText("   بازیگران : "+"    \n\n     "+getArguments().getString("cast")+"    ");
        if(!getArguments().getString("time").equals(" زمان فیلم یافت نشد "))
        time.setText(" مدت زمان فیلم  :  "+getArguments().getString("time")+"  دقیقه  ");
        else
        {
            time.setVisibility(View.INVISIBLE);
            movietime_notfound.setVisibility(View.VISIBLE);
            movietime_notfound_t.setVisibility(View.VISIBLE);
        }
        StringBuilder awardS=new StringBuilder();
        boolean a = false;
        boolean a1 = false;
        boolean b = false;
        boolean b1 = false;
        boolean c= false;
        for (int i = 0; i < getArguments().getString("award").length(); i++) {
            if(!getArguments().getString("award").equals(" "))
                c=true;
            if (awardS.indexOf("برنده اسکار : ") > 0 && !a) {
                awardS.append("\n\n");
                awardS.replace(awardS.indexOf("برنده اسکار : ")-2,awardS.indexOf("برنده اسکار : ")-1,"\n");
                a = true;
            }
            else if (awardS.indexOf("برنده جایزه اسکار ") > 0 && !a1) {
                awardS.append(":");
                awardS.append("\n\n");
                awardS.replace(awardS.indexOf("برنده جایزه اسکار ") - 2, awardS.indexOf("برنده جایزه اسکار ") - 1, "\n");
                a1 = true;
            } if (awardS.indexOf("نامزد اسکار : ") > 0 && !b) {
                awardS.append("\n\n");
                awardS.replace(awardS.indexOf("نامزد اسکار : ")-2,awardS.indexOf("نامزد اسکار : ")-1,"\n\n");
                b = true;
            }
            else if (awardS.indexOf("نامزد جایزه اسکار ") > 0 && !b1) {
                awardS.append(":");
                awardS.append("\n\n");
                awardS.replace(awardS.indexOf("نامزد جایزه اسکار ")-2,awardS.indexOf("نامزد جایزه اسکار ")-1,"\n\n");
                b1 = true;
            }
             if ( c && getArguments().getString("award").charAt(i) >= 'آ' &&  getArguments().getString("award").charAt(i) <= 'ی' ||  getArguments().getString("award").charAt(i) == ' ' ||  getArguments().getString("award").charAt(i) == '.' ||  getArguments().getString("award").charAt(i) == '،'||  getArguments().getString("award").charAt(i) == ':')
                awardS.append( getArguments().getString("award").charAt(i));
        }
        if(getArguments().getString("award").equals("جوایز فیلم یافت نشد"))
        {
            award.setVisibility(View.INVISIBLE);
            awardtext.setVisibility(View.INVISIBLE);
            movieaward_notfound.setVisibility(View.VISIBLE);
            movieaward_notfound_t.setVisibility(View.VISIBLE);
        }
        else award.setText(awardS);
        StringBuilder temp=new StringBuilder();
        String ss=getArguments().getString("style");
        if (ss != null) {
            for (int i = 0; i <ss.length() -1; i++) {
                if(temp.indexOf("علمیتخیلی")>0)
                    temp.replace(ss.indexOf("علمیتخیلی"),ss.indexOf("علمیتخیلی")+9,"علمی تخیلی");
                if(ss.charAt(i)==' ' &&ss.charAt(i+1)!=' ') {
                    temp.append(" ");
                }
                if(ss.charAt(i)!=' ')
                    temp.append(ss.charAt(i));
            }
            if(ss.charAt(ss.length()-1)!='،')
                temp.append(ss.charAt(ss.length()-1));
            if(ss.equals(" سبک فیلم یافت نشد "))
            {
                style.setVisibility(View.INVISIBLE);
                moviestyle_notfound.setVisibility(View.VISIBLE);
                moviestyle_notfound_t.setVisibility(View.VISIBLE);
            }
            else
            style.setText("  سبک فیلم :  "+temp);
        }

        return v;
    }

    public static ThirdFragment setdataandshow(String dir,String cast,String year,String award,String time,String style) {

        ThirdFragment f = new ThirdFragment();
        Bundle b = new Bundle();
        b.putString("dir", dir);
        b.putString("cast", cast);
        b.putString("year", year);
        b.putString("award", award);
        b.putString("time", time);
        b.putString("style", style);
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
