package de.htwdd.vokabeltrainer;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import de.htwdd.vokabeltrainer.helper.DBHelper;
import de.htwdd.vokabeltrainer.helper.LanguageHelper;

public class ManageVocabWordGroups extends AppCompatActivity {
    private DBHelper.VocabSet vs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_vocab_word_groups);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        long id = getIntent().getExtras().getLong("id");
        DBHelper db = new DBHelper(this);
        LanguageHelper lh = LanguageHelper.getInstance();


        vs = db.getVocabSet(id);

        TextView tvSetDescription = (TextView) findViewById(R.id.txtvSetDescription);
        tvSetDescription.setText(Html.fromHtml(vs.description + " (<font color=\"red\">" + lh.getLanguageNameByCode(vs.lang1) + "</font> - <font color=\"blue\">" + lh.getLanguageNameByCode(vs.lang2) + "</font>)"));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = NavUtils.getParentActivityIntent(this);
                intent.putExtra("id", vs.id);
                startActivity(intent);

                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
