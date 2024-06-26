package io.erfan.llogger.activity.asset;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.greendao.query.Query;

import java.util.List;

import io.erfan.llogger.App;
import io.erfan.llogger.recycleradapters.DriverRecyclerViewAdapter;
import io.erfan.llogger.R;
import io.erfan.llogger.model.DaoSession;
import io.erfan.llogger.model.Driver;
import io.erfan.llogger.model.DriverDao;

public class ListDriverFragment extends Fragment {
    private Query<Driver> mQuery;
    private RecyclerView.Adapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recycler_list, container, false);

        // get the driver DAO
        DaoSession daoSession = ((App) getActivity().getApplication()).getDaoSession();
        DriverDao driverDao = daoSession.getDriverDao();

        mQuery = driverDao.queryBuilder().build();
        List<Driver> drivers = mQuery.list();

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_list);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new DriverRecyclerViewAdapter(drivers);
        recyclerView.setAdapter(mAdapter);

        return view;
    }

    public void updateDrivers() {
        List<Driver> drivers = mQuery.list();
        ((DriverRecyclerViewAdapter) mAdapter).updateList(drivers);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateDrivers();
    }
}
