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
public interface HarvestDao {

    @Query("SELECT * FROM harvests")
    List<Harvest> getAll();

    @Query("SELECT * FROM harvests WHERE harvest_at = :date")
    List<Harvest> getAllByDate(String date);

    @Query("SELECT * FROM harvests WHERE employee_id = :employeeId AND harvest_at = :date")
    List<Harvest> getEmployeeHarvestByDate(int employeeId, String date);

    @Insert
    void insertAll(Harvest... harvests);

    @Delete
    void delete(Harvest harvests);

    @Query("DELETE FROM harvests WHERE employee_id = :employeeId")
    void deleteHarvestsByEmployee(int employeeId);


    @Query("SELECT SUM(amount) FROM harvests WHERE employee_id = :employeeId")
    int getSumAmountAllTimeByEmployee(int employeeId);


    @Query("SELECT SUM(amount) FROM harvests WHERE employee_id = :employeeId AND harvest_at = :date")
    int getSumAmountAtDateByEmployee(int employeeId, String date);
}
