package de.htwdd.vokabeltrainer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import de.htwdd.vokabeltrainer.Adapters.VocabSetsCursorAdapter;
import de.htwdd.vokabeltrainer.helper.DBHelper;
import de.htwdd.vokabeltrainer.helper.LanguageHelper;
import de.htwdd.vokabeltrainer.helper.VocabDownloadHelper;

public class VokverActivity extends AppCompatActivity {
    private ArrayList<VocabDownloadHelper.DownloadableVocSet> dvs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vokver);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Vokabeln verwalten");
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startIntent;
                startIntent = new Intent(getApplicationContext(), ManageVokabelset.class);
                startActivity(startIntent);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        populateVocabSetsList();

        /*DBHelper db = new DBHelper(this);

        ArrayList<Cursor> vocs = db.getRandomVocabWords(1);

        for (int i = 0; i < vocs.size(); i++) {
            Log.d("DEBUG", "* Woerter Sprache " + Integer.toString(i));

            while (vocs.get(i).isAfterLast() == false) {
                Log.d("DEBUG", vocs.get(i).getString(1));
                vocs.get(i).moveToNext();
            }
        }*/
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
            new AsyncDownloadableVocabSetsHelper().execute();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*
     * OnClick-Listener fuer die Auswahl des Vobakel-Sets, welches heruntergeladen und installiert werden soll.
     */
    final DialogInterface.OnClickListener downloadableVocsetsOnClickListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialoge, int which) {
            //Log.d("DEBUG", Integer.toString(which));
            Log.d("DEBUG", dvs.get(which).downloadurl);
            new AsyncDownloadAndInstallVocabSetHelper().execute(dvs.get(which).downloadurl);
        }
    };

    public void populateVocabSetsList() {
        DBHelper db = new DBHelper(this);
        LanguageHelper lh = LanguageHelper.getInstance();

        ListView lv = (ListView) findViewById(R.id.listView);
        CursorAdapter adapter = new VocabSetsCursorAdapter(this, db.getAllVocabSets(), 0);

        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent startIntent;
                startIntent = new Intent(getApplicationContext(), ManageVokabelset.class);
                startIntent.putExtra("id", id);
                startActivity(startIntent);
            }
        });

        lv.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override public void onCreateContextMenu(ContextMenu menu, final View v, final ContextMenu.ContextMenuInfo menuInfo) {
                final String caption = ((TextView)((AdapterView.AdapterContextMenuInfo)menuInfo).targetView).getText().toString();

                menu.setHeaderTitle(caption);
                menu.add("Löschen")
                        .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override public boolean onMenuItemClick(MenuItem item) {
                                AlertDialog dialog = new AlertDialog.Builder(VokverActivity.this).create();
                                dialog.setTitle("Vokabel-Set löschen");
                                dialog.setMessage("Soll das Vokabel-Set " + caption +"  wirklich gelöscht werden?");

                                dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Nein", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) { dialog.cancel(); }
                                });

                                dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Unbedingt", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        DBHelper db = new DBHelper(VokverActivity.this);
                                        db.deleteVocabSet(((AdapterView.AdapterContextMenuInfo)menuInfo).id);

                                        VokverActivity.this.populateVocabSetsList();

                                        String toastText = "Vokabel-Set  " + caption + " wurde gelöscht.";
                                        Toast.makeText(VokverActivity.this, toastText, Toast.LENGTH_SHORT).show();

                                        dialog.cancel();
                                    }
                                });

                                dialog.show();

                                return true;
                            }
                        });
            }
        });
    }

    /*
     * Innere Klasse, da Download von Dateien asyncron erfolgen muss. Hier Download und anzeigen der zur Download
     * verfuegbaren Vokabel-Sets.
     */
    private class AsyncDownloadableVocabSetsHelper extends AsyncTask<Void, Void, ArrayList<VocabDownloadHelper.DownloadableVocSet>> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(VokverActivity.this);
            progressDialog.setMessage("Bitte warten...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected ArrayList<VocabDownloadHelper.DownloadableVocSet> doInBackground(Void... params) {
            try {
                return VocabDownloadHelper.getDownloadableVocabSets();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<VocabDownloadHelper.DownloadableVocSet> dvs) {
            if (dvs == null) {
                progressDialog.dismiss();

                AlertDialog dialog = new AlertDialog.Builder(VokverActivity.this).create();
                dialog.setTitle("Fehler");
                dialog.setMessage("Beim Abrufen der verfügbaren Vokabel-Sets ist ein Fehler aufgetreten.");
                dialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", (DialogInterface.OnClickListener)null);
                dialog.show();
            } else {
                VokverActivity.this.dvs = dvs;
                String choice_download[] = new String[dvs.size()];
                LanguageHelper lh = LanguageHelper.getInstance();

                for (int i = 0; i < dvs.size(); i++)
                    choice_download[i] = dvs.get(i).description + " (" + lh.getLanguageNameByCode(dvs.get(i).lang1) + " - " + lh.getLanguageNameByCode(dvs.get(i).lang2) + ")";

                progressDialog.dismiss();

                AlertDialog.Builder builder = new AlertDialog.Builder(VokverActivity.this);
                builder.setTitle("Sprachpakete herunterladen").setItems(choice_download, VokverActivity.this.downloadableVocsetsOnClickListener);
                builder.create().show();
            }
        }
    }

    /*
     * Asynchrones downloaden und installieren eines Vokabel-Sets.
     */
    private class AsyncDownloadAndInstallVocabSetHelper extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... url) {
            VocabDownloadHelper vdh = new VocabDownloadHelper(VokverActivity.this);
            vdh.downloadAndInstallVocabSet(url[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            VokverActivity.this.populateVocabSetsList();
        }
    }
}
