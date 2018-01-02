package com.rolnik.akord;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.rolnik.akord.db.DbInstance;
import com.rolnik.akord.db.Employee;
import com.rolnik.akord.db.EmployeeWithHarvests;
import com.rolnik.akord.db.Harvest;
import com.rolnik.akord.db.converters.DateConverter;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@EActivity(R.layout.activity_akord)
public class AkordActivity extends AppCompatActivity {

    @Pref
    AppPrefs_ appPrefs;

    @Bean
    DbInstance dbInstance;

    @ViewById(R.id.price)
    EditText price;

    @ViewById(R.id.weight)
    EditText weight;


    @ViewById(R.id.step1)
    ConstraintLayout step1Layout;

    @ViewById(R.id.step2)
    ConstraintLayout step2Layout;


    @ViewById(R.id.employeeList)
    ListView employeeList;

    @Bean
    EmployeeListAdapter employeeListAdapter;


    @ViewById(R.id.nextBtn)
    Button nextBtn;

    @ViewById(R.id.saveHarvestBtn)
    Button saveHarvestBtn;


    @ViewById(R.id.priceSummary)
    EditText priceSummary;

    @ViewById(R.id.weightSumarry)
    EditText weightSummary;

    @ViewById(R.id.amountHarvest)
    EditText amountHarvest;

    @ViewById(R.id.employeeSummary)
    TextView employeeSummary;

    @ViewById(R.id.harvestTodaySummary)
    TextView harvestTodaySummary;


    @ViewById(R.id.stepEdit)
    ConstraintLayout stepEdit;

    @ViewById(R.id.employeeEdit)
    EditText employeeEdit;

    @ViewById(R.id.editEmployeePromptBtn)
    FloatingActionButton editEmployeePromptBtn;

    private boolean editState = false;

    private List<EmployeeWithHarvests> employeeWithHarvestsList = new ArrayList<>();
    private int employeeWithHarvestIndex;

    private Harvest harvest;


    @AfterViews
    void init() {
        getSupportActionBar().setTitle("Akord");

        priceSummary.setText(appPrefs.price().get());
        weightSummary.setText(appPrefs.weight().get());

        price.setText(appPrefs.price().get());
        weight.setText(appPrefs.weight().get());


        this.inputListener();
        this.getEmployees();
    }

    @AfterViews
    void bindAdapter() {
        employeeList.setAdapter(employeeListAdapter);
    }


