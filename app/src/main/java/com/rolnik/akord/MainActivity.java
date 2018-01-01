package com.rolnik.akord;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import com.rolnik.akord.db.DbInstance;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;


@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {

    @Bean
    DbInstance dbInstance;

    @AfterViews
    void init() {
        dbInstance.init();
    }

    @Click
    void startBtn() {
        Intent intent = new Intent(this, AkordActivity_.class);
        startActivity(intent);
    }

    @Click
    void overviewBtn() {
        Intent intent = new Intent(this, OverviewActivity_.class);
        startActivity(intent);
    }

    @Click
    void exportBtn() {
        Intent intent = new Intent(this, ExportActivity_.class);
        startActivity(intent);
    }

    @Click
    void settingsBtn() {
        Intent intent = new Intent(this, SettingsActivity_.class);
        startActivity(intent);
    }


}
