package io.erfan.llogger.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;

import io.erfan.llogger.App;
import io.erfan.llogger.PreferenceManager;
import io.erfan.llogger.R;
import io.erfan.llogger.StatsUtils;
import io.erfan.llogger.model.DaoSession;
import io.erfan.llogger.model.Drive;
import io.erfan.llogger.model.DriveDao;

public class PiechartFragment extends Fragment {
    // these will be used to cache (close enough) the data so we don't hit the database everytime

    private PieChart mPiechart;
    private int mChartTextColor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_piechart, container, false);

        mPiechart = (PieChart) view.findViewById(R.id.pie_chart);
        // no need for legend or description
        mPiechart.getDescription().setEnabled(false);
        mPiechart.getLegend().setEnabled(false);
        // disables interactions with the chart
        mPiechart.setTouchEnabled(false);
        // make the text on the chart white
        mChartTextColor = Color.WHITE;
        mPiechart.setEntryLabelColor(mChartTextColor);

        // setup the data
        mPiechart.setData(generatePieData());

        mPiechart.setCenterText(generateCenterText());
        mPiechart.setCenterTextSize(10f);

        // radius of the center hole in percent of maximum radius
        mPiechart.setHoleRadius(45f);
        mPiechart.setTransparentCircleRadius(50f);


        return view;
    }

    private SpannableString generateCenterText() {
        String totalHoursString = String.valueOf(StatsUtils.getTotalHours(getContext()));
        int totalHoursLen = totalHoursString.length();

        SpannableString s = new SpannableString(String.format("%s\nTotal Hours", totalHoursString));
        s.setSpan(new RelativeSizeSpan(3f), 0, totalHoursLen, 0);
        s.setSpan(new ForegroundColorSpan(Color.GRAY), totalHoursLen, s.length(), 0);
        return s;
    }

    private PieData generatePieData() {
        ArrayList<PieEntry> entries = new ArrayList<>();

        ArrayMap<String, Float> data = StatsUtils.getData(getContext());
        for (String key : data.keySet()) {
            entries.add(new PieEntry(data.get(key), key));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Hours");
        dataSet.setColors(Color.parseColor("#42A5F5"), Color.parseColor("#0D47A1"));
        dataSet.setSliceSpace(2f);
        dataSet.setValueTextColor(mChartTextColor);
        dataSet.setValueTextSize(12f);

        return new PieData(dataSet);
    }


}