    void inputListener() {
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

    @Background
    void getEmployees() {
        List<Employee> allEmployees = dbInstance.provideEmployeeDao().getAll();
        String today = DateConverter.dfPattern.format(new Date());

        for (Employee employee : allEmployees) {
            EmployeeWithHarvests listEl = new EmployeeWithHarvests();
            listEl.employee = employee;
            listEl.harvests = dbInstance.provideHarvetDao().getEmployeeHarvestByDate(employee.getId(), today);
            employeeWithHarvestsList.add(listEl);
        }

        showEmployees(employeeWithHarvestsList);
    }

    @UiThread(propagation = UiThread.Propagation.REUSE)
    void addEmployeeToList(Employee employee) {
        EmployeeWithHarvests listEl = new EmployeeWithHarvests();
        listEl.employee = employee;
        listEl.harvests = new ArrayList<>();
        employeeWithHarvestsList.add(listEl);
        employeeListAdapter.notifyDataSetChanged();
    }

    @UiThread(propagation = UiThread.Propagation.REUSE)
    void showEmployees(List<EmployeeWithHarvests> allEmployeesWithHarvests) {
        employeeListAdapter.setData(allEmployeesWithHarvests);
        employeeListAdapter.notifyDataSetChanged();
    }


    @Click(R.id.nextBtn)
    void nextBtnClicked() {
        nextBtn.setVisibility(View.INVISIBLE);
        saveHarvestBtn.setVisibility(View.VISIBLE);
        step2Layout.setVisibility(View.VISIBLE);
        step1Layout.setVisibility(View.INVISIBLE);
        amountHarvest.requestFocus();

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(amountHarvest, InputMethodManager.SHOW_IMPLICIT);

        amountHarvest.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    saveHarvestBtnClicked();
                }
                return false;
            }
        });

        priceSummary.setText(price.getText().toString());
        weightSummary.setText(weight.getText().toString());
        employeeSummary.setText(employeeWithHarvestsList.get(employeeWithHarvestIndex).employee.getName());
        harvestTodaySummary.setText(String.valueOf(employeeWithHarvestsList.get(employeeWithHarvestIndex).harvests.size()+1));

    }


    @Click(R.id.saveHarvestBtn)
    void saveHarvestBtnClicked() {
        harvest = new Harvest();
        harvest.setAmount(Integer.valueOf(amountHarvest.getText().toString()));
        harvest.setCost(Double.parseDouble(priceSummary.getText().toString()));
        harvest.setWeight(Double.parseDouble(weightSummary.getText().toString()));
        harvest.setEmployeeId(employeeWithHarvestsList.get(employeeWithHarvestIndex).employee.getId());
        harvest.setHarvestAt(new Date());
        Log.i("test", harvest.log());
        saveHarvestToDb();
    }

    @Background
    void saveHarvestToDb() {
        dbInstance.provideHarvetDao().insertAll(harvest);
        clearView();
    }

    @UiThread(propagation = UiThread.Propagation.REUSE)
    void clearView() {
        Toast.makeText(this, "Zapisano", Toast.LENGTH_SHORT).show();
        saveHarvestBtn.setVisibility(View.INVISIBLE);
        nextBtn.setVisibility(View.VISIBLE);
        step1Layout.setVisibility(View.VISIBLE);
        step2Layout.setVisibility(View.INVISIBLE);

        amountHarvest.setText("");

        employeeWithHarvestsList.get(employeeWithHarvestIndex).harvests.add(new Harvest());
        employeeListAdapter.disableSelected();
        employeeListAdapter.notifyDataSetChanged();
        //employeeWithHarvestIndex = 0;
    }


    @Click(R.id.addEmployeePromptBtn)
    void addEmployeePromptBtnClicked() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog dialog;
        builder.setTitle("Nowy Pracownik");
        builder.setMessage("Podaj nazwę/id pracownika");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton("Zapisz", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = input.getText().toString();
                addEmployeeToDb(name);
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

    @Background()
    void addEmployeeToDb(String name) {
        Employee employee = new Employee();
        employee.setName(name);
        long[] ids = dbInstance.provideEmployeeDao().insertAll(employee);

        addEmployeeToList(dbInstance.provideEmployeeDao().findById((int) ids[0]));

    }

    @ItemClick(R.id.employeeList)
    void employeeListItemClicked(int index) {
        employeeWithHarvestIndex = index;
        if (editState == false) {
            employeeListAdapter.setSelected(index);
            employeeListAdapter.notifyDataSetChanged();
            nextBtn.setEnabled(true);
        }else {
            stepEdit.setVisibility(View.VISIBLE);
            step1Layout.setVisibility(View.INVISIBLE);
            employeeEdit.setText(employeeWithHarvestsList.get(employeeWithHarvestIndex).employee.getName());
            nextBtn.setVisibility(View.INVISIBLE);
        }
    }

    @Click(R.id.editStateCancelBtn)
    @UiThread(propagation = UiThread.Propagation.REUSE)
    void editStateCancelBtnClicked() {
        stepEdit.setVisibility(View.INVISIBLE);
        step1Layout.setVisibility(View.VISIBLE);
        nextBtn.setVisibility(View.VISIBLE);
        employeeListAdapter.notifyDataSetChanged();
    }

    @Click(R.id.editStateSaveBtn)
    void editStateSaveBtnClicked() {
        employeeWithHarvestsList.get(employeeWithHarvestIndex).employee.setName(employeeEdit.getText().toString());
        updateEmployee(employeeWithHarvestsList.get(employeeWithHarvestIndex).employee);
    }

    @Click(R.id.editStateDeleteBtn)
    void editStateDeleteBtnClicked() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog dialog;
        builder.setTitle("Usuwanie");
        builder.setMessage("Napewno chcesz usunąć tego pracownika?");

        builder.setPositiveButton("Zapisz", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteEmployee(employeeWithHarvestsList.get(employeeWithHarvestIndex).employee);
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


    @Background
    void updateEmployee(Employee employee) {
        dbInstance.provideEmployeeDao().update(employee);
        editStateCancelBtnClicked();
    }

    @Background
    void deleteEmployee(Employee employee) {
        dbInstance.provideEmployeeDao().delete(employee);
        dbInstance.provideHarvetDao().deleteHarvestsByEmployee(employee.getId());
        employeeWithHarvestsList.remove(employeeWithHarvestIndex);
        editStateCancelBtnClicked();
    }

    @Click(R.id.editEmployeePromptBtn)
    void editEmployeePromptBtnClicked() {

        editState = !editState;
        if (!editState) {

            editEmployeePromptBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
            editEmployeePromptBtn.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_create_white_24dp));
        }else {
            Toast.makeText(this, "Aby edytować kliknij na pracownika", Toast.LENGTH_SHORT).show();
            editEmployeePromptBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
            editEmployeePromptBtn.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_clear_white_24dp));
        }


    }


}


@EViewGroup(R.layout.activity_akord_employee_list)
class EmployeeItemView extends LinearLayout {

    @ViewById(R.id.title)
    TextView title;

    @ViewById(R.id.harvestToday)
    TextView harvestToday;

    @ViewById(R.id.checked)
    ImageView checked;

    public EmployeeItemView(Context context) {
        super(context);
    }

    public void bind(EmployeeWithHarvests employee, int currentIndex, int selectedIndex, boolean selectedInit) {
        title.setText(employee.employee.getName());
        if (selectedInit) {
            int visible = currentIndex == selectedIndex ? VISIBLE : INVISIBLE;
            checked.setVisibility(visible);
        }
        harvestToday.setText(String.valueOf(employee.harvests.size()));
    }
}

@EBean
class EmployeeListAdapter extends BaseAdapter {

    List<EmployeeWithHarvests> employeesWithHarvests;

    @RootContext
    Context context;

    int selectedIndex;

    boolean selectedInit = false;

    @AfterInject
    void initAdapter() {
        employeesWithHarvests = new ArrayList<EmployeeWithHarvests>();
    }

    public void setData (List<EmployeeWithHarvests> employees) {
        this.employeesWithHarvests = employees;
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

        EmployeeItemView employeeItemView;
        if (convertView == null) {
            employeeItemView = EmployeeItemView_.build(context);
        } else {
            employeeItemView = (EmployeeItemView) convertView;
        }

        employeeItemView.bind(getItem(position), position, selectedIndex, selectedInit);

        return  employeeItemView;
    }

    @Override
    public int getCount() {
        return employeesWithHarvests.size();
    }

    @Override
    public EmployeeWithHarvests getItem(int position) {
        return employeesWithHarvests.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}