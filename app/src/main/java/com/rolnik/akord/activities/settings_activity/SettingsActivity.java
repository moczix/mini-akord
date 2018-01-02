package com.rolnik.akord.activities.settings_activity;

import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.rolnik.akord.AppPrefs_;
import com.rolnik.akord.R;

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
        getSupportActionBar().setTitle("Ustawienia");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        excelFileNameEditText.setText(appPrefs.excelFileName().get());
        priceSettingsEditText.setText(appPrefs.price().get().toString());
        weightSettingsEditText.setText(appPrefs.weight().get().toString());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Click(R.id.saveBtn)
    void SaveBtnClicked() {
        appPrefs.edit()
                .excelFileName().put(excelFileNameEditText.getText().toString())
                .price().put(priceSettingsEditText.getText().toString())
                .weight().put(weightSettingsEditText.getText().toString())
                .apply();

        Toast.makeText(this, "Zapisano", Toast.LENGTH_SHORT).show();
    }

}