package com.ar.movieinformation.OMDB_by_another;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by Alireza Ghodrati on 20/02/2018.
 */

public class OmdbAPIFactory {
    static String apikey="48c378c2";
    private OmdbAPIFactory() {}


    private static Retrofit retrofit() {
        return new Retrofit.Builder()
                .baseUrl("http://www.omdbapi.com/?apikey="+apikey+"&")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }


    public static OmdbAPI getOmdbAPI() {
        Omdb omdb = retrofit().create(Omdb.class);
        return new OmdbAPI(omdb);
    }
}
