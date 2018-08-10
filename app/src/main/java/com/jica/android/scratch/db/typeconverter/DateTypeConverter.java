package com.jica.android.scratch.db.typeconverter;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;

public class DateTypeConverter {

    @TypeConverter
    public static Date toDate(Long number){
        return number == null ? null : new Date(number);
    }

    @TypeConverter
    public static Long toLong(Date date){
        return date == null ? null : date.getTime();
    }
}