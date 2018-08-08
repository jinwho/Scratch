package com.jica.android.scratch;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.jica.android.scratch.db.NoteViewModel;
import com.jica.android.scratch.db.entity.Note;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditActivity extends AppCompatActivity {

    // Request Code
    private static final int SELECT_FROM_GALLERY = 1;

    private NoteViewModel noteViewModel;
    private Note note;

    private boolean isUpdateMode;
    private boolean isPictureSet;

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.contents)
    TextView contents;
    @BindView(R.id.picture)
    ImageView picture;

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

                            //만약 사진이 있다면 보여준다. // better way? TODO
                            String filename = observer_note.getFilename();
                            if (filename != null) {
                                File file = new File(getFilesDir(), filename);
                                picture.setVisibility(View.VISIBLE);
                                Glide.with(EditActivity.this)
                                        .load(file)
                                        .apply(RequestOptions.skipMemoryCacheOf(true))
                                        .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                                        .into(picture);
                            }
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
            case R.id.menu_gallery:
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(intent, SELECT_FROM_GALLERY);
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

        // TODO how to save original photo from gallery, also GIF support
        // TODO maybe use : class fileAsyncTask extends AsyncTask <File,Void,Void>{}
        // TODO dealing with Glide caches!!
        // save file when picture set is true
        if (isPictureSet) {

            // 파일 이름 설정
            String filename = note.getFilename();
            if (filename == null) {
                filename = now.getTime() + ".png";
                note.setFilename(filename);
            }

            // 비트맵
            picture.invalidate();
            BitmapDrawable drawable = (BitmapDrawable) picture.getDrawable();
            Bitmap bitmap = drawable.getBitmap();

            // 파일 저장
            try {
                FileOutputStream outputStream = openFileOutput(filename, MODE_PRIVATE);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_FROM_GALLERY && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            if (imageUri != null) {
                isPictureSet = true;
                picture.setVisibility(View.VISIBLE);
                Glide.with(this)
                        .load(imageUri)
                        .into(picture);
            }
        }
    }
}