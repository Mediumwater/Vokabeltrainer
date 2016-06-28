package de.htwdd.vokabeltrainer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import de.htwdd.vokabeltrainer.Adapters.VocabWordGroupTextualArrayListAdapter;
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

        final long id = getIntent().getExtras().getLong("id");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startIntent;
                startIntent = new Intent(getApplicationContext(), EditWordGroup.class);
                startIntent.putExtra("setid", id);
                startActivity(startIntent);
            }
        });

        DBHelper db = new DBHelper(this);
        LanguageHelper lh = LanguageHelper.getInstance();

        vs = db.getVocabSet(id);

        TextView tvSetDescription = (TextView) findViewById(R.id.txtvSetDescription);
        tvSetDescription.setText(Html.fromHtml(vs.description + " (<font color=\"red\">" + lh.getLanguageNameByCode(vs.lang1) + "</font> - <font color=\"blue\">" + lh.getLanguageNameByCode(vs.lang2) + "</font>)"));

        // Listview mit Informationen füllen.
        ArrayList al = new ArrayList<String[]>();

        for (int i = 1; i <= db.getVocabGroupCount((int)id); i++) {
            ArrayList<Cursor> vw = db.getVocabWords((int)id, i);

            // Wörter der Sprache 1 aufbereiten.
            Cursor cur1 = vw.get(0);
            ArrayList words1 = new ArrayList<String>();

            while (!cur1.isAfterLast()) {
                words1.add(cur1.getString(1));
                cur1.moveToNext();
            }

            cur1.close();

            // Wörter der Sprache 2 aufbereiten.
            Cursor cur2 = vw.get(1);
            ArrayList words2 = new ArrayList<String>();

            while (!cur2.isAfterLast()) {
                words2.add(cur2.getString(1));
                cur2.moveToNext();
            }

            cur2.close();

            al.add(new String[]{TextUtils.join(", ", words1), TextUtils.join(", ", words2)});
        }

        VocabWordGroupTextualArrayListAdapter vwgtala = new VocabWordGroupTextualArrayListAdapter(this, R.layout.fragment_fragment_vocab_word_group_list_item, al);

        ListView lv = (ListView) findViewById(R.id.listView_word_groups);
        lv.setAdapter(vwgtala);
        lv.setOnCreateContextMenuListener(this.listViewContextMenuListener);

        // OnClick-Listener für die List-View.
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id2) {
                Intent startIntent;
                startIntent = new Intent(getApplicationContext(), EditWordGroup.class);
                startIntent.putExtra("setid", id);
                startIntent.putExtra("wordgroupid", (int)id2 + 1);
                startActivity(startIntent);
            }
        });
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

    View.OnCreateContextMenuListener listViewContextMenuListener = new View.OnCreateContextMenuListener() {
        @Override public void onCreateContextMenu(ContextMenu menu, final View v, final ContextMenu.ContextMenuInfo menuInfo) {
            //menu.setHeaderTitle(caption);
            menu.add("Löschen")
                    .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override public boolean onMenuItemClick(final MenuItem item) {
                            final VocabWordGroupTextualArrayListAdapter aa = (VocabWordGroupTextualArrayListAdapter)((ListView)v).getAdapter();
                            AlertDialog dialog = new AlertDialog.Builder(ManageVocabWordGroups.this).create();
                            dialog.setTitle("Wortgruppe löschen");
                            dialog.setMessage("Soll diese Wortgruppe wirklich gelöscht werden?");

                            dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Nein", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) { dialog.cancel(); }
                            });

                            dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Unbedingt", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    DBHelper db = new DBHelper(ManageVocabWordGroups.this);
                                    int pos = ((AdapterView.AdapterContextMenuInfo)menuInfo).position;
                                    db.deleteWordGroup(vs.id, pos + 1);
                                    aa.remove(aa.getItem(pos));

                                    Toast.makeText(ManageVocabWordGroups.this, "Die Wortgruppe wurde gelöscht.", Toast.LENGTH_SHORT).show();

                                    dialog.cancel();
                                }
                            });

                            dialog.show();

                            return true;
                        }
                    });
        }
    };
}
