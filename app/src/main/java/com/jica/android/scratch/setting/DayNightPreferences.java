package com.jica.android.scratch.setting;

import android.content.SharedPreferences;

public class DayNightPreferences {


    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private Mode mode = Mode.AUTO;


    public DayNightPreferences(SharedPreferences sharedPref) {
        this.sharedPref = sharedPref;
    }

    String getNightMode() {
        return mode.mode_name;
    }

    void setNightMode() {

    }

    void setNextMode() {
        mode = mode.next();
        editor = sharedPref.edit();
        editor.putString("night_mode", mode.mode_name);
        editor.apply();
    }

    enum Mode {
        AUTO("AUTO"), DAY("DAY"), NIGHT("NIGHT");

        String mode_name;

        Mode(String mode_name) {
            this.mode_name = mode_name;
        }

        public Mode next() {
            Mode[] modes = values();
            return modes[(this.ordinal() + 1) % modes.length];
        }
    }

}
