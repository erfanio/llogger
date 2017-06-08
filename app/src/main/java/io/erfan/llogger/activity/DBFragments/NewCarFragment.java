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
import io.erfan.llogger.model.Car;
import io.erfan.llogger.model.CarDao;
import io.erfan.llogger.model.DaoSession;

public class NewCarFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_car, container, false);
        final TextInputLayout nameInput = (TextInputLayout) view.findViewById(R.id.new_car_name);
        final TextInputLayout plateInput = (TextInputLayout) view.findViewById(R.id.new_car_plate);
        final ListCarFragment carList = (ListCarFragment) getChildFragmentManager().findFragmentById(R.id.new_car_list);

        // get the car DAO
        DaoSession daoSession = ((App) getActivity().getApplication()).getDaoSession();
        final CarDao carDao = daoSession.getCarDao();

        Button addButton = (Button) view.findViewById(R.id.new_car_add);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameInput.getEditText().getText().toString();
                String plate = plateInput.getEditText().getText().toString();
                if (!name.isEmpty() && !plate.isEmpty()) {
                    Car car = new Car();
                    car.setName(name);
                    car.setPlateNo(plate);

                    carDao.insert(car);
                    Toast.makeText(getContext(),
                            String.format("new car %s with plate %s created!", car.getName(), car.getPlateNo()),
                            Toast.LENGTH_SHORT).show();

                    nameInput.getEditText().setText("");
                    plateInput.getEditText().setText("");

                    carList.updateCars();
                }
            }
        });

        return view;
    }

}
