package de.htwdd.vokabeltrainer;

import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

import de.htwdd.vokabeltrainer.helper.DBHelper;
import de.htwdd.vokabeltrainer.helper.LanguageHelper;

public class EditWordGroup extends AppCompatActivity {
    long setid;
    int wordgroupid = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_word_group);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                EditWordGroup.this.btnSaveOnClick(v);
            }
        });

        Bundle extras = getIntent().getExtras();
        setid = extras.getLong("setid");

        DBHelper db = new DBHelper(this);
        DBHelper.VocabSet vs = db.getVocabSet(setid);

        LanguageHelper lh = LanguageHelper.getInstance();

        TextView tvLang1 = (TextView) findViewById(R.id.txtvwLang1);
        tvLang1.setText(lh.getLanguageNameByCode(vs.lang1) + ":");

        TextView tvLang2 = (TextView) findViewById(R.id.txtvwLang2);
        tvLang2.setText(lh.getLanguageNameByCode(vs.lang2) + ":");

        if ((wordgroupid = extras.getInt("wordgroupid")) > 0) {
            Log.d("DEBUG", "Word-Group-ID: " + Integer.toString(wordgroupid));
            ArrayList<Cursor> vw = db.getVocabWords((int)setid, wordgroupid);

            // Wörter der Sprache 1 aufbereiten.
            Cursor cur1 = vw.get(0);
            StringBuilder words1 = new StringBuilder();
            boolean isFirst = true;
            TextView tvWords1 = (TextView) findViewById(R.id.editWords1);

            while (!cur1.isAfterLast()) {
                if (isFirst) isFirst = false;
                else words1.append("\n");

                words1.append(cur1.getString(1));
                cur1.moveToNext();
            }

            cur1.close();
            tvWords1.setText(words1.toString());

            // Wörter der Sprache 2 aufbereiten.
            Cursor cur2 = vw.get(1);
            StringBuilder words2 = new StringBuilder();
            isFirst = true;
            TextView tvWords2 = (TextView) findViewById(R.id.editWords2);

            while (!cur2.isAfterLast()) {
                if (isFirst) isFirst = false;
                else words2.append("\n");

                words2.append(cur2.getString(1));
                cur2.moveToNext();
            }

            cur2.close();
            tvWords2.setText(words2.toString());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                /*Intent intent = NavUtils.getParentActivityIntent(this);
                intent.putExtra("id", setid);
                startActivity(intent);*/
                backToParentActivity();

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void btnSaveOnClick(View v) {
        TextView tvWords1 = (TextView) findViewById(R.id.editWords1);
        TextView tvWords2 = (TextView) findViewById(R.id.editWords2);

        String[] words1 = tvWords1.getText().toString().split("\n");
        String[] words2 = tvWords2.getText().toString().split("\n");

        // TODO: Prüfen, dass genügend Wörter in beide Felder eingegeben wurden.

        DBHelper db = new DBHelper(this);

        if (wordgroupid == 0) { // Neue Wort-Gruppe anlegen.
            db.insertWordGroup(setid, words1, words2);
        } else { // Vorhandene Wort-Gruppe überschreiben.
            db.updateWordGroup(setid, wordgroupid, words1, words2);
        }

        backToParentActivity();
        Toast.makeText(this, "Die Wortgruppe wurde erfolgreich gespeichert.", Toast.LENGTH_LONG).show();
    }

    private void backToParentActivity() {
        Intent intent = NavUtils.getParentActivityIntent(this);
        intent.putExtra("id", setid);
        startActivity(intent);
    }
}
