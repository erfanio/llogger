package io.erfan.llogger.activity.DBFragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import io.erfan.llogger.App;
import io.erfan.llogger.R;
import io.erfan.llogger.model.Car;
import io.erfan.llogger.model.CarDao;
import io.erfan.llogger.model.DaoSession;

public class NewCarFragment extends Fragment {
    private TextInputLayout mNameInput;
    private TextInputLayout mPlateInput;
    private ListCarFragment mCarList;
    private CarDao mCarDao;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_car, container, false);

        mNameInput = (TextInputLayout) view.findViewById(R.id.new_car_name);
        mPlateInput = (TextInputLayout) view.findViewById(R.id.new_car_plate);
        mCarList = (ListCarFragment) getChildFragmentManager().findFragmentById(R.id.new_car_list);
        Button addButton = (Button) view.findViewById(R.id.new_car_add);

        // get the car DAO
        DaoSession daoSession = ((App) getActivity().getApplication()).getDaoSession();
        mCarDao = daoSession.getCarDao();

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCar();
            }
        });
        mPlateInput.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    addCar();
                    handled = true;
                }
                return handled;
            }
        });

        return view;
    }

    public void addCar() {
        String name = mNameInput.getEditText().getText().toString();
        String plate = mPlateInput.getEditText().getText().toString();
        if (!name.isEmpty() && !plate.isEmpty()) {
            Car car = new Car();
            car.setName(name);
            car.setPlateNo(plate);

            mCarDao.insert(car);
            Toast.makeText(getContext(),
                    String.format("new car %s with plate %s created!", car.getName(), car.getPlateNo()),
                    Toast.LENGTH_SHORT).show();

            mNameInput.getEditText().setText("");
            mPlateInput.getEditText().setText("");

            // Check if no view has focus:
            View view = getActivity().getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

            mCarList.updateCars();
        }
    }

}
