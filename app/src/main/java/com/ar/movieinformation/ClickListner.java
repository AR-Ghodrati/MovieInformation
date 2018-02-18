package com.ar.movieinformation;

import android.view.View;

/**
 * Created by alireza on 20/12/2017.
 */

public interface ClickListner {
    void onClick(View view, int position);
    void onLongClick(View view, int position);
}
