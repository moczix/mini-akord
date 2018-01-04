package com.mini.akord.activities.akord_activity;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.mini.akord.db.EmployeeWithHarvests;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.util.ArrayList;
import java.util.List;

@EBean
public class AkordListAdapter extends BaseAdapter {

    List<EmployeeWithHarvests> mList;

    @RootContext
    Context context;

    int selectedIndex;

    boolean selectedInit = false;

    @AfterInject
    void initAdapter() {
        mList = new ArrayList<>();
    }

    public void setData (List<EmployeeWithHarvests> list) {
        mList = list;
    }

    public void disableSelected() {
        selectedInit = false;
    }

    public void setSelected(int index) {
        selectedIndex = index;
        selectedInit = true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        AkordItemView itemView;
        if (convertView == null) {
            itemView = AkordItemView_.build(context);
        } else {
            itemView = (AkordItemView) convertView;
        }

        itemView.bind(getItem(position), position, selectedIndex, selectedInit);

        return  itemView;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public EmployeeWithHarvests getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
