package com.mini.akord;

import org.androidannotations.annotations.sharedpreferences.DefaultString;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

/**
 * Created by moczniak on 01.01.2018.
 */

@SharedPref(value = SharedPref.Scope.APPLICATION_DEFAULT)
public interface AppPrefs {

    @DefaultString("MiniAkord")
    String excelFileName();


    @DefaultString("1")
    String price();

    @DefaultString("1")
    String weight();

}
