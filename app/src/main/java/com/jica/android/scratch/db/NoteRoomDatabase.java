package com.jica.android.scratch.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.jica.android.scratch.db.entity.Note;


@Database(entities = {Note.class}, version = 2, exportSchema = false)
public abstract class NoteRoomDatabase extends RoomDatabase {

    public abstract NoteDao noteDao();

    private static NoteRoomDatabase INSTANCE;

    public static NoteRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (NoteRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            NoteRoomDatabase.class, "note_db")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
