package io.erfan.llogger.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.erfan.llogger.App;
import io.erfan.llogger.Preference;
import io.erfan.llogger.R;
import io.erfan.llogger.model.Car;
import io.erfan.llogger.model.CarDao;
import io.erfan.llogger.model.DaoSession;
import io.erfan.llogger.model.Drive;
import io.erfan.llogger.model.Supervisor;
import io.erfan.llogger.model.SupervisorDao;

public class NewDriveActivity extends AppCompatActivity {
    private Spinner mCar;
    private Spinner mSupervisor;
    private TextInputLayout mOdometer;

    private List<Supervisor> mSupervisors;
    private List<Car> mCars;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_drive);
        Toolbar toolbar = (Toolbar) findViewById(R.id.new_drive_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mCar = (Spinner) findViewById(R.id.new_drive_car);
        mSupervisor = (Spinner) findViewById(R.id.new_drive_supervisor);
        mOdometer = (TextInputLayout) findViewById(R.id.new_drive_odometer);

        // get a list of all supervisors and cars
        DaoSession daoSession = ((App) getApplication()).getDaoSession();
        SupervisorDao supervisorDao = daoSession.getSupervisorDao();
        CarDao carDao = daoSession.getCarDao();
        mSupervisors = supervisorDao.loadAll();
        mCars = carDao.loadAll();

        // add a default if none exist in the database (unlikely but again doesn't hurt to be too careful)
        if (mSupervisors.isEmpty()) {
            // add a supervisor with dummy values to the database
            Supervisor supervisor = new Supervisor();
            supervisor.setName("Default Supervisor");
            supervisor.setLicenceNo("000");
            supervisorDao.insert(supervisor);

            // update the list
            mSupervisors = supervisorDao.loadAll();
        }
        if (mCars.isEmpty()) {
            // add a car with dummy values to the database
            Car car = new Car();
            car.setName("Default Car");
            car.setPlateNo("000");
            carDao.insert(car);

            // update the list
            mCars = carDao.loadAll();
        }

        // get a list of cars and supervisor's names
        List<String> carNames = new ArrayList<>();
        for (Car car : mCars) {
            carNames.add(car.getName());
        }
        List<String> supervisorNames = new ArrayList<>();
        for (Supervisor supervisor : mSupervisors) {
            supervisorNames.add(supervisor.getName());
        }

        // add an adapter to the cars spinner and supervisors spinner (using the list of names as the options)
        ArrayAdapter<String> carDataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, carNames);
        carDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCar.setAdapter(carDataAdapter);
        ArrayAdapter<String> supervisorDataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, supervisorNames);
        supervisorDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSupervisor.setAdapter(supervisorDataAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.start_drive_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start();
            }
        });
        mOdometer.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // the go action on the keyboard (instead of return key) will start the drive
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    start();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finishAfterTransition();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void start() {
        Intent intent = new Intent(this, DrivingActivity.class);

        // get the selected car and supervisor
        Car car = mCars.get(mCar.getSelectedItemPosition());
        Supervisor supervisor = mSupervisors.get(mSupervisor.getSelectedItemPosition());

        // convert odometer to int
        String odometerString = ((TextInputLayout) findViewById(R.id.new_drive_odometer))
                .getEditText().getText().toString();
        int odometer = Integer.valueOf(odometerString);

        // set car and supervisor
        Drive drive = new Drive();
        drive.setCarId(car.getId());
        drive.setSupervisorId(supervisor.getId());
        drive.setOdometer(odometer);


        // set driver as the current user
        Preference prefMan = new Preference(this);
        drive.setDriverId(prefMan.getUser());

        intent.putExtra("Drive", drive);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finishAfterTransition();
    }
}
