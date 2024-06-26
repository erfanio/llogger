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
import io.erfan.llogger.R;
import io.erfan.llogger.recycleradapters.SupervisorRecyclerViewAdapter;
import io.erfan.llogger.model.DaoSession;
import io.erfan.llogger.model.Supervisor;
import io.erfan.llogger.model.SupervisorDao;

public class ListSupervisorFragment extends Fragment {
    private Query<Supervisor> mQuery;
    private RecyclerView.Adapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recycler_list, container, false);

        // get the supervisor DAO
        DaoSession daoSession = ((App) getActivity().getApplication()).getDaoSession();
        SupervisorDao supervisorDao = daoSession.getSupervisorDao();

        mQuery = supervisorDao.queryBuilder().build();
        List<Supervisor> supervisors = mQuery.list();

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_list);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new SupervisorRecyclerViewAdapter(supervisors);
        recyclerView.setAdapter(mAdapter);

        return view;
    }

    public void updateSupervisors() {
        List<Supervisor> supervisors = mQuery.list();
        ((SupervisorRecyclerViewAdapter) mAdapter).updateList(supervisors);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateSupervisors();
    }
}
