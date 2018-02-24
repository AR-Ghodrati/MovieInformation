package com.ar.movieinformation.OMDB_by_another.Model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Alireza Ghodrati on 20/02/2018.
 */

public class Listing {
    @SerializedName("Title")
    private String title;

    @SerializedName("Year")
    private String year;

    @SerializedName("Poster")
    private String poster;

    @SerializedName("imdbID")
    private String imdbID;

    @SerializedName("Type")
    private String type;

    public String getTitle() {
        return title;
    }

    public String getYear() {
        return year;
    }

    public String getImdbID() {
        return imdbID;
    }

    public String getType() {
        return type;
    }

    public String getPoster() {
        return poster;
    }
}
