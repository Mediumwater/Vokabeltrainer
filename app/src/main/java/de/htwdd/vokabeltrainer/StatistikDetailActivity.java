package de.htwdd.vokabeltrainer;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

import de.htwdd.vokabeltrainer.helper.LanguageHelper;

public class StatistikDetailActivity extends AppCompatActivity {

    public PieChart pc;

    public Intent intent;
    public String description;
    public String lang1;
    public String lang2;
    public int hits;
    public int misses;
    public double ratio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistik_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        intent = getIntent();
        description = intent.getStringExtra("Description");
        lang1 = intent.getStringExtra("Lang1");
        lang2 = intent.getStringExtra("Lang2");
        hits = intent.getIntExtra("Hits", 0);
        misses = intent.getIntExtra("Misses", 0);
        ratio = intent.getDoubleExtra("Ratio", 0);


        pc = (PieChart) findViewById(R.id.chart);

        pc.setHoleColor(Color.TRANSPARENT);
        pc.getLegend().setEnabled(false);
        pc.setDrawCenterText(true);
        LanguageHelper lh = new LanguageHelper();
        pc.setCenterText(description + " \n" + lh.getLanguageNameByCode(lang1) + " - " + lh.getLanguageNameByCode(lang2));
        pc.setDescription("");

        setDataPie();

        pc.animateY(1400, Easing.EasingOption.EaseInOutQuad);

        TextView tv_hits = (TextView) findViewById(R.id.textView11);
        TextView tv_misses = (TextView) findViewById(R.id.textView13);

        tv_hits.setText("" + hits);
        tv_misses.setText("" + misses);

    }

    private void setDataPie(/*int count, float range*/) {

        //float mult = range;

        ArrayList<Entry> yVals1 = new ArrayList<Entry>();

        // IMPORTANT: In a PieChart, no values (Entry) should have the same
        // xIndex (even if from different DataSets), since no values can be
        // drawn above each other.
        /*for (int i = 0; i < count + 1; i++) {
            yVals1.add(new Entry((float) (Math.random() * mult) + mult / 5, i));
        }*/

        yVals1.add(new Entry((float) (ratio*100), 0));
        yVals1.add(new Entry((float) (100-(ratio*100)), 1));

        ArrayList<String> xVals = new ArrayList<String>();

        /*for (int i = 0; i < count + 1; i++)
            xVals.add("" + i);*/

        xVals.add("");
        xVals.add("");

        PieDataSet dataSet = new PieDataSet(yVals1, description);
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<Integer>();

        /*for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);*/

        //colors.add(ColorTemplate.getHoloBlue());

        colors.add(Color.argb(255, 81, 209, 103));
        colors.add(Color.argb(255, 215, 55, 55));

        dataSet.setColors(colors);
        //dataSet.setSelectionShift(0f);

        PieData data = new PieData(xVals, dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
        //data.setValueTypeface(tf);
        pc.setData(data);

        // undo all highlights
        pc.highlightValues(null);

    }

}
