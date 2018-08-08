package com.jica.android.scratch;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.jica.android.scratch.db.NoteViewModel;
import com.jica.android.scratch.db.entity.Note;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditActivity extends AppCompatActivity {

    // Request Code
    private static final int SELECT_FROM_GALLERY = 1;

    private NoteViewModel noteViewModel;
    private Note note;

    private boolean isUpdateMode;
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
                            Uri noteUri = observer_note.getImageUri();
                            if (noteUri != null) {
                                picture.setVisibility(View.VISIBLE);
                                Glide.with(EditActivity.this)
                                        .load(noteUri)
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
            case R.id.menu_url:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Url 을 입력하세요.");

                // Set up the input
                final EditText input = new EditText(this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Uri uri =  Uri.parse( input.getText().toString() );
                        picture.setVisibility(View.VISIBLE);
                        imageUri = uri;
                        setImage();
                    }
                });
                builder.create().show();

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
        if (imageUri != null) {
            // 노트 Uri 설정
            note.setImageUri(imageUri);
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
                setImage();
            }
        }
    }

    private void setImage(){
        picture.setVisibility(View.VISIBLE);
        Glide.with(this)
                .load(imageUri)
                .into(picture);

    }
}