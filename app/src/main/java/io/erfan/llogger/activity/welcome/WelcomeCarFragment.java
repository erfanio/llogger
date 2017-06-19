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
import io.erfan.llogger.model.CarDao;
import io.erfan.llogger.model.DaoSession;

public class WelcomeCarFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_welcome_car, container, false);

        // get the car DAO
        DaoSession daoSession = ((App) getActivity().getApplication()).getDaoSession();
        final CarDao carDao = daoSession.getCarDao();

        Button button = (Button) view.findViewById(R.id.welcome_car_done);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (carDao.count() > 0) {
                    ((WelcomeActivity) getActivity()).done();
                } else {
                    Toast.makeText(getContext(), R.string.haveto_create_car, Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }
}
