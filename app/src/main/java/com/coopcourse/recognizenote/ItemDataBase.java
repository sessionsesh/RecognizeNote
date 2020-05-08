package com.coopcourse.recognizenote;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {RecViewItem.class}, version = 1)
public abstract class ItemDataBase extends RoomDatabase {
    public abstract ItemDAO itemDAO();
}
