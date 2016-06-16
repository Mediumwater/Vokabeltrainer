package de.htwdd.vokabeltrainer;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

import de.htwdd.vokabeltrainer.helper.DBHelper;
import de.htwdd.vokabeltrainer.helper.LanguageHelper;

public class StatistikActivity extends AppCompatActivity implements OnChartValueSelectedListener {

    public HorizontalBarChart bc;
    public ArrayList<DBHelper.VocabSet> vocabsets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistik);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        bc = (HorizontalBarChart) findViewById(R.id.bchart);
        bc.setOnChartValueSelectedListener(this);

        bc.setDescription("");
        bc.setDrawGridBackground(false);
        bc.getLegend().setEnabled(false);
        //bc.setDrawValueAboveBar(true);

        YAxis yl = bc.getAxisLeft();
        yl.setAxisMinValue(0);
        yl.setAxisMaxValue(100);

        YAxis yr = bc.getAxisRight();
        yr.setEnabled(false);

        XAxis xa = bc.getXAxis();
        xa.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);

        setDataBar();

        bc.animateY(1000, Easing.EasingOption.EaseInOutQuad);
    }



    private void setDataBar(/*int count, int range*/) {

        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
        ArrayList<String> xVals = new ArrayList<String>();

        DBHelper db = new DBHelper(this);
        vocabsets = db.getAllVocabSetsWithRatio();
        LanguageHelper lh = LanguageHelper.getInstance();

        /*for (int i = 0; i < count; i++) {
            xVals.add("" + i);
            yVals1.add(new BarEntry((float) (Math.random() * range), i));
        }*/

        int i = 0;
        for (DBHelper.VocabSet e : vocabsets) {
            xVals.add(e.description + " (" + lh.getLanguageNameByCode(e.lang1) + " - " + lh.getLanguageNameByCode(e.lang2) + ")");
            yVals1.add(new BarEntry((float) (e.ratio * 100), i));
            i++;
        }

        BarDataSet set1;

        if (bc.getData() != null &&
                bc.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet)bc.getData().getDataSetByIndex(0);
            set1.setYVals(yVals1);
            bc.getData().setXVals(xVals);
            bc.getData().notifyDataChanged();
            bc.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(yVals1, "DataSet 1");

            set1.setColor(Color.argb(255, 255, 218, 112));

            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(set1);

            BarData data = new BarData(xVals, dataSets);
            data.setValueTextSize(10f);
            data.setValueFormatter(new PercentFormatter());
            data.setDrawValues(false);
            //data.setValueTypeface(tf);

            bc.setData(data);
        }

        //größe der Balken immer gleich, indem die Höhe des gesamten Charts immer an die Anzahl der Balken angepasst wird
        HorizontalBarChart.LayoutParams params = bc.getLayoutParams();
        params.height = 200 * i;
        bc.setLayoutParams(params);

    }

    /**
     * Called when a value has been selected inside the chart.
     *
     * @param e The selected Entry.
     * @param dataSetIndex The index in the datasets array of the data object
     * the Entrys DataSet is in.
     * @param h the corresponding highlight object that contains information
     * about the highlighted position
     */
    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
        DBHelper.VocabSet vs = vocabsets.get(e.getXIndex());

        Intent startIntent;
        startIntent = new Intent(getApplicationContext(), StatistikDetailActivity.class);

        startIntent.putExtra("Description", vs.description);
        startIntent.putExtra("Lang1", vs.lang1);
        startIntent.putExtra("Lang2", vs.lang2);
        startIntent.putExtra("Hits", vs.hits);
        startIntent.putExtra("Misses", vs.misses);
        startIntent.putExtra("Ratio", vs.ratio);

        startActivity(startIntent);
    }

    /**
     * Called when nothing has been selected or an "un-select" has been made.
     */
    @Override
    public void onNothingSelected() {
        //wird theoretisch für unsere Zwecke nicht gebraucht
    }
}
