package io.erfan.llogger.activity.DBFragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import io.erfan.llogger.model.DaoSession;
import io.erfan.llogger.model.Driver;
import io.erfan.llogger.model.DriverDao;

public class NewDriverFragment extends Fragment {
    TextInputLayout mNameInput;
    SelectDriverFragment mDriverList;
    DriverDao mDriverDao;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_driver, container, false);

        mNameInput = (TextInputLayout) view.findViewById(R.id.new_driver_name);
        mDriverList = new SelectDriverFragment();
        Button addButton = (Button) view.findViewById(R.id.new_driver_add);

        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.new_driver_fragment, mDriverList);
        ft.commit();

        // get the driver DAO
        DaoSession daoSession = ((App) getActivity().getApplication()).getDaoSession();
        mDriverDao = daoSession.getDriverDao();

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDriver();
            }
        });
        mNameInput.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    addDriver();
                    handled = true;
                }
                return handled;
            }
        });

        return view;
    }

    void addDriver() {
        String name = mNameInput.getEditText().getText().toString();
        if (!name.isEmpty()) {
            Driver driver = new Driver();
            driver.setName(name);
            mDriverDao.insert(driver);

            // Check if no view has focus:
            View view = getActivity().getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

            mDriverList.updateDrivers();
        }
    }
}
