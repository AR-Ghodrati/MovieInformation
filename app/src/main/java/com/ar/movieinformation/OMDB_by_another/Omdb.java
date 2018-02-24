package com.ar.movieinformation.OMDB_by_another;

import com.ar.movieinformation.OMDB_by_another.Model.DetailedListing;
import com.ar.movieinformation.OMDB_by_another.Model.SearchResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface Omdb {

    @GET("/")
    Call<SearchResponse> search(@QueryMap Map<String, String> queryMap);

    @GET("/")
    Call<DetailedListing> getDetailedListing(@QueryMap Map<String, String> queryMap);

}
