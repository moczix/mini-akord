package com.rolnik.akord;

import org.androidannotations.annotations.sharedpreferences.DefaultInt;
import org.androidannotations.annotations.sharedpreferences.DefaultString;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

/**
 * Created by moczniak on 01.01.2018.
 */

@SharedPref(value = SharedPref.Scope.APPLICATION_DEFAULT)
public interface AppPrefs {

    @DefaultString("RolniAkord")
    String excelFileName();


    @DefaultInt(1)
    int price();

    @DefaultInt(1)
    int weight();

}
