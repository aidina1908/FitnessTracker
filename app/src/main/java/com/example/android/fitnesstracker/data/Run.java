package com.example.android.fitnesstracker.data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity (tableName = "run_table")
public class Run {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo
    private String length;
    private String  speed;
    private long time;


    public Run(String length, String speed, long time) {
        this.length = length;
        this.speed = speed;
        this.time = time;

    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getLength() {
        return length;
    }

    public String getSpeed() {
        return speed;
    }

    public long getTime() {
        return time;
    }
}

