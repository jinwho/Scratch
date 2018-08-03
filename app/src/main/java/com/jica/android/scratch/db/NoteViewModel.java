package com.jica.android.scratch.db;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;


import com.jica.android.scratch.db.entity.Note;
import com.jica.android.scratch.db.entity.SmallNote;

import java.util.List;

public class NoteViewModel extends AndroidViewModel {
    private NoteRepository noteRepository;
    private LiveData<List<SmallNote>> smallNotes;

    public NoteViewModel(Application application) {
        super(application);
        noteRepository = new NoteRepository(application);
        smallNotes = noteRepository.getSmallNotes();
    }

    public LiveData<List<SmallNote>> getSmallNotes() {
        return smallNotes;
    }

    public LiveData<Note> getNote(Integer id) {
        return noteRepository.getNote(id);
    }

    public void delete(Integer id) { noteRepository.delete(id); }

    public void insert(Note note) {
        noteRepository.insert(note);
    }

    public void update(Note note) {
        noteRepository.update(note);
    }


}
