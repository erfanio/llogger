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
import io.erfan.llogger.model.DaoSession;
import io.erfan.llogger.model.Supervisor;
import io.erfan.llogger.model.SupervisorDao;

public class NewSupervisorFragment extends Fragment {
    SupervisorDao mSupervisorDao;
    TextInputLayout mNameInput;
    TextInputLayout mLicenceInput;
    ListSupervisorFragment mSupervisorList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_supervisor, container, false);
        mNameInput = (TextInputLayout) view.findViewById(R.id.new_supervisor_name);
        mLicenceInput = (TextInputLayout) view.findViewById(R.id.new_supervisor_licence);
        mSupervisorList = (ListSupervisorFragment) getChildFragmentManager().findFragmentById(R.id.new_supervisor_list);

        // get the supervisor DAO
        DaoSession daoSession = ((App) getActivity().getApplication()).getDaoSession();
        mSupervisorDao = daoSession.getSupervisorDao();

        Button addButton = (Button) view.findViewById(R.id.new_supervisor_add);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSupervisor();
            }
        });
        mLicenceInput.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    addSupervisor();
                    handled = true;
                }
                return handled;
            }
        });

        return view;
    }

    private void addSupervisor() {
        String name = mNameInput.getEditText().getText().toString();
        String licence = mLicenceInput.getEditText().getText().toString();
        if (!name.isEmpty() && !licence.isEmpty()) {
            Supervisor supervisor = new Supervisor();
            supervisor.setName(name);
            supervisor.setLicenceNo(licence);

            mSupervisorDao.insert(supervisor);
            Toast.makeText(getContext(),
                    String.format("new supervisor %s with licence %s created!", supervisor.getName(), supervisor.getLicenceNo()),
                    Toast.LENGTH_SHORT).show();

            mNameInput.getEditText().setText("");
            mLicenceInput.getEditText().setText("");

            // Check if no view has focus:
            View view = getActivity().getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

            mSupervisorList.updateSupervisors();
        }
    }
}
