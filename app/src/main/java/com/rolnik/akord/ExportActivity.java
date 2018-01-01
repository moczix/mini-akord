package com.rolnik.akord;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.rolnik.akord.db.DbInstance;
import com.rolnik.akord.db.Employee;
import com.rolnik.akord.db.EmployeeWithHarvests;
import com.rolnik.akord.db.converters.DateConverter;
import com.rolnik.akord.excel.Excel;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@EActivity(R.layout.activity_export)
public class ExportActivity extends AppCompatActivity {



    @Bean
    DbInstance dbInstance;



    List<EmployeeWithHarvests> employeeWithHarvestsList = new ArrayList<>();

    private GoogleSignInClient mGoogleSignInClient;
    private DriveResourceClient mDriveResourceClient;


    private static final int REQUEST_CODE_SIGN_IN = 0;

    @AfterViews
    void init() {
        getSupportActionBar().setTitle("Eksport");

        saveExcel();
    }


    @UiThread(propagation = UiThread.Propagation.REUSE)
    void googleSignIn() {
        mGoogleSignInClient = buildGoogleSignInClient();
        startActivityForResult(mGoogleSignInClient.getSignInIntent(), REQUEST_CODE_SIGN_IN);
    }

    GoogleSignInClient buildGoogleSignInClient() {
        GoogleSignInOptions signInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestScopes(Drive.SCOPE_FILE)
                        .build();
        return GoogleSignIn.getClient(this, signInOptions);
    }




    void saveFileToDrive() {
        final Task<DriveFolder> rootFolderTask = mDriveResourceClient .getRootFolder();
        final Task<DriveContents> createContentsTask = mDriveResourceClient .createContents();
        Tasks.whenAll(rootFolderTask, createContentsTask)
                .continueWithTask(new Continuation<Void, Task<DriveFile>>() {
                    @Override
                    public Task<DriveFile> then(@NonNull Task<Void> task) throws Exception {
                        DriveFolder parent = rootFolderTask.getResult();
                        DriveContents contents = createContentsTask.getResult();

                        Excel excel = new Excel();
                        excel.prepareExcelInMemory(employeeWithHarvestsList, contents.getOutputStream());

                        MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                .setTitle("HelloWorld_"+new Date().getTime()+".xls")
                                .setMimeType("application/vnd.ms-excel")
                                .setStarred(true)
                                .build();

                        return mDriveResourceClient .createFile(parent, changeSet, contents);
                    }
                })
                .addOnSuccessListener(this,
                        new OnSuccessListener<DriveFile>() {
                            @Override
                            public void onSuccess(DriveFile driveFile) {
                                Log.i("test", "sukces: " + driveFile.getDriveId().encodeToString());
                            }
                        })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("test", "Unable to create file", e);
                    }
                });
    }


    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SIGN_IN && resultCode == RESULT_OK) {
            //mDriveClient = Drive.getDriveClient(this, GoogleSignIn.getLastSignedInAccount(this));
            mDriveResourceClient = Drive.getDriveResourceClient(this, GoogleSignIn.getLastSignedInAccount(this));
            saveFileToDrive();
        }
    }


    @Background
    void saveExcel() {
        List<Employee> allEmployees = dbInstance.provideEmployeeDao().getAll();
        String today = DateConverter.dfPattern.format(new Date());

        for (Employee employee : allEmployees) {
            EmployeeWithHarvests listEl = new EmployeeWithHarvests();
            listEl.employee = employee;
            listEl.harvests = dbInstance.provideHarvetDao().getEmployeeHarvestByDate(employee.getId(), today);
            employeeWithHarvestsList.add(listEl);
        }
        googleSignIn();

    }

}
