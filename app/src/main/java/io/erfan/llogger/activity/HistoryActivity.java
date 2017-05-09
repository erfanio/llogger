package io.erfan.llogger.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import org.greenrobot.greendao.query.Query;

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
    Query<Drive> mQuery;

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

        mQuery = mDriveDao.queryBuilder().orderAsc(DriveDao.Properties.MTime).build();
        List<Drive> drives = mQuery.list();

        mRecyclerView = (RecyclerView) findViewById(R.id.history_list);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new DriveRecyclerViewAdapter(drives);
        mRecyclerView.setAdapter(mAdapter);
    }

    public void updateDrives() {
        List<Drive> drives = mQuery.list();
        ((DriveRecyclerViewAdapter) mAdapter).updateList(drives);
    }

}
