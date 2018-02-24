package com.ar.movieinformation;

import java.io.Serializable;

/**
 * Created by alireza on 16/08/2017.
 */

public class Movie implements Serializable {


    private String IMDBlink;
    private String IMDbRankFull;
    private String Top_250_Rank;
    private String savedCountRanking;
    private String titleFa;
    private String poster;


    private String title;
    private String year;
    private String imdbRating;
    private boolean IsTranslated;
    //private ShortPlot ExtraInfo;



    Movie() {


        IsTranslated=false;
       // ExtraInfo=new ShortPlot();
    }

  /*  public ShortPlot getExtraInfo() {
        return ExtraInfo;
    }

    public void setExtraInfo(ShortPlot extraInfo) {
        ExtraInfo = extraInfo;
    }
    */

    public boolean isTranslated() {
        return IsTranslated;
    }

    public void setTranslated(boolean translated) {
        IsTranslated = translated;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }




    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }




    public String getSavedCountRanking() {
        return savedCountRanking;
    }

    public void setSavedCountRanking(String savedCountRanking) {
        this.savedCountRanking = savedCountRanking;
    }

    int countranking() {
      //  Log.e("IMDbRankFull",IMDbRankFull);
        StringBuilder temp = new StringBuilder();
        for (int i = 0; i < IMDbRankFull.length(); i++) {
            if (IMDbRankFull.charAt(i) >= '0' && IMDbRankFull.charAt(i) <= '9')
                temp.append(IMDbRankFull.charAt(i));
        }
        String num = temp.substring(2);
        return Integer.parseInt(num);
    }

    public String getIMDBlink() {
        return IMDBlink;
    }

    public void setIMDBlink(String IMDBlink) {
        this.IMDBlink = IMDBlink;
    }



    public String getTitleFa() {
        return titleFa;
    }

    public String getTop_250_Rank() {
        return Top_250_Rank;
    }

    public void setIMDbRankFull(String IMDbRankFull) {
        this.IMDbRankFull = IMDbRankFull;
    }

    @Override
    public String toString() {
        return super.toString();
    }


    public void setTitleFa(String titleFa) {
        this.titleFa = titleFa;
    }

    public void setTop_250_Rank(String top_250_Rank) {
        Top_250_Rank = top_250_Rank;
    }

    public String getIMDbRankFull() {
        return IMDbRankFull;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }



    public String getImdbRating() {
        return imdbRating;
    }

    public void setImdbRating(String imdbRating) {
        this.imdbRating = imdbRating;
    }




}
