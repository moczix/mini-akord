package com.mini.akord.db;

import android.arch.persistence.room.Embedded;

/**
 * Created by moczniak on 02.01.2018.
 */

public class EmployeeWithAmount {

    @Embedded
    public Employee employee;

    public int amountAll;
    public int amountAtDate;
}
