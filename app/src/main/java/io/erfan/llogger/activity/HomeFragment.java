package io.erfan.llogger.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
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

public class HomeFragment extends Fragment {

    DriveDao mDriveDao;
    Query<Drive> mQuery;

    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.new_drive_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), NewDriveActivity.class);
                startActivity(intent);
            }
        });

        CardView loadMore = (CardView) view.findViewById(R.id.main_load_more);
        loadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((RootActivity) getActivity()).switch_fragment(RootActivity.Fragments.HISTORY);
            }
        });

        // get the drive DAO
        DaoSession daoSession = ((App) getActivity().getApplication()).getDaoSession();
        mDriveDao = daoSession.getDriveDao();

        mQuery = mDriveDao.queryBuilder().orderDesc(DriveDao.Properties.Time).build();

        mRecyclerView = (RecyclerView) view.findViewById(R.id.main_history_list);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new DriveRecyclerViewAdapter(getShortDriveList());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setNestedScrollingEnabled(false);

        return view;
    }

    public List<Drive> getShortDriveList() {
        List<Drive> allDrives = mQuery.list();

        if (allDrives.size() >= 4) {
            return allDrives.subList(0, 4);
        } else {
            return allDrives.subList(0, allDrives.size());
        }
    }

    public void updateDrives() {
        ((DriveRecyclerViewAdapter) mAdapter).updateList(getShortDriveList());
    }


    @Override
    public void onResume() {
        super.onResume();
        updateDrives();
    }
}
