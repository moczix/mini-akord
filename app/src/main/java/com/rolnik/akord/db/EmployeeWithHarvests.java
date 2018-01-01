package com.rolnik.akord.db;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;

import java.util.List;

/**
 * Created by moczniak on 31.12.2017.
 */

public class EmployeeWithHarvests {
    @Embedded
    public Employee employee;

    @Relation(parentColumn = "id", entityColumn = "employee_id", entity =   Harvest.class)
    public List<Harvest> harvests;
}
