package com.ar.movieinformation.OMDB.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Alireza Ghodrati on 20/02/2018.
 */

public class Rating implements Serializable {
    @SerializedName("Source")
    @Expose
    private String source;
    @SerializedName("Value")
    @Expose
    private String value;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
