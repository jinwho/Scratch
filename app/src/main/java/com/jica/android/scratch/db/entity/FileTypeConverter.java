package com.jica.android.scratch.db.entity;

import android.arch.persistence.room.TypeConverter;

import java.io.File;

public class FileTypeConverter {
    @TypeConverter
    public static File toFile(String path){
        return path == null ? null : new File(path);
    }

    @TypeConverter
    public static String toString(File file){
        return file == null ? null : file.toString();
    }
}
