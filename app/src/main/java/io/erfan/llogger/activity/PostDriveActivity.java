package io.erfan.llogger.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import io.erfan.llogger.App;
import io.erfan.llogger.R;
import io.erfan.llogger.model.DaoSession;
import io.erfan.llogger.model.Drive;
import io.erfan.llogger.model.DriveDao;

public class PostDriveActivity extends AppCompatActivity {

    private Drive mDrive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_drive);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        mDrive = intent.getParcelableExtra("Drive");

        TextView mDuration = (TextView) findViewById(R.id.post_drive_duration);
        mDuration.setText(mDrive.getFormattedDuration());

        TextView mDistance = (TextView) findViewById(R.id.post_drive_distance);
        mDistance.setText(mDrive.getFormattedDistance());

        FloatingActionButton mFab = (FloatingActionButton) findViewById(R.id.post_drive_fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get the drive DAO
                DaoSession daoSession = ((App) getApplication()).getDaoSession();
                DriveDao driveDao = daoSession.getDriveDao();

                switch (((RadioGroup) findViewById(R.id.post_drive_light)).getCheckedRadioButtonId()) {
                    case R.id.post_drive_light_day:
                        mDrive.setLight(Drive.Light.DAY);
                        break;

                    case R.id.post_drive_light_night:
                        mDrive.setLight(Drive.Light.NIGHT);
                        break;

                    default:
                        mDrive.setLight(Drive.Light.DAY);
                }

                switch (((RadioGroup) findViewById(R.id.post_drive_weather)).getCheckedRadioButtonId()) {
                    case R.id.post_drive_weather_dry:
                        mDrive.setWeather(Drive.Weather.DRY);
                        break;

                    case R.id.post_drive_weather_wet:
                        mDrive.setWeather(Drive.Weather.WET);
                        break;

                    default:
                        mDrive.setWeather(Drive.Weather.DRY);
                }

                switch (((RadioGroup) findViewById(R.id.post_drive_traffic)).getCheckedRadioButtonId()) {
                    case R.id.post_drive_traffic_light:
                        mDrive.setTraffic(Drive.Traffic.LIGHT);
                        break;

                    case R.id.post_drive_traffic_medium:
                        mDrive.setTraffic(Drive.Traffic.MEDIUM);
                        break;

                    case R.id.post_drive_traffic_heavy:
                        mDrive.setTraffic(Drive.Traffic.HEAVY);
                        break;

                    default:
                        mDrive.setTraffic(Drive.Traffic.LIGHT);
                }

                driveDao.insert(mDrive);
                goHome();
            }
        });

        // show loading
        final FrameLayout progressBarHolder = (FrameLayout) findViewById(R.id.progress_overlay);
        progressBarHolder.setVisibility(View.VISIBLE);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                progressBarHolder.animate().setDuration(200).alpha(0f)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            progressBarHolder.setVisibility(View.GONE);
                        }
                    });
            }
        }, 10000);
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

    @Override
    public void onBackPressed() {
        Snackbar.make(this.findViewById(android.R.id.content), "Do you really want to exit?", Snackbar.LENGTH_LONG)
                .setAction("Exit", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        goHome();
                    }
                })
                .show();
    }

    private void goHome() {
        Intent intent = new Intent(this, RootActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
