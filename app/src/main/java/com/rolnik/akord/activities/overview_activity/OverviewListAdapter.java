package com.rolnik.akord.activities.overview_activity;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.rolnik.akord.db.EmployeeWithAmount;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.util.ArrayList;
import java.util.List;

@EBean
public class OverviewListAdapter extends BaseAdapter {

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
