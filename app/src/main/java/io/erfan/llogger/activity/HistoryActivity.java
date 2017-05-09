package io.erfan.llogger.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.erfan.llogger.App;
import io.erfan.llogger.DriveRecyclerViewAdapter;
import io.erfan.llogger.R;
import io.erfan.llogger.model.DaoSession;
import io.erfan.llogger.model.Drive;
import io.erfan.llogger.model.DriveDao;

public class HistoryActivity extends AppCompatActivity {
    DriveDao mDriveDao;

    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        Toolbar toolbar = (Toolbar) findViewById(R.id.history_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // get the drive DAO
        DaoSession daoSession = ((App) getApplication()).getDaoSession();
        mDriveDao = daoSession.getDriveDao();

        List<Drive> drives = new ArrayList<>();
        drives.add(new Drive((long) 0, (long) 1440, "Clayton", "Dad's Car", "Dad", new Date(), Drive.Light.DAY, Drive.Traffic.MEDIUM, Drive.Weather.DRY));
        drives.add(new Drive((long) 0, (long) 1440, "Clayton", "Dad's Car", "Dad", new Date(), Drive.Light.DAY, Drive.Traffic.MEDIUM, Drive.Weather.DRY));
        drives.add(new Drive((long) 0, (long) 1623, "Clayton", "Dad's Car", "Dad", new Date(), Drive.Light.DAY, Drive.Traffic.MEDIUM, Drive.Weather.DRY));
        drives.add(new Drive((long) 0, (long) 3214, "Clayton", "Dad's Car", "Dad", new Date(), Drive.Light.DAY, Drive.Traffic.MEDIUM, Drive.Weather.DRY));
        drives.add(new Drive((long) 0, (long) 1440, "Clayton", "Dad's Car", "Dad", new Date(), Drive.Light.DAY, Drive.Traffic.MEDIUM, Drive.Weather.DRY));
        drives.add(new Drive((long) 0, (long) 1440, "Clayton", "Dad's Car", "Dad", new Date(), Drive.Light.DAY, Drive.Traffic.MEDIUM, Drive.Weather.DRY));
        drives.add(new Drive((long) 0, (long) 9834, "Clayton", "Dad's Car", "Dad", new Date(), Drive.Light.DAY, Drive.Traffic.MEDIUM, Drive.Weather.DRY));

        mRecyclerView = (RecyclerView) findViewById(R.id.history_list);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new DriveRecyclerViewAdapter(drives);
        mRecyclerView.setAdapter(mAdapter);
    }

}
