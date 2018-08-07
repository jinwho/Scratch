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
import android.util.Log;
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

public class ViewActivity extends AppCompatActivity implements View.OnLongClickListener {

    private Note note;

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



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO change image size
        setContentView(R.layout.activity_view);
        ButterKnife.bind(this);
        NoteViewModel noteViewModel = ViewModelProviders.of(this).get(NoteViewModel.class);

        //getNote view data
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            if (extras.containsKey("id")) {
                Integer id = extras.getInt("id");

                noteViewModel.getNote(id).observe(this, new Observer<Note>() {
                    @Override
                    public void onChanged(@Nullable Note observer_note) {
                        //set ui
                        if (observer_note != null) {
                            note = observer_note;
                            title.setText(observer_note.getTitle());
                            contents.setText(observer_note.getContents());

                            //set date print format
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd / HH:mm:ss", Locale.getDefault());
                            String createdText = format.format(observer_note.getCreated());
                            String modifiedText = format.format(observer_note.getModified());

                            created.setText(createdText);
                            modified.setText(modifiedText);

                            //만약 사진이 있다면 보여준다.
                            String filename = observer_note.getFilename();
                            if (filename != null) {
                                File file = new File(getFilesDir(), filename);
                                if (file.exists()) {
                                    picture.setImageBitmap(BitmapFactory.decodeFile(file.getPath()));
                                    picture.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    }
                });
            }else {
                Log.d("ViewActivity", "id does not exist");
                finish();
            }
        } else {
            finish();
        }

        // long click to edit
        title.setOnLongClickListener(this);
        contents.setOnLongClickListener(this);
        picture.setOnLongClickListener(this);

        // click picture to view picture
        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO view picture
            }
        });
    }

    @Override
    public boolean onLongClick(View view) {
        Intent intent = new Intent(this, EditActivity.class);
        intent.putExtra("id", note.getId());
        startActivity(intent);
        return true;
    }
}
