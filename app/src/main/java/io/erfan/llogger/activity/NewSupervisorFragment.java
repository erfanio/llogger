package io.erfan.llogger.activity;

import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import io.erfan.llogger.App;
import io.erfan.llogger.R;
import io.erfan.llogger.model.DaoSession;
import io.erfan.llogger.model.Supervisor;
import io.erfan.llogger.model.SupervisorDao;

public class NewSupervisorFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_supervisor, container, false);
        final TextInputLayout nameInput = (TextInputLayout) view.findViewById(R.id.new_supervisor_name);
        final TextInputLayout licenceInput = (TextInputLayout) view.findViewById(R.id.new_supervisor_licence);

        // get the supervisor DAO
        DaoSession daoSession = ((App) getActivity().getApplication()).getDaoSession();
        final SupervisorDao supervisorDao = daoSession.getSupervisorDao();

        Button addButton = (Button) view.findViewById(R.id.new_supervisor_add);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameInput.getEditText().getText().toString();
                String licence = licenceInput.getEditText().getText().toString();
                if (!name.isEmpty() && !licence.isEmpty()) {
                    Supervisor supervisor = new Supervisor();
                    supervisor.setName(name);
                    supervisor.setLicenceNo(licence);

                    supervisorDao.insert(supervisor);
                    Toast.makeText(getContext(),
                            String.format("new supervisor %s with licence %s created!", supervisor.getName(), supervisor.getLicenceNo()),
                            Toast.LENGTH_SHORT).show();

                    nameInput.getEditText().setText("");
                    licenceInput.getEditText().setText("");
                }
            }
        });

        return view;
    }

}
