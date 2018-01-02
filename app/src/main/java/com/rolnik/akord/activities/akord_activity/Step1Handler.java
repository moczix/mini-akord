package com.rolnik.akord.activities.akord_activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.rolnik.akord.AppPrefs_;
import com.rolnik.akord.R;
import com.rolnik.akord.db.DbInstance;
import com.rolnik.akord.db.Employee;
import com.rolnik.akord.db.EmployeeWithHarvests;
import com.rolnik.akord.db.converters.DateConverter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@EBean
public class Step1Handler {
    @RootContext
    Context context;

    @Bean
    DbInstance dbInstance;

    @Pref
    AppPrefs_ appPrefs;

    @ViewById(R.id.price)
    EditText price;

    @ViewById(R.id.weight)
    EditText weight;

    @ViewById(R.id.nextBtn)
    Button nextBtn;

    @ViewById(R.id.editEmployeePromptBtn)
    FloatingActionButton editEmployeePromptBtn;

    private boolean editState = false;

    private TalkStep1HandlerWithActivity talkStep1HandlerWithActivity;

    private AkordListAdapter akordListAdapterRef;
    private List<EmployeeWithHarvests> akordListDataRef;

    public void registerTalkCallback(TalkStep1HandlerWithActivity cb) {
        talkStep1HandlerWithActivity = cb;
    }

    public void setAkordListAdapterRef(AkordListAdapter akordListAdapterRef) {
        this.akordListAdapterRef = akordListAdapterRef;
    }

    public void setAkordListDataRef(List<EmployeeWithHarvests> akordListDataRef) {
        this.akordListDataRef = akordListDataRef;
    }

    public String getPriceEditText() {
        return price.getText().toString();
    }

    public String getWeightEditText() {
        return weight.getText().toString();
    }

    public boolean isEditState() {
        return editState;
    }

    @AfterViews
    void init() {
        price.setText(appPrefs.price().get());
        weight.setText(appPrefs.weight().get());
    }

    void enableNextBtn() {
        nextBtn.setEnabled(true);
    }

    @Background
    void initDataSet() {
        List<Employee> allEmployees = dbInstance.provideEmployeeDao().getAll();
        String today = DateConverter.dfPattern.format(new Date());

        for (Employee employee : allEmployees) {
            EmployeeWithHarvests listEl = new EmployeeWithHarvests();
            listEl.employee = employee;
            listEl.harvests = dbInstance.provideHarvetDao().getEmployeeHarvestByDate(employee.getId(), today);
            akordListDataRef.add(listEl);
        }

        setDataInAdapter();
    }

    @UiThread(propagation = UiThread.Propagation.REUSE)
    void setDataInAdapter() {
        akordListAdapterRef.setData(akordListDataRef);
        akordListAdapterRef.notifyDataSetChanged();
    }

    @Click(R.id.nextBtn)
    void nextBtnClicked() {
        talkStep1HandlerWithActivity.switchToStep2View();
    }


    @Click(R.id.addEmployeePromptBtn)
    void addEmployeePromptBtnClicked() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertDialog dialog;
        builder.setTitle("Nowy Pracownik");
        builder.setMessage("Podaj nazwę/id pracownika");

        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton("Zapisz", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = input.getText().toString();
                proceedEmployeeToDb(name);
            }
        });
        builder.setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();
    }

    @Background
    void proceedEmployeeToDb(String name) {
        if(dbInstance.provideEmployeeDao().countByName(name) == 0) {
            Employee employee = new Employee();
            employee.setName(name);
            long[] ids = dbInstance.provideEmployeeDao().insertAll(employee);
            addEmployeeToList(dbInstance.provideEmployeeDao().findById((int) ids[0]));
        }else {
            showEmployeeExistErr();
        }
    }

    @UiThread(propagation = UiThread.Propagation.REUSE)
    void addEmployeeToList(Employee employee) {
        EmployeeWithHarvests listEl = new EmployeeWithHarvests();
        listEl.employee = employee;
        listEl.harvests = new ArrayList<>();
        akordListDataRef.add(listEl);
        akordListAdapterRef.notifyDataSetChanged();
    }

    @UiThread(propagation = UiThread.Propagation.REUSE)
    void showEmployeeExistErr() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertDialog dialog;
        builder.setTitle("Błąd");
        builder.setMessage("Istnieje już pracownik o tej nazwie/id");
        dialog = builder.create();
        dialog.show();
    }


    @Click(R.id.editEmployeePromptBtn)
    void editEmployeePromptBtnClicked() {
        editState = !editState;
        if (!editState) {
            editEmployeePromptBtn.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.colorPrimary)));
            editEmployeePromptBtn.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_create_white_24dp));
        }else {
            Toast.makeText(context, "Aby edytować kliknij na pracownika", Toast.LENGTH_SHORT).show();
            editEmployeePromptBtn.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.colorAccent)));
            editEmployeePromptBtn.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_clear_white_24dp));
        }

    }

}
