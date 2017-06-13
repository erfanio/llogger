package io.erfan.llogger.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.ChangeBounds;
import android.transition.Transition;
import android.transition.TransitionSet;
import android.util.Pair;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import java.util.ArrayList;
import java.util.List;

import io.erfan.llogger.App;
import io.erfan.llogger.R;
import io.erfan.llogger.TextSizeTransition;
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

        final TextView viewDuration = (TextView) findViewById(R.id.detail_duration);
        TextView viewDistance = (TextView) findViewById(R.id.detail_distance);
        TextView viewWeatherEmoji = (TextView) findViewById(R.id.detail_weather_emoji);
        TextView viewLightEmoji = (TextView) findViewById(R.id.detail_light_emoji);
        TextView viewLight = (TextView) findViewById(R.id.detail_light);
        TextView viewWeather = (TextView) findViewById(R.id.detail_weather);
        TextView viewTraffic = (TextView) findViewById(R.id.detail_traffic);
        final TextView viewCar = (TextView) findViewById(R.id.detail_car);
        TextView viewSupervisor = (TextView) findViewById(R.id.detail_supervisor);


        DaoSession daoSession = ((App) getApplication()).getDaoSession();
        DriveDao driveDao = daoSession.getDriveDao();

        Intent intent = getIntent();
        Long driveId = intent.getLongExtra("DriveId", -1);
        if (driveId == -1) {
            Toast.makeText(this, "Unexpected error occurred", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        mDrive = driveDao.loadDeep(driveId);

        // setup for activity transition share elements
        viewDuration.setTransitionName("duration" + driveId);
        viewCar.setTransitionName("car" + driveId);
        getWindow().setSharedElementEnterTransition(makeSharedElementEnterTransition("duration" + driveId, "car" + driveId));
        setEnterSharedElementCallback(new android.support.v4.app.SharedElementCallback() {
            @Override
            public void onSharedElementStart(List<String> sharedElementNames, List<View> sharedElements, List<View> sharedElementSnapshots) {
                startSharedElement(viewDuration, 22);
                startSharedElement(viewCar, 14);
            }

            @Override
            public void onSharedElementEnd(List<String> sharedElementNames, List<View> sharedElements, List<View> sharedElementSnapshots) {
                endSharedElement(viewDuration, 48);
                endSharedElement(viewCar, 22);
            }
        });

        viewDuration.setText(mDrive.getFormattedDuration());
        viewDistance.setText(mDrive.getFormattedDistance());
        viewCar.setText(mDrive.getCar().getName());
        viewSupervisor.setText(mDrive.getSupervisor().getName());
        viewLightEmoji.setText(mDrive.getLightEmojiRes());
        viewWeatherEmoji.setText(mDrive.getWeatherEmojiRes());
        viewLight.setText(mDrive.getLightStringRes());
        viewWeather.setText(mDrive.getWeatherStringRes());
        viewTraffic.setText(mDrive.getTrafficStringRes());

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

    public Transition makeSharedElementEnterTransition(String durationName, String carName) {
        TransitionSet set = new TransitionSet();
        set.setOrdering(TransitionSet.ORDERING_TOGETHER);

        List<Pair<Integer, String>> pairs = new ArrayList<>();
        pairs.add(new Pair<>(R.id.detail_duration, durationName));
        pairs.add(new Pair<>(R.id.detail_car, carName));

        for (Pair<Integer, String> pair : pairs) {
            Transition changeBounds = new ChangeBounds();
            changeBounds.addTarget(pair.first);
            changeBounds.addTarget(pair.second);
            set.addTransition(changeBounds);

            Transition textSize = new TextSizeTransition();
            textSize.addTarget(pair.first);
            textSize.addTarget(pair.second);
            set.addTransition(textSize);
        }

        return set;
    }

    private void startSharedElement(View view, float size) {
        TextView textView = (TextView) view;

        // Setup the TextView's start values.
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
    }

    private void endSharedElement(View view, float size) {
        TextView textView = (TextView) view;

        // Record the TextView's old width/height.
        int oldWidth = textView.getMeasuredWidth();
        int oldHeight = textView.getMeasuredHeight();

        // Setup the TextView's end values.
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);

        // Re-measure the TextView (since the text size has changed).
        int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        textView.measure(widthSpec, heightSpec);

        // Record the TextView's new width/height.
        int newWidth = textView.getMeasuredWidth();
        int newHeight = textView.getMeasuredHeight();

        // Layout the TextView in the center of its container, accounting for its new width/height.
        int widthDiff = newWidth - oldWidth;
        int heightDiff = newHeight - oldHeight;
        textView.layout(textView.getLeft() - widthDiff / 2, textView.getTop() - heightDiff / 2,
                textView.getRight() + widthDiff / 2, textView.getBottom() + heightDiff / 2);
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
