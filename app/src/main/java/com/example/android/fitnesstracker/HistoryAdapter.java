package com.example.android.fitnesstracker;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.android.fitnesstracker.data.Run;
import com.example.android.fitnesstracker.data.RunDatabase;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {
    private List<Run> runs;
    private RunDatabase runDatabase;
    private OnItemListener mOnItemListener;
    private static final String LOG_TAG = "ItemAdapter";


    public HistoryAdapter (List<Run> runs , OnItemListener onItemListener , RunDatabase runDatabase , Context context )
    {
        this.runs = runs;
        this.mOnItemListener = onItemListener;
        this.runDatabase = runDatabase;

    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return runs != null ? runs.size() : 0;
    }

    public class HistoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView speed;
        public Chronometer time;
        public EditText length;

        OnItemListener onItemListener;

        public HistoryViewHolder(View view, OnItemListener onItemListener) {

            super(view);
            speed = view.findViewById(R.id.speed_history);
            time = view.findViewById(R.id.time_history);
            length = view.findViewById(R.id.length_history);


            this.onItemListener = onItemListener;

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }

    public interface OnItemListener{
        void  onItemClick(int i);
    }
}
