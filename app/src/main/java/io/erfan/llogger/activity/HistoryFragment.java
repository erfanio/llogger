package io.erfan.llogger.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.greendao.query.Query;

import java.util.List;

import io.erfan.llogger.App;
import io.erfan.llogger.PrefMan;
import io.erfan.llogger.recycleradapters.DriveRecyclerViewAdapter;
import io.erfan.llogger.R;
import io.erfan.llogger.model.DaoSession;
import io.erfan.llogger.model.Drive;
import io.erfan.llogger.model.DriveDao;

public class HistoryFragment extends Fragment {
    private Query<Drive> mQuery;
    private RecyclerView.Adapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recycler_list, container, false);

        // get driver ID
        PrefMan prefMan = new PrefMan(view.getContext());
        Long driverId = prefMan.getUser();

        // get the drive DAO
        DaoSession daoSession = ((App) getActivity().getApplication()).getDaoSession();
        DriveDao driveDao = daoSession.getDriveDao();
        // get the list of drives
        mQuery = driveDao.queryBuilder().where(DriveDao.Properties.DriverId.eq(driverId))
                .orderDesc(DriveDao.Properties.Time).build();
        List<Drive> drives = mQuery.list();

        // setup recycler adapter with a list of driver (maybe limited to a number in getDrives)
        RecyclerView  recyclerView = (RecyclerView) view.findViewById(R.id.recycler_list);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new DriveRecyclerViewAdapter(drives, new OnDriveItemClickListenerFromHistory());
        recyclerView.setAdapter(mAdapter);


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // update the list
        List<Drive> drives = mQuery.list();
        ((DriveRecyclerViewAdapter) mAdapter).updateList(drives);
    }

    private class OnDriveItemClickListenerFromHistory implements DriveRecyclerViewAdapter.OnDriveItemClickListener {
        @Override
        public void onClick(Long driveId, View durationView, View carView) {
            Intent openViewDrive = new Intent(getContext(), DriveDetailActivity.class);
            openViewDrive.putExtra("DriveId", driveId);

            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    getActivity(),
                    new Pair<>(durationView, ViewCompat.getTransitionName(durationView)),
                    new Pair<>(carView, ViewCompat.getTransitionName(carView)));

            startActivity(openViewDrive, options.toBundle());
        }
    }
}
