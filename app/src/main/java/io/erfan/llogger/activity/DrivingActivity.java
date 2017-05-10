package io.erfan.llogger.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import io.erfan.llogger.App;
import io.erfan.llogger.R;
import io.erfan.llogger.model.DaoSession;
import io.erfan.llogger.model.Drive;
import io.erfan.llogger.model.DriveDao;

import static io.erfan.llogger.Utils.formatDistance;
import static io.erfan.llogger.Utils.formatDuration;

public class DrivingActivity extends AppCompatActivity {
    FloatingActionButton mMainFab;
    FloatingActionButton mSecondFab;
    TextView mDuration;
    TextView mDistance;

    boolean mPaused;
    Drive mDrive;
    Long mElapsed;
    Timer mTimer;

    Long mFakeDistance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driving);
        Toolbar toolbar = (Toolbar) findViewById(R.id.driving_toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        mDrive = intent.getParcelableExtra("Drive");
        mDrive.setTime(Calendar.getInstance().getTime());

        mPaused = false;
        mElapsed = (long) 0;

        mFakeDistance = (long) 0;

        mMainFab = (FloatingActionButton) findViewById(R.id.driving_fab);
        mMainFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPaused) {
                    resume();
                } else {
                    pause();
                }
            }
        });

        mSecondFab = (FloatingActionButton) findViewById(R.id.driving_fab2);
        mSecondFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get the drive DAO
                DaoSession daoSession = ((App) getApplication()).getDaoSession();
                DriveDao driveDao = daoSession.getDriveDao();

                mDrive.setLocation("Clayton");
                mDrive.setDuration(mElapsed);
                mDrive.setLight(Drive.Light.DAY);
                mDrive.setTraffic(Drive.Traffic.MEDIUM);
                mDrive.setWeather(Drive.Weather.DRY);

                driveDao.insert(mDrive);

                goHome();
            }
        });

        mDuration = (TextView) findViewById(R.id.driving_duration);
        mDistance = (TextView) findViewById(R.id.driving_distance);

        setupTimer();
    }

    void setupTimer() {
        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        mElapsed++;
                        mDuration.setText(formatDuration(mElapsed));
                        mFakeDistance += 150;
                        mDistance.setText(formatDistance(mFakeDistance));
                    }
                });
            }
        }, 1000, 1000);
    }

    void goHome() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    void resume() {
        mMainFab.setImageResource(R.drawable.ic_pause_white);
        mSecondFab.setVisibility(View.GONE);
        setTitle(R.string.title_activity_driving);
        setupTimer();
        mPaused = false;
    }

    void pause() {
        mMainFab.setImageResource(R.drawable.ic_play_white);
        mSecondFab.setVisibility(View.VISIBLE);
        setTitle(R.string.title_activity_driving_paused);
        mTimer.cancel();
        mPaused = true;
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
}
