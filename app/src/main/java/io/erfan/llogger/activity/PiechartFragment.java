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

import io.erfan.llogger.R;
import io.erfan.llogger.StatsUtils;

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
        mPiechart.setCenterTextSize(10f);

        // setup the data
        mPiechart.setData(generatePieData());
        mPiechart.setCenterText(generateCenterText());

        // radius of the center hole in percent of maximum radius
        mPiechart.setHoleRadius(45f);
        mPiechart.setTransparentCircleRadius(50f);


        return view;
    }

    private SpannableString generateCenterText() {
        // get the total hours (and calculate its length)
        String totalHours = String.valueOf(StatsUtils.getTotalHours(getContext()));
        int totalHoursLen = totalHours.length();

        // create a spannable text where the hours is three times bigger than the rest of the text
        SpannableString s = new SpannableString(String.format("%s\nTotal Hours", totalHours));
        s.setSpan(new RelativeSizeSpan(3f), 0, totalHoursLen, 0);
        s.setSpan(new ForegroundColorSpan(Color.GRAY), totalHoursLen, s.length(), 0);
        return s;
    }

    private PieData generatePieData() {
        ArrayList<PieEntry> entries = new ArrayList<>();

        ArrayMap<String, Float> data = StatsUtils.getData(getContext());

        // add entries in order (day then night)
        entries.add(new PieEntry(data.get(StatsUtils.DAY), getString(R.string.day)));
        entries.add(new PieEntry(data.get(StatsUtils.NIGHT), getString(R.string.night)));


        // construct the data set from the data
        PieDataSet dataSet = new PieDataSet(entries, getString(R.string.hours));
        // first color for day, second for night
        dataSet.setColors(getContext().getColor(R.color.day), getContext().getColor(R.color.night));

        // formatting
        dataSet.setSliceSpace(2f);
        dataSet.setValueTextColor(mChartTextColor);
        dataSet.setValueTextSize(12f);

        return new PieData(dataSet);
    }

    @Override
    public void onResume() {
        super.onResume();
        // refresh data
        mPiechart.setData(generatePieData());
        mPiechart.setCenterText(generateCenterText());
    }
}
