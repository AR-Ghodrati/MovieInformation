package com.ar.movieinformation;

import java.util.Vector;

/**
 * Created by alireza on 03/11/2017.
 */

public class DirClass {
    private String dirname="";
    private int moviescount=0;
    private Vector<String> moviesPosters;
    void SetDirName(String name){this.dirname=name;}
    void SetDirMoviesCount(int num){this.moviescount=num;}
    void SetMoviesPoster(String path){
        if(!moviesPosters.contains(path))
        this.moviesPosters.addElement(path);
    }
    String GetDirName(){return dirname;}
    int GetMoviesCount(){return moviescount;}
    Vector<String> GetMoviesPoster(){return moviesPosters;}
}
