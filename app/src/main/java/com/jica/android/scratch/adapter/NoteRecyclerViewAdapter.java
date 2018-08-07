package com.jica.android.scratch.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.jica.android.scratch.R;
import com.jica.android.scratch.db.entity.SmallNote;

import java.util.List;

public class NoteRecyclerViewAdapter extends RecyclerView.Adapter<NoteRecyclerViewAdapter.NoteViewHolder> {

    private final LayoutInflater mInflater;
    private List<SmallNote> smallNotes; // Cached copy of words
    private NoteAdapterCallback noteAdapterCallback;

    class NoteViewHolder extends RecyclerView.ViewHolder{
        private final TextView title;
        private NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    noteAdapterCallback.viewCallback(smallNotes.get(getAdapterPosition()).getId());
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    noteAdapterCallback.deleteCallback(smallNotes.get(getAdapterPosition()).getId());
                    return true;
                }
            });
        }
    }
    public interface NoteAdapterCallback {
        void deleteCallback(int pos);
        void viewCallback(int id);
    }

    public NoteRecyclerViewAdapter(Context context) {
        this.noteAdapterCallback = ((NoteAdapterCallback) context);
        this.mInflater = LayoutInflater.from(context);
    }

    public void setSmallNotes(List<SmallNote> smallNotes){
        this.smallNotes = smallNotes;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.recyclerview_row, parent, false);
        return new NoteViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, final int position) {
        if (smallNotes != null) {
            final SmallNote smallNote = smallNotes.get(position);
            holder.title.setText(smallNote.getTitle());
        }
    }

    @Override
    public int getItemCount() {
        if (smallNotes != null)
            return smallNotes.size();
        else return 0;
    }
}
