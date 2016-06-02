package de.htwdd.vokabeltrainer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import de.htwdd.vokabeltrainer.helper.DBHelper;

public class VokverActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vokver);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startIntent;
                startIntent = new Intent(getApplicationContext(), AddVokabelsetActivity.class);
                startActivity(startIntent);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Log.d("DEBUG", "Hier könnte Liste befuellt werden.");
        DBHelper db = new DBHelper(this);
        ArrayList array_list = db.getAllVocabSets();
        //ArrayList array_list = db.getAllWords();

        ArrayAdapter arrayAdapter=new ArrayAdapter(this, android.R.layout.simple_list_item_1, array_list);
        ListView lv = (ListView)findViewById(R.id.listView);
        lv.setAdapter(arrayAdapter);

        Log.d("DEBUG", Integer.toString(db.getVocabGroupCount(1)));

        //ArrayList<Cursor> vocs = db.getVocabWords(1, 1);
        ArrayList<Cursor> vocs = db.getRandomVocabWords(1);

        for (int i = 0; i < vocs.size(); i++) {
            Log.d("DEBUG", "* Woerter Sprache " + Integer.toString(i));

            while (vocs.get(i).isAfterLast() == false) {
                Log.d("DEBUG", vocs.get(i).getString(1));
                vocs.get(i).moveToNext();
            }
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.vokverw, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_download) {
            //=====================================================================================================
            //Download verfügbarer Sets auswählen und starten
            //=====================================================================================================
            /**
             * TODO: Verfügbare Downloads dynamisch abrufen und bei Klick herunterladen und in DB speichern
             */
            String choice_download[]= {"Advanced Business", "Advanced Alltag", "technisches Englisch", "Präsentationen", "Verhandlungen", "verschiedene Fachbegriffe"};
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Sprachpakete herunterladen")
                    .setItems(choice_download,  new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // The 'which' argument contains the index position
                            // of the selected item
                        }
                    });
            builder.create().show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
