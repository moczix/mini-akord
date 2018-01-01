package com.rolnik.akord.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by moczniak on 31.12.2017.
 */

@Dao
public interface EmployeeDao {
    @Query("SELECT * FROM employees")
    List<Employee> getAll();

    @Query("SELECT * FROM employees WHERE id IN (:employeeIds)")
    List<Employee> loadAllByIds(int[] employeeIds);

    @Query("SELECT * FROM employees WHERE id = :id")
    Employee findById(int id);

    @Query("SELECT * FROM employees WHERE name = :name")
    Employee findByName(String name);


    /*
    @Query("SELECT * FROM employees WHERE harvestAt = :date")
    List<EmployeeWithHarvests> loadEmployeesWithHarvestsByDate(Date date);
*/

    @Insert
    long[] insertAll(Employee... employees);

    @Delete
    void delete(Employee employees);
}