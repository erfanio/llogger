package io.erfan.llogger.activity.driving;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import io.erfan.llogger.R;
import io.erfan.llogger.activity.DrivingActivity;

public class DrivingService extends Service {
    public interface DrivingServiceListener {
        void connected();
        void locationUpdated(Location location);
        void paused();
        void resumed();
    }

    // constants for intents
    public static final String PAUSE = "Pause";
    public static final String RESUME = "Resume";

    private static final String NOTIFICATION_PAUSE = "PAUSE_DRIVING";
    private static final String NOTIFICATION_RESUME = "RESUME_DRIVING";
    private static final int NOTIFICATION_ID = 11;

    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private DrivingServiceListener mListener = null;

    private final IBinder mBinder = new DrivingBinder();
    public class DrivingBinder extends Binder {
        public DrivingService getService() {
            return DrivingService.this;
        }
    }

    @Override
    public void onCreate() {
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        setupNotification();
    }

    @Override
    public void onDestroy() {
        mNotificationManager.cancelAll();
        stopForeground(true);
        // a bug in android O needs this
//        mNotificationManager.cancel(NOTIFICATION_ID);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private void setupMap() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(100);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        // check for permissions
                        if (ActivityCompat.checkSelfPermission(DrivingService.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                                ActivityCompat.checkSelfPermission(DrivingService.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            mGoogleApiClient.disconnect();
                            stopSelf();
                            return;
                        }

                        mListener.connected();

                        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, new LocationListener() {
                            @Override
                            public void onLocationChanged(Location location) {
                                if (mListener != null) {
                                    mListener.locationUpdated(location);
                                }
                            }
                        });
                    }

                    @Override
                    public void onConnectionSuspended(int i) {}
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}
                })
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClient.connect();
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
    }

    private Notification runningNotification() {
        mBuilder.mActions.clear();
        Intent intent = new Intent(this, DrivingReceiver.class);
        intent.setAction(PAUSE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        mBuilder.addAction(R.drawable.ic_pause_black, "Pause", pendingIntent);
        mBuilder.setContentText(getString(R.string.title_activity_driving));
        return mBuilder.build();
    }

    private Notification pausedNotification() {
        mBuilder.mActions.clear();
        Intent intent = new Intent(this, DrivingReceiver.class);
        intent.setAction(RESUME);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        mBuilder.addAction(R.drawable.ic_play_black, "Resume", pendingIntent);
        mBuilder.setContentText(getString(R.string.title_activity_driving_paused));
        return mBuilder.build();
    }

    public void showNotification(Notification notification) {
        mNotificationManager.notify(NOTIFICATION_ID, notification);
    }

    public void start(DrivingServiceListener listener) {
        mListener = listener;
        setupMap();
        startForeground(NOTIFICATION_ID, runningNotification());
    }

    public void resume() {
        Log.d("TAG", "resume");
        mListener.resumed();
        mGoogleApiClient.connect();
        showNotification(runningNotification());
    }

    public void pause() {
        Log.d("TAG", "pause");
        mListener.paused();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        showNotification(pausedNotification());
    }
}
