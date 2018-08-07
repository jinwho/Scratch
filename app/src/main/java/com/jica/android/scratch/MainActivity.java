package com.jica.android.scratch;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;


import com.jica.android.scratch.adapter.NoteRecyclerViewAdapter;
import com.jica.android.scratch.db.NoteViewModel;
import com.jica.android.scratch.db.entity.SmallNote;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements NoteRecyclerViewAdapter.NoteAdapterCallback {

    private NoteViewModel noteViewModel;

    //for night mode
    private SharedPreferences sharedPref;
    private int currentMode;
    private int nextMode;

    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;

    //buttons
    @BindView(R.id.night_mode_btn)
    ImageView night_mode_btn;
    @BindView(R.id.add_btn)
    ImageButton add_btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        noteViewModel = ViewModelProviders.of(this).get(NoteViewModel.class);
        ButterKnife.bind(this);


        // set theme based preference
        sharedPref = getPreferences(MODE_PRIVATE);
        // key, default value
        currentMode = sharedPref.getInt("night_mode", AppCompatDelegate.MODE_NIGHT_AUTO);
        AppCompatDelegate.setDefaultNightMode(currentMode);

        switch (currentMode) {
            //나이트 모드 상태에 따라 이미지를 바꾼다.
            case AppCompatDelegate.MODE_NIGHT_AUTO:
                night_mode_btn.setImageResource(R.drawable.grey_logo);
                nextMode = AppCompatDelegate.MODE_NIGHT_NO;
                break;
            case AppCompatDelegate.MODE_NIGHT_NO:
                night_mode_btn.setImageResource(R.drawable.black_logo);
                nextMode = AppCompatDelegate.MODE_NIGHT_YES;
                break;
            case AppCompatDelegate.MODE_NIGHT_YES:
                night_mode_btn.setImageResource(R.drawable.white_logo);
                nextMode = AppCompatDelegate.MODE_NIGHT_AUTO;
                break;
        }


        // set recyclerView and Adapter
        final NoteRecyclerViewAdapter noteRecyclerViewAdapter = new NoteRecyclerViewAdapter(this);
        recyclerview.setAdapter(noteRecyclerViewAdapter);
        recyclerview.setHasFixedSize(true);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        //recyclerview.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));


        noteViewModel.getSmallNotes().observe(this, new Observer<List<SmallNote>>() {
            @Override
            public void onChanged(@Nullable final List<SmallNote> smallNote) {
                // Update the cached copy of the words in the adapter.
                noteRecyclerViewAdapter.setSmallNotes(smallNote);
            }
        });

        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                startActivity(intent);
            }
        });

        night_mode_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // if toolbar clicked change theme
                SharedPreferences.Editor editor;
                editor = sharedPref.edit();
                editor.putInt("night_mode", nextMode);
                editor.apply();
                recreate();
            }
        });
    }


    @Override
    public void deleteCallback(int id) {
        //지울 때 사진도 지워야 한다.
        noteViewModel.delete(id);
    }

    @Override
    public void viewCallback(int id) {
        Intent intent = new Intent(this, ViewActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }
}
