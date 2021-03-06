package com.jica.android.scratch;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
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
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.jica.android.scratch.db.NoteViewModel;
import com.jica.android.scratch.db.entity.Note;

import java.io.File;
import java.io.FileInputStream;
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
    @BindView(R.id.view_activity)
    ConstraintLayout view_activity;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                                picture.setVisibility(View.VISIBLE);
                                Glide.with(ViewActivity.this)
                                        .load(new File(getFilesDir(),filename))
                                        .into(picture);
                                /*
                                try {
                                    FileInputStream inputStream = openFileInput(filename);
                                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                                    picture.setImageBitmap(bitmap);
                                    inputStream.close();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }*/
                            }
                        }
                    }
                });
            } else {
                Log.d("ViewActivity", "id does not exist");
                finish();
            }
        } else {
            Log.d("ViewActivity", "id does not exist");
            finish();
        }

        // long click to edit
        title.setOnLongClickListener(this);
        contents.setOnLongClickListener(this);
        picture.setOnLongClickListener(this);
    }

    @Override
    public boolean onLongClick(View view) {
        Intent intent = new Intent(this, EditActivity.class);
        intent.putExtra("id", note.getId());
        startActivity(intent);
        return true;
    }
}
