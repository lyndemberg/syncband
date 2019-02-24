package com.mesh.syncband.database.converters;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;

public class RoomTypeConverters {

    @TypeConverter
    public static Date fromTimestamp(Long stamp){
        if(stamp == null)
            return null;
        else
            return new Date(stamp);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date){
        if(date == null)
            return null;
        else
            return date.getTime();
    }

}
