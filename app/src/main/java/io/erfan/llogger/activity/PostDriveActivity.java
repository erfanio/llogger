package io.erfan.llogger.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.maps.android.PolyUtil;

import java.util.List;
import java.util.Map;

import io.erfan.llogger.App;
import io.erfan.llogger.DriveConditions;
import io.erfan.llogger.R;
import io.erfan.llogger.Utils;
import io.erfan.llogger.model.DaoSession;
import io.erfan.llogger.model.Drive;
import io.erfan.llogger.model.DriveDao;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PostDriveActivity extends AppCompatActivity {
    public static String EXTRA_DRIVE = "Drive";
    public static String EXTRA_TIMESPANS = "Timespans";

    private Drive mDrive;
    private List<Utils.Timespan> mTimespans;

    TextView mViewDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_drive);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        mDrive = intent.getParcelableExtra(EXTRA_DRIVE);
        mTimespans = intent.getParcelableArrayListExtra(EXTRA_TIMESPANS);

        mViewDuration = (TextView) findViewById(R.id.post_drive_duration);
        mViewDuration.setText(Utils.formatDuration(Utils.calculateTimespansDuration(mTimespans)));
        TextView viewDistance = (TextView) findViewById(R.id.post_drive_distance);
        viewDistance.setText(mDrive.getFormattedDistance());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.post_drive_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // choose drive properties based on active radio buttons
                switch (((RadioGroup) findViewById(R.id.post_drive_light)).getCheckedRadioButtonId()) {
                    case R.id.post_drive_light_day:
                        mDrive.setLight(Drive.Light.DAY);
                        break;

                    case R.id.post_drive_light_dawn_dusk:
                        mDrive.setLight(Drive.Light.DAWN_DUSK);
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

                // set parking and road type
                mDrive.setParking(((CheckBox) findViewById(R.id.post_drive_parking)).isChecked());
                mDrive.setRoadLocal(((CheckBox) findViewById(R.id.post_drive_parking)).isChecked());
                mDrive.setRoadMain(((CheckBox) findViewById(R.id.post_drive_road_main)).isChecked());
                mDrive.setRoadCity(((CheckBox) findViewById(R.id.post_drive_road_city)).isChecked());
                mDrive.setRoadFreeway(((CheckBox) findViewById(R.id.post_drive_road_freeway)).isChecked());
                mDrive.setRoadRuralHwy(((CheckBox) findViewById(R.id.post_drive_road_rural_hwy)).isChecked());
                mDrive.setRoadRuralOther(((CheckBox) findViewById(R.id.post_drive_road_rural)).isChecked());
                mDrive.setRoadGravel(((CheckBox) findViewById(R.id.post_drive_road_gravel)).isChecked());

                // insert into the database
                DaoSession daoSession = ((App) getApplication()).getDaoSession();
                DriveDao driveDao = daoSession.getDriveDao();
                driveDao.insert(mDrive);

                goHome();
            }
        });

        // run network on another thread
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();

                // get the last coord
                List<String> paths = mDrive.getPath();
                List<LatLng> lastPath = PolyUtil.decode(paths.get(paths.size() - 1));
                LatLng coord = lastPath.get(lastPath.size() - 1);

                // create the params
                String params = String.format("lat=%s&lng=%s", String.valueOf(coord.latitude), String.valueOf(coord.longitude));

                // query the server
                Request request = new Request.Builder()
                        .url("https://llogger.erfan.space/drive_conditions?" + params)
                        .header("User-Agent", "OkHttp Headers.java")
                        .addHeader("Accept", "application/json; q=0.5")
                        .get()
                        .build();

                // wrap everything in a try since every single step _might_ throw
                try {
                    // send the query
                    Response response = client.newCall(request).execute();

                    // throw so we don't continue (but also want the last statement in the method to run)
                    if (!response.isSuccessful()) {
                        throw new Exception("Couldn't reach the server");
                    }

                    // get an object from the response
                    Gson gson = new Gson();
                    DriveConditions driveConditions = gson
                            .fromJson(response.body().charStream(), DriveConditions.class);

                    setupDriveConditions(driveConditions);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                hideOverlay();
            }
        });
    }

    private void setupDriveConditions(DriveConditions driveConditions) {
        // set weather condition
        if (driveConditions.getWet()) {
            ((RadioButton) findViewById(R.id.post_drive_weather_wet)).setChecked(true);
        } else {
            ((RadioButton) findViewById(R.id.post_drive_weather_dry)).setChecked(true);
        }

        // set light
        if (driveConditions.getLight().equals("day")) {
            ((RadioButton) findViewById(R.id.post_drive_light_day)).setChecked(true);
        } else if (driveConditions.getLight().equals("dawn/dusk")) {
            ((RadioButton) findViewById(R.id.post_drive_light_dawn_dusk)).setChecked(true);
        } else {
            ((RadioButton) findViewById(R.id.post_drive_light_night)).setChecked(true);
        }

        // get day and night duration (and set them in drive)
        Map<Integer, Long> durations = Utils.calculateDayNightDuration(mTimespans, driveConditions);
        mDrive.setDayDuration(durations.get(Utils.DAY));
        mDrive.setNightDuration(durations.get(Utils.NIGHT));

        mViewDuration.setText(mDrive.getFormattedDuration());
    }

    private void hideOverlay() {
        final FrameLayout frameLayout = (FrameLayout) findViewById(R.id.progress_overlay);
        // animate fade out and then hide the overlay
        frameLayout.animate().setDuration(200).alpha(0f)
            .setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    frameLayout.setVisibility(View.GONE);
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

    @Override
    public void onBackPressed() {
        // prevent user from exiting accidentally (since this activity was started on a new stack)
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
        // go home and clear activity stack so can't come back to this activity
        Intent intent = new Intent(this, RootActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
