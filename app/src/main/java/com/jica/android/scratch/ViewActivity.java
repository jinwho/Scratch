package com.jica.android.scratch;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.jica.android.scratch.db.NoteViewModel;
import com.jica.android.scratch.db.entity.Note;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewActivity extends AppCompatActivity {

    // Request Code
    private static final int SELECT_FROM_GALLERY = 1;

    // Note data
    private NoteViewModel noteViewModel;
    private Note note;
    private boolean isNewNote;

    // ui data
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.contents)
    TextView contents;
    @BindView(R.id.created)
    TextView created;
    @BindView(R.id.modified)
    TextView modified;
    @BindView(R.id.picture)
    ImageView picture;

    //edit state

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        ButterKnife.bind(this);
        noteViewModel = ViewModelProviders.of(this).get(NoteViewModel.class);

        //set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        //getNote view data
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            if (extras.containsKey("id")) {
                isNewNote = false;
                Integer id = extras.getInt("id");

                noteViewModel.getNote(id).observe(this, new Observer<Note>() {
                    @Override
                    public void onChanged(@Nullable Note observe_note) {
                        //set ui
                        if (observe_note != null) {
                            note = observe_note;
                            title.setText(observe_note.getTitle());
                            contents.setText(observe_note.getContents());

                            //set date print format
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd / HH:mm:ss", Locale.getDefault());
                            String createdText = format.format(observe_note.getCreated());
                            String modifiedText = format.format(observe_note.getModified());

                            created.setText(createdText);
                            modified.setText(modifiedText);

                            //만약 사진이 있다면 보여준다.
                            String filename = observe_note.getFilename();
                            if (filename != null) {
                                picture.setVisibility(View.VISIBLE);
                                File file = new File(getFilesDir(), filename);
                                if (file.exists()) {
                                    picture.setImageBitmap(BitmapFactory.decodeFile(file.getPath()));
                                }
                            }
                        }
                    }
                });
            }
        } else {
            //if no intent
            note = new Note();
            isNewNote = true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.view_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO
        switch (item.getItemId()) {
            case R.id.menu_save:
                saveNote();
                finish();
            case R.id.menu_gallery:
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, SELECT_FROM_GALLERY);
                return true;
            case R.id.menu_delete:
                noteViewModel.delete(note.getId());
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_FROM_GALLERY && resultCode == RESULT_OK) {
            //TODO 사진을 가져와 보여준다, 노트에 파일이름을 추가 한다, 사진은 전역변수로 기억했다가 필요에 따라 저장한다.
        }
    }

    // TODO
    // title, contents의 내용이 변경되거나
    // 사진이 바뀔 경우에 저장한다.

    public void saveNote() {
        note.setTitle(title.getText().toString());
        note.setContents(contents.getText().toString());

        //getNote current date
        Date now = new Date();

        if (isNewNote) {
            note.setCreated(now);
            note.setModified(now);
            noteViewModel.insert(note);
        } else {
            note.setModified(now);
            noteViewModel.update(note);
        }
    }
}
