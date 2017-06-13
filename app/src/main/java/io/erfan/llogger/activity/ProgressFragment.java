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
import io.erfan.llogger.PrefMan;
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
        PrefMan prefMan = new PrefMan(getContext());
        Driver driver = ((App) getActivity().getApplication())
                .getDaoSession().getDriverDao().load(prefMan.getUser());

        // get data
        ArrayMap<String, Float> data = StatsUtils.getData(getContext());
        float totalPercent = (data.get(StatsUtils.DAY) + data.get(StatsUtils.NIGHT)) / 120 * 100;
        float dayPercent = data.get(StatsUtils.DAY) / 120 * 100;
        float nightDrivingPercent = data.get(StatsUtils.NIGHT) / 20 * 100;

        // total progress (day progress and night as secondary)
        totalProgress.setProgress(dayPercent);
        // day + night (to appear in front of primary progress
        totalProgress.setSecondaryProgress(totalPercent);
        // night progress
        nightProgress.setProgress(nightDrivingPercent);

        if (totalPercent < 15) {
            progressText.setText(getString(R.string.progress_begin, driver.getName()));
        } else if (totalPercent < 80) {
            progressText.setText(getString(R.string.progress_middle, driver.getName()));
        } else if (totalPercent < 100 || nightDrivingPercent < 100) {
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
