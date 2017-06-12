package io.erfan.llogger.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;

import io.erfan.llogger.App;
import io.erfan.llogger.Preference;
import io.erfan.llogger.R;
import io.erfan.llogger.StatsUtils;
import io.erfan.llogger.model.Driver;

public class ProgressFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_progress, container, false);

        setupUI(view);

        return view;
    }

    private void setupUI(View view) {
        // UI
        TextView progressText = (TextView) view.findViewById(R.id.progress_text);
        RoundCornerProgressBar totalProgress = (RoundCornerProgressBar) view.findViewById(R.id.progress_total);
        RoundCornerProgressBar nightProgress = (RoundCornerProgressBar) view.findViewById(R.id.progress_night);

        // load the current user
        Preference prefMan = new Preference(getContext());
        Driver driver = ((App) getActivity().getApplication())
                .getDaoSession().getDriverDao().load(prefMan.getUser());

        // get data
        ArrayMap<String, Float> data = StatsUtils.getData(getContext());
        float dayPercent = data.get(StatsUtils.DAY) / 120 * 100;
        float nightPercent = data.get(StatsUtils.NIGHT) / 20 * 100;

        // total progress (day progress and night as secondary)
        totalProgress.setProgress(dayPercent);
        totalProgress.setSecondaryProgress(nightPercent);
        // night progress
        nightProgress.setProgress(nightPercent);

        int totalHours = StatsUtils.getTotalHours(getContext());

        if (totalHours < 20) {
            progressText.setText(getString(R.string.progress_begin, driver.getName()));
        } else if (totalHours < 100) {
            progressText.setText(getString(R.string.progress_middle, driver.getName()));
        } else if (totalHours < 120 || nightPercent < 100) {
            progressText.setText(getString(R.string.progress_end, driver.getName()));
        } else {
            progressText.setText(getString(R.string.progress_finish, driver.getName()));
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        setupUI(getView());
    }
}
