package com.jica.android.scratch.setting;

import android.content.Context;
import android.content.SharedPreferences;

import com.jica.android.scratch.MainActivity;

public class DayNightPreferences {


    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;


    public DayNightPreferences(SharedPreferences sharedPref, Context context) {
        this.sharedPref = sharedPref;
        //SharedPreferences sharedPref22 = MainActivity.getPreferences(Context.MODE_PRIVATE);
    }

}
