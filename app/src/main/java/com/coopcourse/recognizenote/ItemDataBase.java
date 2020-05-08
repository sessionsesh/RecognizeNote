package com.coopcourse.recognizenote;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

@Database(entities = {RecViewItemTable.class}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class ItemDataBase extends RoomDatabase {
    public abstract ItemDao itemDao();
}
