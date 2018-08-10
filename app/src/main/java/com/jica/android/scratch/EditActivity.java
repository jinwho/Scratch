package com.jica.android.scratch;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.jica.android.scratch.db.NoteViewModel;
import com.jica.android.scratch.db.entity.Note;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditActivity extends AppCompatActivity {

    // Request Code
    private static final int SELECT_FROM_GALLERY = 1;

    private NoteViewModel noteViewModel;
    private Note note;

    private boolean isUpdateMode;
    private boolean isImageChanged;
    private Uri imageUri;

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

                            //만약 사진이 있다면 보여준다.
                            String filename = observer_note.getFilename();
                            if (filename != null) {
                                imageUri = Uri.fromFile(new File(getFilesDir(), filename));
                                if (imageUri != null) {
                                    setImage();
                                }
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
            case R.id.menu_url:
                // Set up the input
                final EditText input = new EditText(this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);

                new AlertDialog.Builder(this)
                        .setTitle(R.string.from_url)
                        .setView(input)
                        .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String url = input.getText().toString();
                                // check if it's valid url
                                if (!URLUtil.isValidUrl(url)){
                                    Toast.makeText(EditActivity.this, R.string.warning_wrong_url, Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                imageUri = Uri.parse(url);
                                if (imageUri != null) {
                                    isImageChanged = true;
                                    setImage();
                                }
                            }
                        }).create().show();
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


        // if new image set from uri(from URL or from sd card)
        if (isImageChanged) {

            // if old file exist then delete
            if (note.getFilename() != null) {
                deleteFile(note.getFilename());
            }

            // generate file data
            String filename = now.getTime() + ".png";
            note.setFilename(filename);
            Bitmap bitmap = ((BitmapDrawable) picture.getDrawable()).getBitmap();

            // TODO better way to save file?
            try {
                FileOutputStream outputStream = openFileOutput(filename, MODE_PRIVATE);
                //FileWriter fileWriter;
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

            imageUri = data.getData();
            if (imageUri != null) {
                isImageChanged = true;
                setImage();
            }
        }
    }


    // from imageUri
    private void setImage() {
        picture.setVisibility(View.VISIBLE);
        Glide.with(this).load(imageUri).into(picture);
    }
}