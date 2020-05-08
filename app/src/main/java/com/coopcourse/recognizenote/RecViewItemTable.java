package com.coopcourse.recognizenote;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "Item")
public class RecViewItemTable {
    @PrimaryKey(autoGenerate = true)
    int id;
    @ColumnInfo(name = "text")
    String text;
    @ColumnInfo(name = "dateTime")
    String dateTime;

    RecViewItemTable(int id, String text, String dateTime) {
        this.id = id;
        this.text = text;
        this.dateTime = dateTime;
    }

    @Ignore
    RecViewItemTable(String text, String dateTime) {
        this.text = text;
        this.dateTime = dateTime;
    }
}


