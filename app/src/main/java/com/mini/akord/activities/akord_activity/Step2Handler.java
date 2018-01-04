package com.mini.akord.activities.akord_activity;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mini.akord.AppPrefs_;
import com.mini.akord.R;
import com.mini.akord.db.DbInstance;
import com.mini.akord.db.EmployeeWithHarvests;
import com.mini.akord.db.Harvest;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.Date;
import java.util.List;

@EBean
public class Step2Handler {
    @RootContext
    Context context;

    @Bean
    DbInstance dbInstance;

    @Pref
    AppPrefs_ appPrefs;

    @ViewById(R.id.priceSummary)
    EditText priceSummary;

    @ViewById(R.id.amountHarvest)
    EditText amountHarvest;

    @ViewById(R.id.employeeSummary)
    TextView employeeSummary;

    @ViewById(R.id.harvestTodaySummary)
    TextView harvestTodaySummary;

    @ViewById(R.id.weightSumarry)
    EditText weightSummary;

    @ViewById(R.id.saveHarvestBtn)
    Button saveHarvestBtn;

    private TalkStep2HandlerWithActivity talkStep2HandlerWithActivity;

    private AkordListAdapter akordListAdapterRef;
    private List<EmployeeWithHarvests> akordListDataRef;

    private int akordListDataIndex;

    public void setAkordListAdapterRef(AkordListAdapter akordListAdapterRef) {
        this.akordListAdapterRef = akordListAdapterRef;
    }

    public void setAkordListDataRef(List<EmployeeWithHarvests> akordListDataRef) {
        this.akordListDataRef = akordListDataRef;
    }

    public void setAkordListDataIndex(int akordListDataIndex) {
        this.akordListDataIndex = akordListDataIndex;
    }

    public void registerTalkCallback(TalkStep2HandlerWithActivity cb) {
        talkStep2HandlerWithActivity = cb;
    }

    @AfterViews
    void init() {
        priceSummary.setText(appPrefs.price().get());
        weightSummary.setText(appPrefs.weight().get());
        amountHarvest.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                saveHarvestBtn.setEnabled(true);
            }
        });
    }

    void afterSwitch(String priceSummaryVal, String weightSummaryVal) {
        amountHarvest.requestFocus();
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(amountHarvest, InputMethodManager.SHOW_IMPLICIT);
        amountHarvest.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    saveHarvest();
                }
                return false;
            }
        });

        priceSummary.setText(priceSummaryVal);
        weightSummary.setText(weightSummaryVal);
        employeeSummary.setText(akordListDataRef.get(akordListDataIndex).employee.getName());
        harvestTodaySummary.setText(String.valueOf(akordListDataRef.get(akordListDataIndex).harvests.size()+1));
    }


    @Click(R.id.saveHarvestBtn)
    void saveHarvestBtnClicked() {
        saveHarvest();
    }

    private void saveHarvest() {
        Harvest harvest = new Harvest();
        harvest.setAmount(Integer.valueOf(amountHarvest.getText().toString()));
        harvest.setCost(Double.parseDouble(priceSummary.getText().toString()));
        harvest.setWeight(Double.parseDouble(weightSummary.getText().toString()));
        harvest.setEmployeeId(akordListDataRef.get(akordListDataIndex).employee.getId());
        harvest.setHarvestAt(new Date());
        Log.i("test", harvest.log());
        proceesHardestToDb(harvest);
    }

    @Background
    void proceesHardestToDb(Harvest harvest) {
        dbInstance.provideHarvetDao().insertAll(harvest);
        clearView();
    }

    @UiThread(propagation = UiThread.Propagation.REUSE)
    void clearView() {
        akordListDataRef.get(akordListDataIndex).harvests.add(new Harvest());
        akordListAdapterRef.disableSelected();
        akordListAdapterRef.notifyDataSetChanged();
        amountHarvest.setText("");
        Toast.makeText(context, "Zapisano", Toast.LENGTH_SHORT).show();
        talkStep2HandlerWithActivity.switchToStep1View();
    }

    @Click(R.id.backToStep1)
    void backToStep1Clicked() {
        amountHarvest.setText("");
        talkStep2HandlerWithActivity.switchToStep1View();
    }



}
