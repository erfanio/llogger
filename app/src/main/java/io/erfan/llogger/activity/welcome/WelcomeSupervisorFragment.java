package io.erfan.llogger.activity.welcome;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import io.erfan.llogger.App;
import io.erfan.llogger.R;
import io.erfan.llogger.activity.WelcomeActivity;
import io.erfan.llogger.model.DaoSession;
import io.erfan.llogger.model.SupervisorDao;

public class WelcomeSupervisorFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_welcome_supervisor, container, false);

        // get the supervisor DAO
        DaoSession daoSession = ((App) getActivity().getApplication()).getDaoSession();
        final SupervisorDao supervisorDao = daoSession.getSupervisorDao();

        Button button = (Button) view.findViewById(R.id.welcome_supervisor_continue);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (supervisorDao.count() > 0) {
                    ((WelcomeActivity) getActivity()).nextPage();
                } else {
                    Toast.makeText(getContext(), "Please create a supervisor before proceeding!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }
}
