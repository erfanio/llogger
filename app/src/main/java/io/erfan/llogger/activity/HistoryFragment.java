package io.erfan.llogger.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.greendao.query.Query;

import java.util.List;

import io.erfan.llogger.App;
import io.erfan.llogger.DriveRecyclerViewAdapter;
import io.erfan.llogger.R;
import io.erfan.llogger.model.DaoSession;
import io.erfan.llogger.model.Drive;
import io.erfan.llogger.model.DriveDao;

public class HistoryFragment extends Fragment {
    DriveDao mDriveDao;
    Query<Drive> mQuery;

    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        // get the drive DAO
        DaoSession daoSession = ((App) getActivity().getApplication()).getDaoSession();
        mDriveDao = daoSession.getDriveDao();

        mQuery = mDriveDao.queryBuilder().orderDesc(DriveDao.Properties.Time).build();
        List<Drive> drives = mQuery.list();

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_list);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new DriveRecyclerViewAdapter(drives);
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    public void updateDrives() {
        List<Drive> drives = mQuery.list();
        ((DriveRecyclerViewAdapter) mAdapter).updateList(drives);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateDrives();
    }
}
