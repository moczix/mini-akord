package com.mini.akord.activities.akord_activity;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.EditText;

import com.mini.akord.R;
import com.mini.akord.db.DbInstance;
import com.mini.akord.db.Employee;
import com.mini.akord.db.EmployeeWithHarvests;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.List;

@EBean
public class EditorHandler {

    @RootContext
    Context context;

    @Bean
    DbInstance dbInstance;

    @ViewById(R.id.employeeEdit)
    EditText employeeEdit;

    private TalkEditorHandlerWithActivity talkEditorHandlerWithActivity;

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

    public void registerTalkCallback(TalkEditorHandlerWithActivity cb) {
        talkEditorHandlerWithActivity = cb;
    }

    void initView() {
        employeeEdit.setText(akordListDataRef.get(akordListDataIndex).employee.getName());
    }

    @Click(R.id.editStateSaveBtn)
    void editStateSaveBtnClicked() {
        akordListDataRef.get(akordListDataIndex).employee.setName(employeeEdit.getText().toString());
        updateEmployee(akordListDataRef.get(akordListDataIndex).employee);
    }

    @Click(R.id.editStateDeleteBtn)
    void editStateDeleteBtnClicked() {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertDialog dialog;
        builder.setTitle("Usuwanie");
        builder.setMessage("Napewno chcesz usunąć tego pracownika?");

        builder.setPositiveButton("Zapisz", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteEmployee(akordListDataRef.get(akordListDataIndex).employee);
            }
        });
        builder.setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();




    }

    @Click(R.id.editStateCancelBtn)
    @UiThread(propagation = UiThread.Propagation.REUSE)
    void editStateCancelBtnClicked() {
        akordListAdapterRef.notifyDataSetChanged();
        talkEditorHandlerWithActivity.switchToStep1ViewFromEditor();
    }


    @Background
    void updateEmployee(Employee employee) {
        dbInstance.provideEmployeeDao().update(employee);
        editStateCancelBtnClicked();
    }

    @Background
    void deleteEmployee(Employee employee) {
        dbInstance.provideEmployeeDao().delete(employee);
        dbInstance.provideHarvetDao().deleteHarvestsByEmployee(employee.getId());
        akordListDataRef.remove(akordListDataIndex);
        editStateCancelBtnClicked();
    }



}
