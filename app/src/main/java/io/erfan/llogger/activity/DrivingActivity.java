package io.erfan.llogger.activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import io.erfan.llogger.App;
import io.erfan.llogger.R;
import io.erfan.llogger.Utils;
import io.erfan.llogger.model.Drive;

import static io.erfan.llogger.Utils.formatDuration;

public class DrivingActivity extends AppCompatActivity implements DrivingService.DrivingServiceListener {
    private static final String UNKNOWN = "Unknown";

    private FloatingActionButton mMainFab;
    private FloatingActionButton mSecondFab;
    private TextView mDuration;
    private TextView mDistance;

    private GoogleMap mMap;
    private Polyline mPolyline;

    private boolean mPaused;
    private Drive mDrive;
    private Long mElapsed;
    private Timer mTimer;
    private String mPath;
    private List<String> mPaths;
    private Long mPathDistance;
    private Calendar mLocationNameRetry;

    private ServiceConnection mConnection;
    DrivingService mService;
    boolean mBound = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driving);
        Toolbar toolbar = (Toolbar) findViewById(R.id.driving_toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        mDrive = intent.getParcelableExtra("Drive");
        mDrive.setTime(Calendar.getInstance().getTime());
        mDrive.setLocation(UNKNOWN);
        // get try to get location name asap
        mLocationNameRetry = Calendar.getInstance();

        mPaused = false;
        mElapsed = (long) 0;
        mPath = "";
        mPaths = new ArrayList<>();

        mPathDistance = (long) 0;

        mConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName className,
                                           IBinder service) {
                // We've bound to LocalService, cast the IBinder and get LocalService instance
                DrivingService.DrivingBinder binder = (DrivingService.DrivingBinder) service;
                mService = binder.getService();
                mBound = true;
                mService.start(DrivingActivity.this);
            }

            @Override
            public void onServiceDisconnected(ComponentName arg0) {
                mBound = false;
            }
        };


        if (ActivityCompat.checkSelfPermission(DrivingActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(DrivingActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(DrivingActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    App.LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            bindService();
        }

        mMainFab = (FloatingActionButton) findViewById(R.id.driving_fab);
        mMainFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                mDrive.setPath(mPaths);
                mDrive.setDistance(mPathDistance);
                // set both, later will set one to 0
                mDrive.setDayDuration(mElapsed);
                mDrive.setNightDuration(mElapsed);

                Intent intent = new Intent(v.getContext(), PostDriveActivity.class);
                intent.putExtra("Drive", mDrive);
                startActivity(intent);

                finish();
            }
        });

        mDuration = (TextView) findViewById(R.id.driving_duration);
        mDistance = (TextView) findViewById(R.id.driving_distance);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.driving_map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                mPolyline = googleMap.addPolyline(new PolylineOptions());
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == App.LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                bindService();
            }
        }
    }

    private void bindService() {
        Intent intent = new Intent(this, DrivingService.class);
        bindService(intent, mConnection, BIND_AUTO_CREATE);
    }

    private void getLocationName(final double lat, final double lng) {
        final Context context = this;
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());

                List<Address> addresses = null;
                try {
                    addresses = geocoder.getFromLocation(lat, lng, 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (addresses != null && addresses.size() != 0) {
                    Address address = addresses.get(0);
                    mDrive.setLocation(address.getLocality());
                } else {
                    // if no address found set counter to try later (and not immediately)
                    mLocationNameRetry.add(Calendar.SECOND, 20);
                }
            }
        });
    }

    public void connected() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        setupTimer();
    }

    public void locationUpdated(Location location) {
        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();
        LatLng latLng = new LatLng(currentLatitude, currentLongitude);

        // retry time throttles the rate of retries
        if (mDrive.getLocation().equals(UNKNOWN) &&
                location.getAccuracy() < 100 &&
                mLocationNameRetry.before(Calendar.getInstance())) {
            getLocationName(currentLatitude, currentLongitude);
        }

        List<LatLng> path = new ArrayList<>(PolyUtil.decode(mPath));

        if (path.size() > 0) {
            mPathDistance += (long) SphericalUtil.computeDistanceBetween(latLng, path.get(path.size() - 1));
            mDistance.setText(String.valueOf(Utils.formatDistance(mPathDistance)));
        }

        path.add(latLng);
        mPath = PolyUtil.encode(path);

        mPolyline.setPoints(path);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    public void paused() {
        mPaused = true;
        mMainFab.setImageResource(R.drawable.ic_play_white);
        mSecondFab.setVisibility(View.VISIBLE);
        setTitle(R.string.title_activity_driving_paused);
        mTimer.cancel();
        mMap.addPolyline(new PolylineOptions().addAll(PolyUtil.decode(mPath)));
        mPaths.add(mPath);
        mPath = "";
    }

    public void resumed() {
        mPaused = false;
        mMainFab.setImageResource(R.drawable.ic_pause_white);
        mSecondFab.setVisibility(View.GONE);
        setTitle(R.string.title_activity_driving);
    }

    private void setupTimer() {
        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mElapsed++;
                        mDuration.setText(formatDuration(mElapsed));
                    }
                });
            }
        }, 1000, 1000);
    }

    private void goHome() {
        // go home
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
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }
}
