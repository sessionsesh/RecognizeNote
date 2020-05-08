package com.coopcourse.recognizenote;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "Item")
public class RecViewItem {
    @PrimaryKey(autoGenerate = true)
    public int id;
    @ColumnInfo(name = "text")
    public String text;
    @ColumnInfo(name = "dateTime")
    public String dateTime;

    public RecViewItem(int id, String text, String dateTime) {
        this.id = id;
        this.text = text;
        this.dateTime = dateTime;
    }

    @Ignore
    public RecViewItem(String text, String dateTime) {
        this.text = text;
        this.dateTime = dateTime;

    }
}


