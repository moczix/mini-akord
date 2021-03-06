package com.mini.akord.activities.main_activity;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.mini.akord.R;
import com.mini.akord.activities.akord_activity.AkordActivity_;
import com.mini.akord.activities.export_activity.ExportActivity_;
import com.mini.akord.activities.overview_activity.OverviewActivity_;
import com.mini.akord.activities.settings_activity.SettingsActivity_;
import com.mini.akord.db.DbInstance;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;



@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {

    @Bean
    DbInstance dbInstance;



    private String expirationDate = "2018-01-05";//yyyy-MM-dd
    private boolean isExpired = false;

    @AfterViews
    void init() {
        dbInstance.init();

        //checkExpiration();//demo off
        //showDemoAlert(); //demo off
    }

    void checkExpiration() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date strDate = null;
        try {
            strDate = sdf.parse(expirationDate);
            if (new Date().after(strDate)) {
                isExpired = true;
            }

        } catch (ParseException e) {
            isExpired = true;
            e.printStackTrace();
        }
    }

    void showDemoAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Wersja Demo")
                .setTitle("Wersja demo wygaśnie: " + expirationDate);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Click
    void startBtn() {
        if (!isExpired){
            Intent intent = new Intent(this, AkordActivity_.class);
            startActivity(intent);
        }
    }

    @Click
    void overviewBtn() {
        if (!isExpired) {
            Intent intent = new Intent(this, OverviewActivity_.class);
            startActivity(intent);
        }
    }

    @Click
    void exportBtn() {
        if (!isExpired) {
            Intent intent = new Intent(this, ExportActivity_.class);
            startActivity(intent);
        }
    }

    @Click
    void settingsBtn() {
        if (!isExpired) {
            Intent intent = new Intent(this, SettingsActivity_.class);
            startActivity(intent);
        }
    }


}
