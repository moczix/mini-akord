package com.mini.akord.db;

import android.arch.persistence.room.Room;
import android.content.Context;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

/**
 * Created by moczniak on 31.12.2017.
 */

@EBean(scope = EBean.Scope.Singleton)
public class DbInstance {

    @RootContext
    Context context;



    private AppDatabase instance;

    public void init() {
        instance = Room.databaseBuilder(context.getApplicationContext(),
                AppDatabase.class, "database").build();
    }

    public EmployeeDao provideEmployeeDao() {
        return instance.employeeDao();
    }

    public HarvestDao provideHarvetDao() {
        return instance.harvestDao();
    }

}
