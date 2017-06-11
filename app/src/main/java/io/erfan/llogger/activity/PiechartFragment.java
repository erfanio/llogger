package io.erfan.llogger.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

public class PiechartFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_piechart, container, false);

        PieChart pieChart = (PieChart) view.findViewById(R.id.pie_chart);
        pieChart.getDescription().setEnabled(false);
        pieChart.getLegend().setEnabled(false);

        pieChart.setCenterText(generateCenterText());
        pieChart.setCenterTextSize(10f);

        // radius of the center hole in percent of maximum radius
        pieChart.setHoleRadius(45f);
        pieChart.setTransparentCircleRadius(50f);

        pieChart.setData(generatePieData());
        pieChart.setEntryLabelColor(Color.WHITE);

        return view;
    }

    private SpannableString generateCenterText() {
        SpannableString s = new SpannableString("100\nTotal Hours");
        s.setSpan(new RelativeSizeSpan(3f), 0, 3, 0);
        s.setSpan(new ForegroundColorSpan(Color.GRAY), 3, s.length(), 0);
        return s;
    }

    private PieData generatePieData() {
        ArrayList<PieEntry> entries = new ArrayList<>();

        entries.add(new PieEntry(50f, "Day"));
        entries.add(new PieEntry(11f, "Night"));

        PieDataSet dataSet = new PieDataSet(entries, "Hours");
        dataSet.setColors(Color.parseColor("#42A5F5"), Color.parseColor("#0D47A1"));
        dataSet.setSliceSpace(2f);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTextSize(12f);

        return new PieData(dataSet);
    }
}
