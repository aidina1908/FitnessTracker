package com.example.android.fitnesstracker.data;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Run.class}, version = 2)
public abstract class RunDatabase extends RoomDatabase {

    private static RunDatabase instance;

    public abstract RunDao runDao();

        public static RunDatabase getInstance(Context context) {
            if (instance == null) {
                instance = Room.databaseBuilder(context.getApplicationContext(),
                        RunDatabase.class, "inventory_database")
                        .allowMainThreadQueries()
                        .fallbackToDestructiveMigration()
                        .addCallback(roomCallback)
                        .build();
            }
            return instance;

        }

        private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback(){
            @Override
            public void onCreate(@NonNull SupportSQLiteDatabase db) {
                super.onCreate(db);
            }
        };

        public static void destroyInstance(){
            instance = null;
        }
    }
