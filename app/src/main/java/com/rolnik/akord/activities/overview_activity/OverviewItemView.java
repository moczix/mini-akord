package com.rolnik.akord.activities.overview_activity;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rolnik.akord.R;
import com.rolnik.akord.db.EmployeeWithAmount;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

@EViewGroup(R.layout.activity_overview_list)
public class OverviewItemView extends LinearLayout {

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
