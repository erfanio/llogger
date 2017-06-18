package io.erfan.llogger.activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import io.erfan.llogger.App;
import io.erfan.llogger.Timespan;
import io.erfan.llogger.activity.driving.DrivingService;
import io.erfan.llogger.R;
import io.erfan.llogger.Utils;
import io.erfan.llogger.model.Drive;

import static io.erfan.llogger.Utils.formatDuration;
import static io.erfan.llogger.activity.PostDriveActivity.EXTRA_DRIVE;
import static io.erfan.llogger.activity.PostDriveActivity.EXTRA_TIMESPANS;

public class DrivingActivity extends AppCompatActivity implements DrivingService.DrivingServiceListener {
    private static final String UNKNOWN = "Unknown";

    private FloatingActionButton mMainFab;
    private FloatingActionButton mSecondFab;
    private TextView mDuration;
    private TextView mDistance;

    private GoogleMap mMap;
    private Polyline mPolyline;
    private Timer mTimer;

    private Drive mDrive;

    private boolean mPaused = false;
    private List<Timespan> mTimespans = new ArrayList<>();
    private Long mStartTime;
    private String mPath = "";
    private List<String> mPaths = new ArrayList<>();
    private Long mPathDistance = 0L;
    private Calendar mLocationNameRetry = Calendar.getInstance(); // get location name asap

