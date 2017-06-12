package io.erfan.llogger.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import java.util.List;

import io.erfan.llogger.App;
import io.erfan.llogger.R;
import io.erfan.llogger.model.DaoSession;
import io.erfan.llogger.model.Drive;
import io.erfan.llogger.model.DriveDao;

public class DriveDetailActivity extends AppCompatActivity {
    private Drive mDrive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drive_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView mViewDuration = (TextView) findViewById(R.id.detail_duration);
        TextView mViewDistance = (TextView) findViewById(R.id.detail_distance);
        TextView mViewWeatherEmoji = (TextView) findViewById(R.id.detail_weather_emoji);
        TextView mViewLightEmoji = (TextView) findViewById(R.id.detail_light_emoji);
        TextView mViewLight = (TextView) findViewById(R.id.detail_light);
        TextView mViewWeather = (TextView) findViewById(R.id.detail_weather);
        TextView mViewTraffic = (TextView) findViewById(R.id.detail_traffic);
        TextView mViewVehicle = (TextView) findViewById(R.id.detail_vehicle);
        TextView mViewSupervisor = (TextView) findViewById(R.id.detail_supervisor);


        DaoSession daoSession = ((App) getApplication()).getDaoSession();
        DriveDao driveDao = daoSession.getDriveDao();

        Intent intent = getIntent();
        Long driveId = intent.getLongExtra("DriveId", -1);
        if (driveId == -1) {
            Toast.makeText(this, "Unexpected error occurred", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        mDrive = driveDao.load(driveId);

        mViewDuration.setText(mDrive.getFormattedDuration());
        mViewDistance.setText(mDrive.getFormattedDistance());
        mViewVehicle.setText(mDrive.getCar().getName());
        mViewSupervisor.setText(mDrive.getSupervisor().getName());
        mViewLightEmoji.setText(mDrive.getLightEmojiRes());
        mViewWeatherEmoji.setText(mDrive.getWeatherEmojiRes());
        mViewLight.setText(mDrive.getLightStringRes());
        mViewWeather.setText(mDrive.getWeatherStringRes());
        mViewTraffic.setText(mDrive.getTrafficStringRes());

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finishAfterTransition();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
