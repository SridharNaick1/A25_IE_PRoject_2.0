package com.example.bottomnavigation;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Record.class}, version = 2, exportSchema = false)
public abstract class RecordDatabase extends RoomDatabase {

    public abstract RecordDao recordDao();

    private static volatile RecordDatabase INSTANCE;

    static RecordDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (RecordDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), RecordDatabase.class, "record_database").build();
                }
            }
        }
        return INSTANCE;
    }


}
