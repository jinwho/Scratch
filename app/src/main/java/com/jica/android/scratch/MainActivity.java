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
import android.widget.Toast;


import com.jica.android.scratch.adapter.NoteRecyclerViewAdapter;
import com.jica.android.scratch.db.NoteViewModel;
import com.jica.android.scratch.db.entity.SmallNote;

import java.util.List;

public class MainActivity extends AppCompatActivity implements NoteRecyclerViewAdapter.NoteAdapterCallback {

    private NoteViewModel noteViewModel;

    static {
       AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        noteViewModel = ViewModelProviders.of(this).get(NoteViewModel.class);

        // set recyclerView and Adapter
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        final NoteRecyclerViewAdapter noteRecyclerViewAdapter = new NoteRecyclerViewAdapter(this);
        recyclerView.setAdapter(noteRecyclerViewAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

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


    }

    @Override
    public void deleteCallback(int pos) {
        noteViewModel.delete(pos);
    }

    @Override
    public void viewCallback(int id) {
        Intent intent = new Intent(this, ViewActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }
}
