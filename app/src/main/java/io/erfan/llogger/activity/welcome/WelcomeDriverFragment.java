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
import io.erfan.llogger.model.DriverDao;

public class WelcomeDriverFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_welcome_driver, container, false);

        // get the driver DAO
        DaoSession daoSession = ((App) getActivity().getApplication()).getDaoSession();
        final DriverDao driverDao = daoSession.getDriverDao();

        Button button = (Button) view.findViewById(R.id.welcome_driver_continue);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (driverDao.count() > 0) {
                ((WelcomeActivity) getActivity()).nextPage();
            } else {
                Toast.makeText(getContext(), R.string.haveto_create_driver, Toast.LENGTH_SHORT).show();
            }
            }
        });

        return view;
    }
}
