package io.erfan.llogger.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import io.erfan.llogger.App;
import io.erfan.llogger.R;
import io.erfan.llogger.model.DaoSession;
import io.erfan.llogger.model.Drive;
import io.erfan.llogger.model.DriveDao;

public class PostDriveActivity extends AppCompatActivity {
    FloatingActionButton mFab;
    TextView mDuration;
    TextView mDistance;

    Drive mDrive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_drive);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        mDrive = intent.getParcelableExtra("Drive");

        mDuration = (TextView) findViewById(R.id.post_drive_duration);
        mDuration.setText(mDrive.getFormattedDuration());

        mDistance = (TextView) findViewById(R.id.post_drive_distance);
        mDistance.setText(mDrive.getFormattedDistance());

        mFab = (FloatingActionButton) findViewById(R.id.post_drive_fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get the drive DAO
                DaoSession daoSession = ((App) getApplication()).getDaoSession();
                DriveDao driveDao = daoSession.getDriveDao();

                switch (((RadioGroup) findViewById(R.id.post_drive_light)).getCheckedRadioButtonId()) {
                    case R.id.post_drive_light_day:
                        mDrive.light = Drive.Light.DAY;
                        break;

                    case R.id.post_drive_light_night:
                        mDrive.light = Drive.Light.NIGHT;
                        break;

                    default:
                        mDrive.light = Drive.Light.DAY;
                }

                switch (((RadioGroup) findViewById(R.id.post_drive_weather)).getCheckedRadioButtonId()) {
                    case R.id.post_drive_weather_dry:
                        mDrive.weather = Drive.Weather.DRY;
                        break;

                    case R.id.post_drive_weather_wet:
                        mDrive.weather = Drive.Weather.WET;
                        break;

                    default:
                        mDrive.weather = Drive.Weather.DRY;
                }

                switch (((RadioGroup) findViewById(R.id.post_drive_traffic)).getCheckedRadioButtonId()) {
                    case R.id.post_drive_traffic_light:
                        mDrive.traffic = Drive.Traffic.LIGHT;
                        break;

                    case R.id.post_drive_traffic_medium:
                        mDrive.traffic = Drive.Traffic.MEDIUM;
                        break;

                    case R.id.post_drive_traffic_heavy:
                        mDrive.traffic = Drive.Traffic.HEAVY;
                        break;

                    default:
                        mDrive.traffic = Drive.Traffic.LIGHT;
                }

                driveDao.insert(mDrive);
                goHome();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.drive_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cancel_drive:
                goHome();
        }
        return true;
    }

    void goHome() {
        Intent intent = new Intent(this, RootActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
