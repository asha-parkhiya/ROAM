package com.sparkle.roam;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;


@Database(entities = {LoginData.class}, version = 2, exportSchema = false)

public abstract class LoginRoomDatabase extends RoomDatabase {

    public abstract LoginDataDao loginDataDao();

    public static final String DATABASE_NAME = "RoamAgent-database";

    private static volatile LoginRoomDatabase INSTANCE;

    public static LoginRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (LoginRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = buildDatabase(context.getApplicationContext());
                }
            }
        }
        return INSTANCE;
    }

    private static LoginRoomDatabase buildDatabase(final Context context) {
        return Room.databaseBuilder(context, LoginRoomDatabase.class, DATABASE_NAME).fallbackToDestructiveMigration().build();
    }
}


