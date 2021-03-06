package com.mini.akord.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

/**
 * Created by moczniak on 31.12.2017.
 */

@Database(entities = {Employee.class, Harvest.class}, version = 6)
abstract class AppDatabase extends RoomDatabase {
    public abstract EmployeeDao employeeDao();
    public abstract HarvestDao harvestDao();
}

