package com.jica.android.scratch;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.jica.android.scratch.db.NoteViewModel;
import com.jica.android.scratch.db.entity.Note;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditActivity extends AppCompatActivity {

    private NoteViewModel noteViewModel;
    private Note note;

    private boolean isUpdateMode;

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.contents)
    TextView contents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        noteViewModel = ViewModelProviders.of(this).get(NoteViewModel.class);
        ButterKnife.bind(this);

        //set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // 제목 없애기
            actionBar.setDisplayShowTitleEnabled(false);
            // 뒤로가기 버튼
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            if (extras.containsKey("id")) {
                isUpdateMode = true;
                Integer id = extras.getInt("id");

                noteViewModel.getNote(id).observe(this, new Observer<Note>() {
                    @Override
                    public void onChanged(@Nullable Note observer_note) {
                        //set ui
                        if (observer_note != null) {
                            note = observer_note;
                            title.setText(observer_note.getTitle());
                            contents.setText(observer_note.getContents());
                        }
                    }
                });
            } else {
                Log.d("EditActivity", "id does not exist");
                finish();
            }
        } else {
            isUpdateMode = false;
            note = new Note();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menu_save:
                saveNote();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void saveNote() {
        //set note data
        note.setTitle(title.getText().toString());
        note.setContents(contents.getText().toString());

        //get current date
        Date now = new Date();

        // save note
        if (isUpdateMode) {
            note.setModified(now);
            noteViewModel.update(note);
        } else {
            note.setCreated(now);
            note.setModified(now);
            noteViewModel.insert(note);
        }
    }
}