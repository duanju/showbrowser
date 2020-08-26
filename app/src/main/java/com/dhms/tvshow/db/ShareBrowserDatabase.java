package com.dhms.tvshow.db;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.dhms.tvshow.History;

@Database(entities = {History.class}, version = 1)
public abstract class ShareBrowserDatabase extends RoomDatabase {
    public abstract HistoryDao userDao();

    private static final Object LOCKER = new Object();
    private static volatile ShareBrowserDatabase sDBInstance;

    public static ShareBrowserDatabase createDB(Context applicationContext) {
        if (null == sDBInstance) {
            synchronized (LOCKER) {
                if (null == sDBInstance) {
                    sDBInstance = Room
                            .databaseBuilder(applicationContext.getApplicationContext()
                                    , ShareBrowserDatabase.class, "share-browser")
                            .build();
                }
            }
        }

        return sDBInstance;
    }
}