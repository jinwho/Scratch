package com.jica.android.scratch.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;


import com.jica.android.scratch.db.entity.Note;
import com.jica.android.scratch.db.entity.SmallNote;

import java.util.List;

@Dao
public interface NoteDao {

    @Query("SELECT id,title from Note")
    LiveData<List<SmallNote>> getSmallNotes();

    @Query("SELECT * from Note where id = :id")
    LiveData<Note> getNote(Integer id);

    @Query("DELETE FROM Note where id = :ids")
    void deleteNote(Integer... ids);

    @Query("DELETE FROM Note")
    void deleteAll();

    @Insert
    void insert(Note... notes);

    @Update
    void update(Note... notes);

    @Delete
    void delete(Note... notes);


}
