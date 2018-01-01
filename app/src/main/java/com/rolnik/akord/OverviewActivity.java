package com.rolnik.akord;

import android.support.v7.app.AppCompatActivity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;


@EActivity(R.layout.activity_overview)
public class OverviewActivity extends AppCompatActivity {

    @AfterViews
    void init() {
        getSupportActionBar().setTitle("Przegląd");
    }

}
