package com.jica.android.scratch;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


import com.jica.android.scratch.db.NoteViewModel;
import com.jica.android.scratch.db.entity.Note;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class ViewActivity extends AppCompatActivity {

    private Note note;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        NoteViewModel noteViewModel = ViewModelProviders.of(this).get(NoteViewModel.class);

        // ui
        final TextView title = findViewById(R.id.title);
        final TextView contents = findViewById(R.id.contents);
        contents.setMovementMethod(ScrollingMovementMethod.getInstance());
        final TextView created = findViewById(R.id.created);
        final TextView modified = findViewById(R.id.modified);

        //getNote view data
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            if (extras.containsKey("id")) {
                Integer id = extras.getInt("id");
                noteViewModel.getNote(id).observe(this, new Observer<Note>() {
                    @Override
                    public void onChanged(@Nullable Note note) {
                        //set ui
                        if (note != null) {
                            ViewActivity.this.note = note;
                            title.setText(note.getTitle());
                            contents.setText(note.getContents());

                            //set print format
                            SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd / hh:mm:ss", Locale.getDefault());
                            String createdText = format.format(note.getCreated());
                            String modifiedText = format.format(note.getModified());

                            created.setText(createdText);
                            modified.setText(modifiedText);
                        }
                    }
                });
            }
        }else {
            Log.d("View", "must need id ");
            finish();
        }


        FloatingActionButton fab_edit = findViewById(R.id.fab_edit);
        fab_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = getApplicationContext();
                Intent intent = new Intent(context, EditActivity.class);
                intent.putExtra("id", note.getId());
                startActivity(intent);
            }
        });
    }
}
