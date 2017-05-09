package io.erfan.llogger.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import io.erfan.llogger.R;
import io.erfan.llogger.model.Drive;

public class DriveDetailActivity extends AppCompatActivity {
    Drive mDrive;
    TextView mViewDuration;
    TextView mViewDistance;
    TextView mViewLigth;
    TextView mViewTraffic;
    TextView mViewWeather;
    TextView mViewVehicle;
    TextView mViewSupervisor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drive_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mViewDuration = (TextView) findViewById(R.id.detail_duration);
        mViewDistance = (TextView) findViewById(R.id.detail_distance);
        mViewLigth = (TextView) findViewById(R.id.detail_light);
        mViewTraffic = (TextView) findViewById(R.id.detail_traffic);
        mViewWeather = (TextView) findViewById(R.id.detail_weather);
        mViewVehicle = (TextView) findViewById(R.id.detail_vehicle);
        mViewSupervisor = (TextView) findViewById(R.id.detail_supervisor);


        Intent intent = getIntent();
        mDrive = intent.getParcelableExtra("Drive");

        mViewDuration.setText(mDrive.getFormattedDuration());
        mViewVehicle.setText(mDrive.getCar());
        mViewSupervisor.setText(mDrive.getSupervisor());
        mViewLigth.setText(mDrive.getLightString());
        mViewTraffic.setText(mDrive.getTrafficString());
        mViewWeather.setText(mDrive.getWeatherString());
    }
}
