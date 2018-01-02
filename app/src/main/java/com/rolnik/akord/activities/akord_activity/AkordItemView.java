package com.rolnik.akord.activities.akord_activity;

import android.content.Context;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rolnik.akord.R;
import com.rolnik.akord.db.EmployeeWithHarvests;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

@EViewGroup(R.layout.activity_akord_list)
public class AkordItemView extends LinearLayout {

    @ViewById(R.id.title)
    TextView title;

    @ViewById(R.id.harvestToday)
    TextView harvestToday;

    @ViewById(R.id.checked)
    ImageView checked;

    public AkordItemView(Context context) {
        super(context);
    }

    public void bind(EmployeeWithHarvests el, int currentIndex, int selectedIndex, boolean selectedInit) {
        title.setText(el.employee.getName());
        if (selectedInit) {
            int visible = currentIndex == selectedIndex ? VISIBLE : INVISIBLE;
            checked.setVisibility(visible);
        }
        harvestToday.setText(String.valueOf(el.harvests.size()));
    }
}
