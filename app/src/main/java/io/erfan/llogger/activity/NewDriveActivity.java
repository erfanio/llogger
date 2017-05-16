package io.erfan.llogger.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import io.erfan.llogger.R;
import io.erfan.llogger.model.Drive;

public class NewDriveActivity extends AppCompatActivity {
    TextInputLayout mVehicle;
    TextInputLayout mOdometer;
    TextInputLayout mSupervisor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_drive);
        Toolbar toolbar = (Toolbar) findViewById(R.id.new_drive_toolbar);
        setSupportActionBar(toolbar);

        mVehicle = (TextInputLayout) findViewById(R.id.new_drive_vehicle);
        mOdometer = (TextInputLayout) findViewById(R.id.new_drive_odometer);
        mSupervisor = (TextInputLayout) findViewById(R.id.new_drive_supervisor);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.start_drive_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), DrivingActivity.class);

                Drive drive = new Drive();
                drive.car = mVehicle.getEditText().getText().toString();
                drive.supervisor = mSupervisor.getEditText().getText().toString();

                intent.putExtra("Drive", drive);
                startActivity(intent);
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
