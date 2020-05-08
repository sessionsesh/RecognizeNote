package com.coopcourse.recognizenote;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {RecViewItemTable.class}, version = 1, exportSchema = false)
public abstract class ItemDataBase extends RoomDatabase {
    public abstract ItemDao itemDao();
}
