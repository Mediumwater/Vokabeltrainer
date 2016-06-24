package de.htwdd.vokabeltrainer;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

import de.htwdd.vokabeltrainer.helper.DBHelper;
import de.htwdd.vokabeltrainer.helper.LanguageHelper;
import de.htwdd.vokabeltrainer.helper.VocabDownloadHelper;

/**
 * Created by Curtis on 24.06.2016.
 */
public class MultiChoiceActivity extends AppCompatActivity implements View.OnClickListener {
    private ArrayList<VocabDownloadHelper.DownloadableVocSet> dvs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multichoice);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Multiple Choice");
        setSupportActionBar(toolbar);

        TextView txt1 = (TextView) findViewById(R.id.choice1);
        txt1.setOnClickListener(this);
        TextView txt2 = (TextView) findViewById(R.id.choice2);
        txt2.setOnClickListener(this);
        TextView txt3 = (TextView) findViewById(R.id.choice3);
        txt3.setOnClickListener(this);
        TextView txt4 = (TextView) findViewById(R.id.choice4);
        txt4.setOnClickListener(this);


        ArrayList<DBHelper.VocabSet> vocabsets;
        ArrayList<Cursor> rndvoc;


        DBHelper db = new DBHelper(this);
        vocabsets = db.getAllVocabSetsWithRatio();




        rndvoc = db.getRandomVocabWords();

        rndvoc

        LanguageHelper lh = LanguageHelper.getInstance();





        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public void onClick(View view) {
        // do whatever you want here based on the view being passed
        TextView v = (TextView) view;
        switch(v.getId()) {
            case R.id.choice1:
                view.setBackgroundColor(Color.GREEN);
                break;
            case R.id.choice2:
                view.setBackgroundColor(Color.GREEN);
                break;
            case R.id.choice3:
                view.setBackgroundColor(Color.GREEN);
                break;
            case R.id.choice4:
                view.setBackgroundColor(Color.GREEN);
                break;
        }
    }
}