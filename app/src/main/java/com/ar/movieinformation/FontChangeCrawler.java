package com.ar.movieinformation;

import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by alireza on 30/10/2017.
 */

public class FontChangeCrawler
{
    private Typeface typeface;
    private float fontsize=18f;

    public FontChangeCrawler(Typeface typeface)
    {
        this.typeface = typeface;
    }

    FontChangeCrawler(AssetManager assets, String assetsFontFileName,float Fontsize)
    {
        typeface = Typeface.createFromAsset(assets, assetsFontFileName);
        this.fontsize=Fontsize;
    }

    void replaceFonts(ViewGroup viewTree)
    {
        View child;
        for(int i = 0; i < viewTree.getChildCount(); ++i)
        {
            child = viewTree.getChildAt(i);
            if(child instanceof ViewGroup)
            {
                // recursive call
                replaceFonts((ViewGroup)child);
            }
            else if(child instanceof TextView)
            {
                // base case
                ((TextView) child).setTypeface(typeface);
                ((TextView) child).setTextSize(fontsize);
            }
        }
    }
}
