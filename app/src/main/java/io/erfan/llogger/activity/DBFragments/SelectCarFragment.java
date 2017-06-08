package io.erfan.llogger.activity.DBFragments;

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
import io.erfan.llogger.R;
import io.erfan.llogger.CarRecyclerViewAdapter;
import io.erfan.llogger.model.DaoSession;
import io.erfan.llogger.model.Car;
import io.erfan.llogger.model.CarDao;

public class SelectCarFragment extends Fragment {
    Query<Car> mQuery;
    RecyclerView.Adapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recycler_list, container, false);

        // get the car DAO
        DaoSession daoSession = ((App) getActivity().getApplication()).getDaoSession();
        CarDao carDao = daoSession.getCarDao();

        mQuery = carDao.queryBuilder().build();
        List<Car> cars = mQuery.list();

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_list);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new CarRecyclerViewAdapter(cars);
        recyclerView.setAdapter(mAdapter);

        return view;
    }

    public void updateCars() {
        List<Car> cars = mQuery.list();
        ((CarRecyclerViewAdapter) mAdapter).updateList(cars);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateCars();
    }
}