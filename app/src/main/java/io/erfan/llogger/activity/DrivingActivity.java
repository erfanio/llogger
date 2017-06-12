package io.erfan.llogger.activity;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
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

public class DrivingActivity extends AppCompatActivity {
    private static final String TAG = DrivingActivity.class.getSimpleName();
    private static final int NOTIFICATION_ID = 0;
    private static final String NOTIFICATION_PAUSE = "PAUSE_DRIVING";
    private static final String NOTIFICATION_RESUME = "RESUME_DRIVING";

    private FloatingActionButton mMainFab;
    private FloatingActionButton mSecondFab;
    private TextView mDuration;
    private TextView mDistance;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private GoogleMap mMap;
    private Polyline mPolyline;
    private NotificationCompat.Builder mBuilder;

    private boolean mPaused;
    private Drive mDrive;
    private Long mElapsed;
    private Timer mTimer;
    private String mPath;
    private List<String> mPaths;
    private Long mPathDistance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driving);
        Toolbar toolbar = (Toolbar) findViewById(R.id.driving_toolbar);
        setSupportActionBar(toolbar);

        Log.d(TAG, "OnCreate called");
        Intent intent = getIntent();
        mDrive = intent.getParcelableExtra("Drive");
        mDrive.setTime(Calendar.getInstance().getTime());

        mPaused = false;
        mElapsed = (long) 0;
        mPath = "";
        mPaths = new ArrayList<>();

        mPathDistance = (long) 0;

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
                mDrive.setLocation("Clayton");
                mDrive.setPath(mPaths);
                mDrive.setDistance(mPathDistance);
                mDrive.setDayDuration(mElapsed);
                mDrive.setNightDuration(0L);
                Intent intent = new Intent(v.getContext(), PostDriveActivity.class);
                intent.putExtra("Drive", mDrive);
                startActivity(intent);
                ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE))
                        .cancel(NOTIFICATION_ID);
                finish();
            }
        });

        mDuration = (TextView) findViewById(R.id.driving_duration);
        mDistance = (TextView) findViewById(R.id.driving_distance);

        setupNotification();
        setupMap();
        mGoogleApiClient.connect();

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
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "New Intent");
        if (intent.getAction().equals(NOTIFICATION_PAUSE)) {
            pause();
        } else if (intent.getAction().equals(NOTIFICATION_RESUME)) {
            resume();
        }
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
                mGoogleApiClient.connect();
            }
        }
    }

    private void setupMap() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(500);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {

                        // check for permissions
                        if (ActivityCompat.checkSelfPermission(DrivingActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                                ActivityCompat.checkSelfPermission(DrivingActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                            ActivityCompat.requestPermissions(DrivingActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                                    App.LOCATION_PERMISSION_REQUEST_CODE);
                            mGoogleApiClient.disconnect();

                            return;
                        }

                        mMap.setMyLocationEnabled(true);
                        setupTimer();
                        showNotification();
                        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, new LocationListener() {
                            @Override
                            public void onLocationChanged(Location location) {
                                double currentLatitude = location.getLatitude();
                                double currentLongitude = location.getLongitude();
                                LatLng latLng = new LatLng(currentLatitude, currentLongitude);

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
                        });
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        Log.i(TAG, "Connection Suspended");
                        mTimer.cancel();
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Log.i(TAG, "Connection failed. Error: " + connectionResult.getErrorCode());
                    }
                })
                .addApi(LocationServices.API)
                .build();
    }

    private void setupTimer() {
        Log.d(TAG, "SETUP TIMER");
        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mElapsed++;
                        mDuration.setText(formatDuration(mElapsed));
                        Log.d(TAG, String.valueOf(mPaused));
                    }
                });
            }
        }, 1000, 1000);
    }

    private void setupNotification() {
        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(R.drawable.ic_menu_drive);
        mBuilder.setContentTitle("Learners Digital Logbook");
        mBuilder.setStyle(new NotificationCompat.MediaStyle().setShowActionsInCompactView(0));
        mBuilder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        mBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        mBuilder.setOngoing(true);
        mBuilder.setShowWhen(false);

        // tapping the notification will bring this activity to the foreground
        Intent intent = new Intent(this, DrivingActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent);

        if (mPaused) {
            intent = new Intent(this, DrivingActivity.class);
            intent.setAction(NOTIFICATION_RESUME);
            pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.addAction(R.drawable.ic_play_black, "Resume", pendingIntent);
            mBuilder.setContentText(getString(R.string.title_activity_driving_paused));
        } else {
            // pause button
            intent = new Intent(this, DrivingActivity.class);
            intent.setAction(NOTIFICATION_PAUSE);
            pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.addAction(R.drawable.ic_pause_black, "Pause", pendingIntent);
            mBuilder.setContentText(getString(R.string.title_activity_driving));
        }
    }

    private void showNotification() {
        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE))
                .notify(NOTIFICATION_ID, mBuilder.build());
    }

    private void goHome() {
        // remove notification
        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE))
                .cancel(NOTIFICATION_ID);

        // go home
        Intent intent = new Intent(this, RootActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void resume() {
        mPaused = false;
        mMainFab.setImageResource(R.drawable.ic_pause_white);
        mSecondFab.setVisibility(View.GONE);
        setTitle(R.string.title_activity_driving);
        mGoogleApiClient.connect();
        setupNotification();
    }

    private void pause() {
        mPaused = true;
        mMainFab.setImageResource(R.drawable.ic_play_white);
        mSecondFab.setVisibility(View.VISIBLE);
        setTitle(R.string.title_activity_driving_paused);
        mTimer.cancel();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        mMap.addPolyline(new PolylineOptions().addAll(PolyUtil.decode(mPath)));
        mPaths.add(mPath);
        mPath = "";
        setupNotification();
        showNotification();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancelAll();
    }
}
