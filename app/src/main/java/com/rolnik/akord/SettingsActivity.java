package com.rolnik.akord;

import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;


@EActivity(R.layout.activity_settings)
public class SettingsActivity extends AppCompatActivity {


    @Pref
    AppPrefs_ appPrefs;

    @ViewById(R.id.excelFileName)
    EditText excelFileNameEditText;

    @ViewById(R.id.priceSettings)
    EditText priceSettingsEditText;

    @ViewById(R.id.weightSettings)
    EditText weightSettingsEditText;

    @AfterViews
    void init() {
        excelFileNameEditText.setText(appPrefs.excelFileName().get());
        priceSettingsEditText.setText(appPrefs.price().get().toString());
        weightSettingsEditText.setText(appPrefs.weight().get().toString());
    }

    @Click(R.id.saveBtn)
    void SaveBtnClicked() {
        appPrefs.edit()
                .excelFileName().put(excelFileNameEditText.getText().toString())
                .price().put(Integer.parseInt(priceSettingsEditText.getText().toString()))
                .weight().put(Integer.parseInt(weightSettingsEditText.getText().toString()))
                .apply();

        Toast.makeText(this, "Zapisano", Toast.LENGTH_SHORT).show();
    }

}