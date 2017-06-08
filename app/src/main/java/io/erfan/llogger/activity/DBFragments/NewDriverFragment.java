package io.erfan.llogger.activity.DBFragments;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import io.erfan.llogger.App;
import io.erfan.llogger.R;
import io.erfan.llogger.model.DaoSession;
import io.erfan.llogger.model.Driver;
import io.erfan.llogger.model.DriverDao;

public class NewDriverFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_driver, container, false);
        final TextInputLayout nameInput = (TextInputLayout) view.findViewById(R.id.new_driver_name);
        final SelectDriverFragment driverList = (SelectDriverFragment) getChildFragmentManager().findFragmentById(R.id.new_driver_list);

        // get the driver DAO
        DaoSession daoSession = ((App) getActivity().getApplication()).getDaoSession();
        final DriverDao driverDao = daoSession.getDriverDao();

        Button addButton = (Button) view.findViewById(R.id.new_driver_add);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameInput.getEditText().getText().toString();
                if (!name.isEmpty()) {
                    Driver driver = new Driver();
                    driver.setName(name);
                    driverDao.insert(driver);
                    Toast.makeText(getContext(), String.format("new driver %s created!", driver.getName()), Toast.LENGTH_SHORT).show();
                    nameInput.getEditText().setText("");

                    driverList.updateDrivers();
                }
            }
        });

        return view;
    }

}
