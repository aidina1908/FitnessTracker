package com.example.android.fitnesstracker.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface RunDao {

    @Insert
    void insert(Run run);

    @Update
    void update(Run run);

    @Delete
    void delete(Run run);

  //  @Query("DELETE FROM product_table WHERE id = :id")
    //void delete(long id);

    @Query("DELETE FROM run_table")
    void deleteAllRuns();

    @Query("SELECT * FROM run_table ORDER BY time DESC")
    LiveData<List<Run>> getAllRuns();
}

