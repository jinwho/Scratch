package com.jica.android.scratch;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;


import com.jica.android.scratch.db.NoteViewModel;
import com.jica.android.scratch.db.entity.Note;

import java.util.Date;

public class EditActivity extends AppCompatActivity {

    private NoteViewModel noteViewModel;
    private Note note;

    private boolean isUpdateMode;

    private TextView title;
    private TextView contents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        noteViewModel = ViewModelProviders.of(this).get(NoteViewModel.class);

        title = findViewById(R.id.title);
        contents = findViewById(R.id.contents);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            if (extras.containsKey("id")) {
                isUpdateMode = true;
                Integer id = extras.getInt("id");

                noteViewModel.getNote(id).observe(this, new Observer<Note>() {
                    @Override
                    public void onChanged(@Nullable Note note) {
                        //set ui
                        if (note != null) {
                            EditActivity.this.note = note;
                            title.setText(note.getTitle());
                            contents.setText(note.getContents());
                        }
                    }
                });
            }
        } else {
            isUpdateMode = false;
            note = new Note();
        }
        FloatingActionButton fab_save = findViewById(R.id.fab_save);
        fab_save.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                note.setTitle(title.getText().toString());
                note.setContents(contents.getText().toString());

                //getNote current date
                Date now = new Date();

                if (isUpdateMode) {
                    note.setModified(now);
                    noteViewModel.update(note);
                } else {
                    note.setCreated(now);
                    note.setModified(now);
                    noteViewModel.insert(note);
                }
                finish();
            }
        });
    }
}