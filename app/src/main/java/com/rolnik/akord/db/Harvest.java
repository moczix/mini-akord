package com.rolnik.akord.db;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.rolnik.akord.db.converters.DateConverter;

import java.util.Date;

/**
 * Created by moczniak on 31.12.2017.
 */

@Entity(tableName = "harvests")
public class Harvest {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "employee_id")
    private int employeeId;

    private int amount;
    private int cost;
    private int weight;

    @ColumnInfo(name = "harvest_at")
    @TypeConverters({DateConverter.class})
    private Date harvestAt;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public Date getHarvestAt() {
        return harvestAt;
    }

    public void setHarvestAt(Date harvestAt) {
        this.harvestAt = harvestAt;
    }


    public String log() {
        return "{id: " + this.id +
                ", amount: " + this.amount +
                ", price: " + this.cost +
                ", weight: " + this.weight +
                ", harvestAt: " + DateConverter.dfPattern.format(this.harvestAt) +
                "}";
    }
}
