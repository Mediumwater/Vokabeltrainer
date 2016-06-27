package de.htwdd.vokabeltrainer;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import java.util.ArrayList;

import de.htwdd.vokabeltrainer.helper.DBHelper;


/**
 * Created by BigT on 27.06.2016.
 */
public class FreitextTrainingActivity extends AppCompatActivity {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_freitext_training);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Taining");
        setSupportActionBar(toolbar);

        Intent i = getIntent();
        Long setid = (Long)i.getSerializableExtra("spinnerselect");

        DBHelper db = new DBHelper(this);

        Log.d("set id in training", setid.toString());


        ArrayList<DBHelper.VocabWord> vw = db.getRandomVocabWord(setid.intValue());

        //ArrayList<Cursor> rw = db.getRandomVocabWords(setid);



        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}