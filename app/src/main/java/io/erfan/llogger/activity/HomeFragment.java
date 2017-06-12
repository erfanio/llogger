package io.erfan.llogger.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.greenrobot.greendao.query.Query;

import java.util.ArrayList;
import java.util.List;

import io.erfan.llogger.App;
import io.erfan.llogger.PreferenceManager;
import io.erfan.llogger.activity.welcome.ProgressFragment;
import io.erfan.llogger.model.DaoSession;
import io.erfan.llogger.model.DriveDao;
import io.erfan.llogger.R;
import io.erfan.llogger.model.Drive;
import io.erfan.llogger.recycleradapters.DriveRecyclerViewAdapter.ViewHolder;

public class HomeFragment extends Fragment {
    Query<Drive> mQuery;
    List<Drive> mDrives;
    List<ViewHolder> mViewHolders;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // setup fragments
        FragmentManager fragmentManager = getChildFragmentManager();
        // piechart
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.home_pie_chart, new PiechartFragment());
        ft.commit();
        // progress
        ft = fragmentManager.beginTransaction();
        ft.replace(R.id.home_progress, new ProgressFragment());
        ft.commit();

        // get driver ID
        PreferenceManager prefMan = new PreferenceManager(view.getContext());
        Long driverId = prefMan.getUser();

        // get the drive DAO
        DaoSession daoSession = ((App) getActivity().getApplication()).getDaoSession();
        DriveDao driveDao = daoSession.getDriveDao();
        // prepare the query to get drives
        mQuery = driveDao.queryBuilder().where(DriveDao.Properties.DriverId.eq(driverId))
                .orderDesc(DriveDao.Properties.Time).limit(4).build();
        mDrives = mQuery.list();
        mViewHolders = new ArrayList<>();

        // inflate drive list items
        inflateList(mDrives.size(), view);
        bindList();

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
                ((RootActivity) getActivity()).switchFragment(RootActivity.Pages.HISTORY, true);
            }
        });

        return view;
    }

    private void inflateList(int count, View view) {
        // inflate list items and add it to the list LinearLayout
        LinearLayout historyList = (LinearLayout) view.findViewById(R.id.main_history_list);
        for (int i = 0; i < count; i++) {
            View listItem = LayoutInflater.from(getActivity()).inflate(R.layout.list_item_drive, historyList, false);
            mViewHolders.add(new ViewHolder(listItem));
            historyList.addView(listItem);
        }
    }

    private void bindList() {
        for (int i = 0; i < mViewHolders.size(); i++) {
            mViewHolders.get(i).bind(mDrives.get(i));
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // remember the current length
        int prevLen = mDrives.size();

        mDrives = mQuery.list();

        if (mDrives.size() > prevLen) {
            // inflate a more drive list items to have enough to display all drives
            inflateList(mDrives.size() - prevLen, getView());
            bindList();
        }
    }
}
