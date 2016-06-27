package de.htwdd.vokabeltrainer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.github.mikephil.charting.data.BarEntry;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.List;

import de.htwdd.vokabeltrainer.helper.DBHelper;

/**
 * Created by BigT on 27.06.2016.
 */
public class FreitextActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public long selectedSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_freitext);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Freitext");
        setSupportActionBar(toolbar);

        final Spinner setspinner = (Spinner) findViewById(R.id.sset);
        setspinner.setOnItemSelectedListener(this);

        Button bgo = (Button) findViewById(R.id.bsetcheck);
        bgo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startIntent = new Intent(getApplicationContext(), FreitextTrainingActivity.class);
                startIntent.putExtra("spinnerselect", setspinner.getSelectedItemId());
                startActivity(startIntent);
            }
        });

        DBHelper db = new DBHelper(this);
        ArrayList<DBHelper.VocabSet> vocabsets;
        vocabsets = db.getAllVocabSetsWithRatio();
        List<String> setList = new ArrayList<String>();

        /* TODO
         * Sets nach Ratio einfärben (rot =  ungeübt, grün = viel geübt, hellblau mittel, dunkelblau = unteres mittel )
         */

        for (DBHelper.VocabSet e : vocabsets) {
            setList.add(e.description + " " + e.lang1 + " - " + e.lang2);
            //Log.d("DEBUGaaa", e.description +" "+ e.lang1 + " " + e.lang2 +" " + Integer.toString(e.hits) +" " + Integer.toString(e.misses) );
            //al.add(new VocabSet(cur.getString(0), cur.getString(1), cur.getString(2), cur.getInt(3), cur.getInt(4)));
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, setList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        setspinner.setAdapter(dataAdapter);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}