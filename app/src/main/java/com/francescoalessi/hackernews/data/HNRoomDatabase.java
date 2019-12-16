package com.francescoalessi.hackernews.data;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Story.class, Comment.class}, version = 4, exportSchema = false)
public abstract class HNRoomDatabase extends RoomDatabase
{
    public abstract HNDao dao();

    private static volatile HNRoomDatabase INSTANCE;

    static HNRoomDatabase getDatabase(final Context context)
    {
        if (INSTANCE == null)
        {
            synchronized (HNRoomDatabase.class)
            {
                if (INSTANCE == null)
                {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            HNRoomDatabase.class, "hn_database").
                            fallbackToDestructiveMigration().build(); // TODO: remove destructive migration eventually
                }
            }
        }
        return INSTANCE;
    }
}
