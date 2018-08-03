package com.jica.android.scratch.db;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.jica.android.scratch.db.entity.Note;
import com.jica.android.scratch.db.entity.SmallNote;

import java.util.List;

public class NoteRepository {
    private NoteDao noteDao;
    private LiveData<List<SmallNote>> smallNotes;

    NoteRepository(Application application) {
        NoteRoomDatabase db = NoteRoomDatabase.getDatabase(application);
        noteDao = db.noteDao();
        smallNotes = noteDao.getSmallNotes();
    }

    LiveData<List<SmallNote>> getSmallNotes() {
        return smallNotes;
    }



    public void insert (Note... notes) {
        new insertAsyncTask(noteDao).execute(notes[0]);
    }

    private static class insertAsyncTask extends AsyncTask<Note, Void, Void> {

        private NoteDao noteDao;

        insertAsyncTask(NoteDao noteDao) {
            this.noteDao = noteDao;
        }

        @Override
        protected Void doInBackground(final Note... notes) {
            noteDao.insert(notes[0]);
            return null;
        }
    }

    public void update (Note... notes) {
        new updateAsyncTask(noteDao).execute(notes[0]);
    }

    private static class updateAsyncTask extends AsyncTask<Note, Void, Void> {

        private NoteDao noteDao;

        updateAsyncTask(NoteDao noteDao) {
            this.noteDao = noteDao;
        }

        @Override
        protected Void doInBackground(final Note... notes) {
            noteDao.update(notes[0]);
            return null;
        }
    }

    public void delete (Integer id) {
        new deleteAsyncTask(noteDao).execute(id);
    }

    private static class deleteAsyncTask extends AsyncTask<Integer, Void, Void> {

        private NoteDao noteDao;

        deleteAsyncTask(NoteDao noteDao) {
            this.noteDao = noteDao;
        }

        @Override
        protected Void doInBackground(final Integer... ids) {
            noteDao.deleteNote(ids[0]);
            return null;
        }
    }

    public LiveData<Note> getNote(Integer id) {
        return noteDao.getNote(id);
    }
}
