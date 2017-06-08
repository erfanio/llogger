package io.erfan.llogger.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.List;

import io.erfan.llogger.App;
import io.erfan.llogger.R;
import io.erfan.llogger.model.DaoSession;
import io.erfan.llogger.model.Drive;
import io.erfan.llogger.model.DriveDao;

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
        mViewDistance.setText(mDrive.getFormattedDistance());
//        mViewVehicle.setText(mDrive.car);
//        mViewSupervisor.setText(mDrive.supervisor);
        mViewLigth.setText(mDrive.getLightString());
        mViewTraffic.setText(mDrive.getTrafficString());
        mViewWeather.setText(mDrive.getWeatherString());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.detail_map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                boolean first = true;
                for (String encodedPath : mDrive.getPath()) {
                    List<LatLng> path = PolyUtil.decode(encodedPath);
                    if (path.size() > 0) {
                        googleMap.addPolyline(
                                new PolylineOptions()
                                        .addAll(path)
                        );

                        if (first) {
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(path.get(0), 15));
                            first = false;
                        }
                    }
                }
            }
        });
    }
}
