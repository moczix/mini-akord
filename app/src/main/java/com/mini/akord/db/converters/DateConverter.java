package com.mini.akord.db.converters;

/**
 * Created by moczniak on 31.12.2017.
 */

import android.arch.persistence.room.TypeConverter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateConverter {
    public static DateFormat dfPattern = new SimpleDateFormat("yyyy-MM-dd");

    @TypeConverter
    public static Date fromTimestamp(String value) {
        if (value != null) {
            try {
                return dfPattern.parse(value);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return null;
        } else {
            return null;
        }
    }

    @TypeConverter
    public static String dateToTimestamp(Date value) {

        return value == null ? null : dfPattern.format(value);
    }
}