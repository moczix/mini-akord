package com.mini.akord.activities.akord_activity;

import android.support.constraint.ConstraintLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.mini.akord.R;
import com.mini.akord.db.EmployeeWithHarvests;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;


@EActivity(R.layout.activity_akord)
public class AkordActivity extends AppCompatActivity implements TalkStep1HandlerWithActivity, TalkStep2HandlerWithActivity, TalkEditorHandlerWithActivity {


    @Bean
    Step1Handler step1Handler;

    @Bean
    Step2Handler step2Handler;

    @Bean
    EditorHandler editorHandler;





    @ViewById(R.id.step1)
    ConstraintLayout step1Layout;

    @ViewById(R.id.step2)
    ConstraintLayout step2Layout;

    @ViewById(R.id.stepEdit)
    ConstraintLayout stepEdit;

    @ViewById(R.id.employeeList)
    ListView employeeList;

    @Bean
    AkordListAdapter akordListAdapter;


    private boolean editState = false;

    private List<EmployeeWithHarvests> akordListData = new ArrayList<>();
    private int akordListDataIndex;



    @AfterViews
    void init() {
        getSupportActionBar().setTitle("Akord");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        employeeList.setAdapter(akordListAdapter);
        step1Handler.registerTalkCallback(this);
        step1Handler.setAkordListAdapterRef(akordListAdapter);
        step1Handler.setAkordListDataRef(akordListData);
        step1Handler.initDataSet();

        step2Handler.setAkordListAdapterRef(akordListAdapter);
        step2Handler.setAkordListDataRef(akordListData);
        step2Handler.registerTalkCallback(this);


        editorHandler.setAkordListAdapterRef(akordListAdapter);
        editorHandler.setAkordListDataRef(akordListData);
        editorHandler.registerTalkCallback(this);
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

    @ItemClick(R.id.employeeList)
    void employeeListItemClicked(int index) {
        akordListDataIndex = index;
        if (step1Handler.isEditState() == false) {
            akordListAdapter.setSelected(index);
            akordListAdapter.notifyDataSetChanged();
            step1Handler.enableNextBtn();
            step2Handler.setAkordListDataIndex(akordListDataIndex);
        }else {
            Log.i("test", "powinno sie zmienic!");
            stepEdit.setVisibility(View.VISIBLE);
            step1Layout.setVisibility(View.INVISIBLE);
            editorHandler.setAkordListDataIndex(akordListDataIndex);
            editorHandler.initView();
        }
        Log.i("test", "editState: " + String.valueOf(editState));
    }


    @Override
    public void switchToStep2View() {
        step2Layout.setVisibility(View.VISIBLE);
        step1Layout.setVisibility(View.INVISIBLE);

        step2Handler.afterSwitch(step1Handler.getPriceEditText(), step1Handler.getWeightEditText());
    }

    @Override
    public void switchToStep1View() {
        step1Layout.setVisibility(View.VISIBLE);
        step2Layout.setVisibility(View.INVISIBLE);
    }

    @Override
    public void switchToStep1ViewFromEditor() {
        stepEdit.setVisibility(View.INVISIBLE);
        step1Layout.setVisibility(View.VISIBLE);
    }
}


