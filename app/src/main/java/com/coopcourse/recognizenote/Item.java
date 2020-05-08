package com.coopcourse.recognizenote;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
interface ItemDAO {   // Data Access Object
    @Query("SELECT * FROM Item")
    List<RecViewItem> getItems();

    @Query("SELECT * FROM Item LIMIT 1 OFFSET :position")
    RecViewItem getItem(Integer position);

    @Query("SELECT COUNT(*) FROM Item")
    Integer itemCount();

    @Insert
    void insertItem(RecViewItem item);

    @Update
    void updateItem(RecViewItem item);

    @Delete
    void deleteItem(RecViewItem item);
}
