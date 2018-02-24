package com.ar.movieinformation.OMDB_by_another.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Alireza Ghodrati on 20/02/2018.
 */

public class SearchResponse {
    @SerializedName("Search")
    private List<Listing> results;

    @SerializedName("totalResults")
    private String totalResults;

    @SerializedName("Response")
    private String response;

    @SerializedName("Error")
    private String error;

    public List<Listing> getResults() {
        return results;
    }

    public String getTotalResults() {
        return totalResults;
    }

    public String getResponse() {
        return response;
    }

    public String getError() {
        return error;
    }
}
