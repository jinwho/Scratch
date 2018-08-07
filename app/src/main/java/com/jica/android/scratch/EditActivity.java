package com.jica.android.scratch;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.widget.Toast;


import com.jica.android.scratch.db.NoteViewModel;
import com.jica.android.scratch.db.entity.Note;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditActivity extends AppCompatActivity {

    // Request Code
    private static final int SELECT_FROM_GALLERY = 1;

    private NoteViewModel noteViewModel;
    private Note note;

    private boolean isUpdateMode;

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
                                File file = new File(getFilesDir(), filename);
                                if (file.exists()) {
                                    picture.setImageBitmap(BitmapFactory.decodeFile(file.getPath()));
                                    picture.setVisibility(View.VISIBLE);
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
        // TODO
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menu_save:
                saveNote();
                finish();
                return true;
            case R.id.menu_gallery :
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(intent, SELECT_FROM_GALLERY);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void saveNote(){
        note.setTitle(title.getText().toString());
        note.setContents(contents.getText().toString());

        //getNote current date
        Date now = new Date();

        //TODO 사진을 가져왔다면
        String filename;
        if (isUpdateMode) {
            // 업데이트 모드일 경우 기존의 파일이름
            filename = note.getFilename();
            note.setModified(now);
            noteViewModel.update(note);
        } else {
            // 업데이트 모드가 아닐 경우 파일 이름 생성
            filename = now.getTime()+".png";
            note.setFilename(filename);
            note.setCreated(now);
            note.setModified(now);
            noteViewModel.insert(note);
        }
        //사진 가져왔을 때 url을 저장해놓고 지금 저장하자.
        // if picture exist save to file (with filename)
        // filename.isEmpty();
    }

    //class fileAsynctask extends AsyncTask <File,Void,Void>{}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_FROM_GALLERY && resultCode == RESULT_OK) {
            //사진을 가져와 보여준다, 사진은 전역변수로 기억했다가 필요에 따라 저장한다.
            try {
                final Uri imageUri = data.getData();
                //out of memory error
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 8;

                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream,null,options);
                picture.setImageBitmap(selectedImage);
                picture.setVisibility(View.VISIBLE);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(this, "You haven't picked Image",Toast.LENGTH_LONG).show();
        }
    }

    private void setPiecture(String filepath){

    }
}