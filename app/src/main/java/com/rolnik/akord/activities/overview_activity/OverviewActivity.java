package com.rolnik.akord.activities.overview_activity;

import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ListView;

import com.rolnik.akord.R;
import com.rolnik.akord.db.DbInstance;
import com.rolnik.akord.db.Employee;
import com.rolnik.akord.db.EmployeeWithAmount;
import com.rolnik.akord.db.converters.DateConverter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@EActivity(R.layout.activity_overview)
public class OverviewActivity extends AppCompatActivity {

    @Bean
    DbInstance dbInstance;

    @Bean
    OverviewListAdapter overviewListAdapter;

    @ViewById(R.id.overviewList)
    ListView overviewList;

    List<EmployeeWithAmount> mDataSet = new ArrayList<>();


    @AfterViews
    void init() {
        getSupportActionBar().setTitle("Przegląd");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        overviewList.setAdapter(overviewListAdapter);
        fetchData();
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


    @Background
    void fetchData() {
        List<Employee> allEmployees = dbInstance.provideEmployeeDao().getAllOrderByName();
        String today = DateConverter.dfPattern.format(new Date());

        for (Employee employee : allEmployees) {
            EmployeeWithAmount listEl = new EmployeeWithAmount();
            listEl.employee = employee;
            listEl.amountAll = dbInstance.provideHarvetDao().getSumAmountAllTimeByEmployee(employee.getId());
            listEl.amountAtDate = dbInstance.provideHarvetDao().getSumAmountAtDateByEmployee(employee.getId(), today);
            mDataSet.add(listEl);
        }

        initView();
    }

    @UiThread(propagation = UiThread.Propagation.REUSE)
    void initView() {
        overviewListAdapter.setData(mDataSet);
        overviewListAdapter.notifyDataSetChanged();
    }

}


