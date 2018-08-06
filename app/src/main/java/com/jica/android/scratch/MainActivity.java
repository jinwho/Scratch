package com.jica.android.scratch;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;


import com.jica.android.scratch.adapter.NoteRecyclerViewAdapter;
import com.jica.android.scratch.db.NoteViewModel;
import com.jica.android.scratch.db.entity.SmallNote;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements NoteRecyclerViewAdapter.NoteAdapterCallback {

    private NoteViewModel noteViewModel;

    @BindView(R.id.night_mode)
    ImageView night_mode;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;

    // TODO
    static {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        noteViewModel = ViewModelProviders.of(this).get(NoteViewModel.class);
        ButterKnife.bind(this);


        // set recyclerView and Adapter
        final NoteRecyclerViewAdapter noteRecyclerViewAdapter = new NoteRecyclerViewAdapter(this);
        recyclerview.setAdapter(noteRecyclerViewAdapter);
        recyclerview.setHasFixedSize(true);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        recyclerview.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));


        noteViewModel.getSmallNotes().observe(this, new Observer<List<SmallNote>>() {
            @Override
            public void onChanged(@Nullable final List<SmallNote> smallNote) {
                // Update the cached copy of the words in the adapter.
                noteRecyclerViewAdapter.setSmallNotes(smallNote);
            }
        });

        FloatingActionButton fab_add = findViewById(R.id.fab_add);
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                startActivity(intent);
            }
        });

        night_mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*

                TODO
                sharedpreference 를 읽어서 nightmode 값을 읽어온다.
                if null MODE_NIGHT_AUTO
                if true set MODE_NIGHT_YES
                if false set MODE_NIGHT_NO

                TODO 2
                툴바에 있는 이미지를 클릭시 sharedpreference의 nightmod값을 바꾸고
                현재 액티비티를 recreate한다.

                TODO 3
                onDestroy 에서 sharedpreference의 값을 삭제 한다.

                */
            }
        });
    }


    @Override
    public void deleteCallback(int id) {
        noteViewModel.delete(id);
    }

    @Override
    public void viewCallback(int id) {
        Intent intent = new Intent(this, ViewActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }
}
