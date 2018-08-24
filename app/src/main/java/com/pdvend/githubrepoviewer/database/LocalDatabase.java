package com.pdvend.githubrepoviewer.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.pdvend.githubrepoviewer.model.Issue;

/**
 * Local database for caching results.
 */
@Database(entities = {Issue.class}, version = 1, exportSchema = false)
public abstract class LocalDatabase extends RoomDatabase {

    public abstract IssueDao issueDao();

    private static LocalDatabase INSTANCE;

    public static LocalDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (LocalDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            LocalDatabase.class, "database")
                            .build();

                }
            }
        }
        return INSTANCE;
    }
}
