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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import io.erfan.llogger.R;
import io.erfan.llogger.activity.DrivingActivity;

public class DrivingService extends Service {
    // a listener interface so the client can listen for events
    // that happen inside this service
    public interface DrivingServiceListener {
        void connected();
        void locationUpdated(Location location);
        void paused();
        void resumed();
    }

    // constants for intents
    public static final String PAUSE = "Pause";
    public static final String RESUME = "Resume";

    private static final int NOTIFICATION_ID = 11;

    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;
    private int mNotificationTitleRes = R.string.driving_notification_title;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private DrivingServiceListener mListener = null;

    // this will be returned to bound clients to access public methods
    private final IBinder mBinder = new DrivingBinder();
    public class DrivingBinder extends Binder {
        public DrivingService getService() {
            // return an instance of this class so they can access public methods
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
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    //
    private void startLocationTracking() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(100);

        // connect to google api and get location services to track location
        mGoogleApiClient = new GoogleApiClient.Builder(this)
            .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                @Override
                public void onConnected(Bundle bundle) {
                    // check for permissions
                    if (ActivityCompat.checkSelfPermission(DrivingService.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(DrivingService.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        mGoogleApiClient.disconnect();
                        // stop since there is no point running without permission to do its job
                        stopSelf();
                        return;
                    }

                    mListener.connected();

                    // this will start tracking the location
                    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            mListener.locationUpdated(location);
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

    // this will build a notification (we'll keep the same notification and modify it later)
    private void setupNotification() {
        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(R.drawable.ic_menu_drive);
        mBuilder.setStyle(new NotificationCompat.MediaStyle().setShowActionsInCompactView(0));
        mBuilder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        mBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        mBuilder.setCategory(NotificationCompat.CATEGORY_SERVICE);
        mBuilder.setOngoing(true);
        mBuilder.setShowWhen(false);

        // tapping the notification will bring this activity to the foreground
        Intent intent = new Intent(this, DrivingActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent);

        // setup the notification content
        updateContent("0sec", "0m");
    }

    private void updateContent(String duration, String distance) {
        mBuilder.setContentTitle(getString(mNotificationTitleRes, duration));
        mBuilder.setContentText(getString(R.string.driving_notification_text, distance));
    }

    private Notification runningNotification() {
        // clear the previous action (i.e. pause button)
        mBuilder.mActions.clear();

        // pause button will send to a BroadcastReceiver which will
        // acquire an instance of this and will call pause()
        Intent intent = new Intent(this, DrivingReceiver.class);
        intent.setAction(PAUSE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        mBuilder.addAction(R.drawable.ic_notification_pause, getString(R.string.pause), pendingIntent);

        // set the title text template
        mNotificationTitleRes = R.string.driving_notification_title;

        return mBuilder.build();
    }

    private Notification pausedNotification() {
        // clear the previous action (i.e. resume button)
        mBuilder.mActions.clear();

        // resume button will send to a BroadcaseReceiver which will
        // acquire an instance of this and will call resume()
        Intent intent = new Intent(this, DrivingReceiver.class);
        intent.setAction(RESUME);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        mBuilder.addAction(R.drawable.ic_notification_play, getString(R.string.resume), pendingIntent);

        // set the title text template
        mNotificationTitleRes = R.string.driving_notification_title_paused;

        return mBuilder.build();
    }

    public void updateNotification(String duration, String distance) {
        updateContent(duration, distance);
        showNotification(mBuilder.build());
    }

    private void showNotification(Notification notification) {
        mNotificationManager.notify(NOTIFICATION_ID, notification);
    }

    public void start(DrivingServiceListener listener) {
        mListener = listener;
        startLocationTracking();
        // having the service in the foreground will stop the system from killing the service
        // when it needs memory
        startForeground(NOTIFICATION_ID, runningNotification());
    }

    public void resume() {
        mListener.resumed();
        // start tracking location
        mGoogleApiClient.connect();
        showNotification(runningNotification());
    }

    public void pause() {
        mListener.paused();
        // stop tracking location
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        showNotification(pausedNotification());
    }
}
