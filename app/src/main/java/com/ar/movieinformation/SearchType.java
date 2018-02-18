package com.ar.movieinformation;

/**
 * Created by Alireza Ghodrati on 17/02/2018.
 */

public enum SearchType
{
    ALL("all"), TITLE("tt"), TV_EPISODE("ep"), NAME("nm"), COMPANY("co"), KEYWORD("kw"), CHARACTER("ch"), QUOTE("ch");

    private final String value;

    SearchType(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return this.value;
    }
}
