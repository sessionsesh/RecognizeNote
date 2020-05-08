package com.coopcourse.recognizenote;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;

import java.util.Calendar;
import java.util.Date;

@Entity(tableName = "Item")
public class RecViewItemTable {
    @PrimaryKey(autoGenerate = true)
    int id;
    @ColumnInfo(name = "text")
    String text;
    @ColumnInfo(name = "dateTime")
    Date dateTime;

    RecViewItemTable(int id, String text) {
        this.id = id;
        this.text = text;
        this.dateTime = Calendar.getInstance().getTime();
    }

    @Ignore
    RecViewItemTable(String text) {
        this.text = text;
        this.dateTime = Calendar.getInstance().getTime();
    }
}

class Converters{
    @TypeConverter
    public static Date fromTimeStamp(Long value){
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

}


