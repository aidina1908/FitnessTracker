package com.example.android.fitnesstracker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.android.fitnesstracker.data.RunDao;
import com.example.android.fitnesstracker.data.RunDatabase;

public class HistoryFragment extends Fragment {
    private TextView chronometertime;
    private TextView editTextlength;
    private TextView textViewspeed;
    public RunDatabase runDatabase;
    public RunDao runDao;

    public String speed;
    public String lenght;
    public long time;
    View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.list_item, container, false);
        chronometertime = (TextView) view.findViewById(R.id.time_history);
        editTextlength = (TextView) view.findViewById(R.id.length_history);
        textViewspeed = (TextView)view.findViewById(R.id.speed_history);
        return view;
    }
}
