package com.rolnik.akord;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.rolnik.akord.db.DbInstance;
import com.rolnik.akord.db.Employee;
import com.rolnik.akord.db.EmployeeWithAmount;
import com.rolnik.akord.db.converters.DateConverter;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.RootContext;
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
        overviewList.setAdapter(overviewListAdapter);
        fetchData();
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
            Log.i("test", String.valueOf(listEl.amountAll));
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




@EViewGroup(R.layout.activity_overview_list)
class OverviewItemView extends LinearLayout {

    @ViewById(R.id.title)
    TextView title;

    @ViewById(R.id.harvestAll)
    TextView harvestAll;

    @ViewById(R.id.harvestAtDate)
    TextView harvestAtDate;

    public OverviewItemView(Context context) {
        super(context);
    }

    public void bind(EmployeeWithAmount el) {
        title.setText(el.employee.getName());
        harvestAll.setText(String.valueOf(el.amountAll));
        harvestAtDate.setText(String.valueOf(el.amountAtDate));
    }
}

@EBean
class OverviewListAdapter extends BaseAdapter {

    List<EmployeeWithAmount> mDataSet;

    @RootContext
    Context context;


    @AfterInject
    void initAdapter() {
        mDataSet = new ArrayList<>();
    }

    public void setData (List<EmployeeWithAmount> list) {
        mDataSet = list;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        OverviewItemView itemView;
        if (convertView == null) {
            itemView = OverviewItemView_.build(context);
        } else {
            itemView = (OverviewItemView) convertView;
        }

        itemView.bind(getItem(position));

        return  itemView;
    }

    @Override
    public int getCount() {
        return mDataSet.size();
    }

    @Override
    public EmployeeWithAmount getItem(int position) {
        return mDataSet.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}