    private ServiceConnection mConnection;
    DrivingService mService;
    boolean mBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driving);
        Toolbar toolbar = (Toolbar) findViewById(R.id.driving_toolbar);
        setSupportActionBar(toolbar);

        // get the drive object passed to this
        Intent intent = getIntent();
        if (!intent.hasExtra("Drive")) {
            // we need a drive object to be passed to this activity
            Toast.makeText(this, "Unexpected error occurred!", Toast.LENGTH_LONG).show();
            finish();
        }
        mDrive = intent.getParcelableExtra("Drive");
        mDrive.setTime(Calendar.getInstance().getTime());
        mDrive.setLocation(UNKNOWN); // will be changed later if we find an address

        mConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName className,
                                           IBinder service) {
                // we've bound to DrivingService, cast the IBinder and get DrivingService instance
                DrivingService.DrivingBinder binder = (DrivingService.DrivingBinder) service;
                mService = binder.getService();
                mBound = true;
                // tell the service to start (and pass DrivingActivity's instance as the listener)
                mService.start(DrivingActivity.this);
            }

            @Override
            public void onServiceDisconnected(ComponentName arg0) {
                mBound = false;
            }
        };

        // check for permission
        if (ActivityCompat.checkSelfPermission(DrivingActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(DrivingActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // if we don't have permission ask user for it before we start the service
            ActivityCompat.requestPermissions(DrivingActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    App.LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // if we have permission start the service
            bindService();
        }

        mMainFab = (FloatingActionButton) findViewById(R.id.driving_fab);
        mMainFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // only respond if we're bound to the service (otherwise we can't do anything)
                if (mBound) {
                    if (mPaused) {
                        mService.resume();
                    } else {
                        mService.pause();
                    }
                }
            }
        });

        mSecondFab = (FloatingActionButton) findViewById(R.id.driving_fab2);
        mSecondFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // set Drive info
                mDrive.setPath(mPaths);
                mDrive.setDistance(mPathDistance);

                // go to PostDrive to ask for driving conditions
                Intent intent = new Intent(v.getContext(), PostDriveActivity.class);
                intent.putExtra(EXTRA_DRIVE, mDrive);
                intent.putExtra(EXTRA_TIMESPANS, mTimespans.toArray());
                intent.putParcelableArrayListExtra(EXTRA_TIMESPANS, new ArrayList<Parcelable>(mTimespans));
                startActivity(intent);

                finish();
            }
        });

        // init textviews with 0 (in case location takes long to get accurate location)
        mDuration = (TextView) findViewById(R.id.driving_duration);
        mDuration.setText(formatDuration(0L));
        mDistance = (TextView) findViewById(R.id.driving_distance);
        mDistance.setText(Utils.formatDistance(0L));

        // get the google maps fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.driving_map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                // a new polyline to draw the current path
                mPolyline = googleMap.addPolyline(new PolylineOptions());
                // zoom close (16 works pretty well)
                mMap.moveCamera(CameraUpdateFactory.zoomTo(16));
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == App.LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // start service now that we have the proper permissions
                bindService();
            }
        }
    }

    private void bindService() {
        Intent intent = new Intent(this, DrivingService.class);
        bindService(intent, mConnection, BIND_AUTO_CREATE);
    }

    private void getLocationName(final double lat, final double lng) {
        // run on another thread since it might take a while
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Geocoder geocoder = new Geocoder(DrivingActivity.this, Locale.getDefault());

                List<Address> addresses = null;
                try {
                    // a list of one address from the current coordinates
                    addresses = geocoder.getFromLocation(lat, lng, 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (addresses != null && addresses.size() != 0) {
                    Address address = addresses.get(0);
                    // set the location of the drive to the suburb (of the address) we found
                    mDrive.setLocation(address.getLocality());
                } else {
                    // if no address found set counter to try later (and not immediately)
                    mLocationNameRetry.add(Calendar.SECOND, 20);
                }
            }
        });
    }

    private long currentElapsed() {
        long currentElapsed = 0;
        // if not paused
        if (!mPaused) {
            // time elapsed since the last start
            currentElapsed += (System.currentTimeMillis() / 1000) - mStartTime;
        }
        // duration of previous stretches (start then pause)
        for (Timespan timespan : mTimespans) {
            currentElapsed += timespan.end - timespan.start;
        }
        return currentElapsed;
    }

    private void setupTimer() {
        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateUI();
                    }
                });
            }
        }, 1000, 1000);
    }

    private void updateUI() {
        // update the textviews
        mDuration.setText(formatDuration(currentElapsed()));
        mDistance.setText(Utils.formatDistance(mPathDistance));

        // update the notification
        mService.updateNotification(formatDuration(currentElapsed()),
                Utils.formatDistance(mPathDistance));
    }

    public void connected() {
        // will measure duration from this moment (for the current stretch of driving)
        mStartTime = System.currentTimeMillis() / 1000;
        setupTimer();

        // in theory we should always have this permission (since we check for before we start the service)
        // but this is just in case to now crash the app
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    public void locationUpdated(Location location) {
        // too inaccurate ignore
        if (location.getAccuracy() > 30) {
            return;
        }

        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();
        LatLng latLng = new LatLng(currentLatitude, currentLongitude);

        // retry time throttles the rate of retries
        if (mDrive.getLocation().equals(UNKNOWN) &&
                location.getAccuracy() < 100 &&
                mLocationNameRetry.before(Calendar.getInstance())) {
            getLocationName(currentLatitude, currentLongitude);
        }

        // get a path (list of  from the encoded coordinates
        List<LatLng> path = new ArrayList<>(PolyUtil.decode(mPath));

        // the first point doesn't add any distance
        if (path.size() > 0) {
            mPathDistance += (long) SphericalUtil.computeDistanceBetween(latLng, path.get(path.size() - 1));
        }

        // add this point to path
        path.add(latLng);
        mPath = PolyUtil.encode(path);

        // update the map
        mPolyline.setPoints(path);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    public void paused() {
        mPaused = true;
        // update UI
        mMainFab.setImageResource(R.drawable.ic_play_white);
        mSecondFab.setVisibility(View.VISIBLE);
        setTitle(R.string.title_activity_driving_paused);

        // make the current path permanent on the map
        mMap.addPolyline(new PolylineOptions().addAll(PolyUtil.decode(mPath)));
        // create a new path (and current one to the list)
        mPaths.add(mPath);
        mPath = "";

        // add a new timespan
        mTimespans.add(new Timespan(mStartTime, System.currentTimeMillis() / 1000));
        mTimer.cancel();
    }

    public void resumed() {
        mPaused = false;
        // update UI
        mMainFab.setImageResource(R.drawable.ic_pause_white);
        mSecondFab.setVisibility(View.GONE);
        setTitle(R.string.title_activity_driving);
    }

    private void goHome() {
        // open RootActivity (launcher activity) on a new/cleared stack
        Intent intent = new Intent(this, RootActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }
}
