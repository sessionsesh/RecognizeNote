package com.coopcourse.recognizenote.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {RecViewItemTable.class}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class ItemDataBase extends RoomDatabase {
    public abstract ItemDao itemDao();
}
