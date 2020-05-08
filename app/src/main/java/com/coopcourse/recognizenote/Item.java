package com.coopcourse.recognizenote;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
interface ItemDao {   // Data Access Object
    @Query("SELECT * FROM Item")
    List<RecViewItemTable> getItems();

    @Query("SELECT * FROM Item LIMIT 1 OFFSET :position")
    RecViewItemTable getItem(Integer position);

    @Query("SELECT COUNT(*) FROM Item")
    Integer itemCount();

    @Query("DELETE FROM Item")
    void clearTable();

    @Insert
    void insertItem(RecViewItemTable item);

    @Update
    void updateItem(RecViewItemTable item);

    @Delete
    void deleteItem(RecViewItemTable item);
}
