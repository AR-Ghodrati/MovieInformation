package com.ar.movieinformation;

/**
 * Created by alireza on 16/08/2017.
 */

class Movie {
    private String Movie_name = "";
    private String Movie_Rank = "";
    private String Movie_year = "";
    private String MovieDirector_name = "";
    private String IMDB_RANK_simple = "";
    private String Actors_Actress = "";
    private String IMDB_RANK_full = "";
    private String Movie_picture_link = "";
    private String Movie_IMDB_link = "";
    private String Rank="";
    private String Movie_Farsi_name="";

    Movie() {
        Movie_name = "";
        Movie_Rank = "";
        Movie_year = "";
        MovieDirector_name = "";
        IMDB_RANK_simple = "";
        Actors_Actress = "";
        IMDB_RANK_full = "";
        Movie_picture_link = "";
        Movie_IMDB_link = "";
        Movie_Farsi_name="";
    }

    void setMovie_name(String Moviename) {
        Movie_name = Moviename;
    }
    void setMovie_rank(String rank) {
        Rank = rank;
    }
    void setMovie_farsi_name(String Moviefarsiname) {
        Movie_Farsi_name = Moviefarsiname;
    }

    void setMovie_Rank(String MovieRank) {
        Movie_Rank = MovieRank;
    }

    void setMovie_year(String Movieyear) {
        Movie_year = Movieyear;
    }

    void setMovieDirector_name(String MovieDirectorname) {
        MovieDirector_name = MovieDirectorname;
    }

    void setIMDB_RANK_simple(String IMDBRANKsimple) {
        IMDB_RANK_simple = IMDBRANKsimple;
    }

    void setActors_Actress(String ActorsActress) {
        Actors_Actress = ActorsActress;
    }

    void setIMDB_RANK_full(String IMDBRANKfull) {
        IMDB_RANK_full = IMDBRANKfull;
    }

    void setMovie_picture_link(String Moviepicturelink) {
        Movie_picture_link = Moviepicturelink;
    }

    void setMovie_IMDB_link(String MovieIMDBlink) {
        Movie_IMDB_link = MovieIMDBlink;
    }

    String getMovie_name() {
        return Movie_name;
    }
    String getMovie_name_farsi() {
        return Movie_Farsi_name;
    }
    String getMovie_rank() {
        return Rank;
    }

    String getMovie_Rank() {
        return Movie_Rank;
    }

    String getMovie_year() {
        return Movie_year;
    }

    String getMovieDirector_name() {
        return MovieDirector_name;
    }

    String getIMDB_RANK_simple() {
        return IMDB_RANK_simple;
    }

    String getActors_Actress() {
        return Actors_Actress;
    }

    String getIMDB_RANK_full() {
        return IMDB_RANK_full;
    }

    String getMovie_picture_link() {
        return Movie_picture_link;
    }

    String getMovie_IMDB_link() {
        return Movie_IMDB_link;
    }

    int countranking() {
        String temp = "";
        for (int i = 0; i < IMDB_RANK_full.length(); i++) {
            if (IMDB_RANK_full.charAt(i) >= '0' && IMDB_RANK_full.charAt(i) <= '9')
                temp += IMDB_RANK_full.charAt(i);
        }
        String num = temp.substring(2);
        return Integer.parseInt(num);
    }
}
