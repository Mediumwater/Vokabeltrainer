package de.htwdd.vokabeltrainer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.ActionMenuItemView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import de.htwdd.vokabeltrainer.helper.DBHelper;
import de.htwdd.vokabeltrainer.helper.LanguageHelper;

/**
 * Created by alex on 6/13/16.
 */
public class ManageVokabelset extends AppCompatActivity {
    //private long id = 0;
    private DBHelper.VocabSet vs;
    private Menu _menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_vokabelset);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button btnSave = (Button) findViewById(R.id.buttonSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                ManageVokabelset.this.btnSaveOnClick(v);
            }
        });

        Button btnEditVocab = (Button) findViewById(R.id.buttonEditWords);
        btnEditVocab.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                ManageVokabelset.this.btnEditVocab(v);
            }
        });

        Spinner spinnerLang1 = (Spinner) findViewById(R.id.spinner);
        Spinner spinnerLang2 = (Spinner) findViewById(R.id.spinner2);
        LanguageHelper lh = LanguageHelper.getInstance();
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, lh.getLanguageNames());
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinnerLang1.setAdapter(adapter);
        spinnerLang2.setAdapter(adapter);

        Bundle extras = getIntent().getExtras();
        long id;

        /* Ein vorhandenes Vokabelset soll bearbeitet werden. */
        if (extras != null && (id = extras.getLong("id")) > 0) {
            DBHelper db = new DBHelper(this);
            vs = db.getVocabSet(id);

        /* Ein neues Vokabelset soll angelegt werden. */
        } else {
            vs = new DBHelper.VocabSet();
        }

        setEtidable(vs.id == 0);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        MenuItem mnuItem;

        if (vs.id == 0) mnuItem = menu.findItem(R.id.action_edit_vocabset);
        else mnuItem = menu.findItem(R.id.action_cancle_edit_vocabset);

        mnuItem.setVisible(false);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.managevocabset, menu);
        this._menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit_vocabset:
                setEtidable(true);
                return true;
            case R.id.action_cancle_edit_vocabset:
                if (vs.id == 0) {
                    NavUtils.navigateUpFromSameTask(this);
                } else {
                    setEtidable(false);
                }

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setEtidable(boolean editable) {
        final TextView txtDescription = (TextView) findViewById(R.id.editTextDescription);
        final Spinner spinnerLang1 = (Spinner) findViewById(R.id.spinner);
        final Spinner spinnerLang2 = (Spinner) findViewById(R.id.spinner2);
        final Button btnSave = (Button) findViewById(R.id.buttonSave);
        final LinearLayout elemsNotToEditate = (LinearLayout) findViewById(R.id.elementsNotToEditate);

        txtDescription.setEnabled(editable);
        spinnerLang1.setEnabled(editable);
        spinnerLang2.setEnabled(editable);
        elemsNotToEditate.setVisibility(((editable) ? View.GONE : View.VISIBLE));
        btnSave.setVisibility(((editable) ? View.VISIBLE : View.GONE));

        if (_menu != null) {
            _menu.findItem(R.id.action_edit_vocabset).setVisible(!editable);
            _menu.findItem(R.id.action_cancle_edit_vocabset).setVisible(editable);
        }

        /* Werte wieder zuruecksetzen, wenn Editiervorgang abgebrochen wurde. */
        if (!editable) {
            LanguageHelper lh = LanguageHelper.getInstance();

            txtDescription.setText(vs.description);

            spinnerLang1.setSelection(lh.langCodes.get(vs.lang1));
            spinnerLang2.setSelection(lh.langCodes.get(vs.lang2));

            TextView tvVocabGroupCount = (TextView) findViewById(R.id.textViewCountVocabGroups);
            tvVocabGroupCount.setText(Integer.toString(vs.countVocabGroups));

            TextView tvVocabWordCount = (TextView) findViewById(R.id.textViewCountVocabWords);
            tvVocabWordCount.setText(Integer.toString(vs.countVocabWords));
        }
    }

    public void btnSaveOnClick(View v) {
        final TextView txtDescription = (TextView) findViewById(R.id.editTextDescription);
        final Spinner spinnerLang1 = (Spinner) findViewById(R.id.spinner);
        final Spinner spinnerLang2 = (Spinner) findViewById(R.id.spinner2);
        final LanguageHelper lh = LanguageHelper.getInstance();
        boolean success = false;
        String errmsg = "Das Vokabel-Set kann nicht gespeichert werden. Bitte überprüfe deine Eingaben.";

        vs.description = txtDescription.getText().toString();
        vs.lang1 = lh.getLanguageCodeByIndex(spinnerLang1.getSelectedItemPosition());
        vs.lang2 = lh.getLanguageCodeByIndex(spinnerLang2.getSelectedItemPosition());

        if (vs.lang1 == vs.lang2) {
            errmsg = "Du musst zwei unterschiedliche Sprachen wählen.";
        } else {

            if (vs.id == 0) { // Neues Vokabel-Set anlegen.
                Log.d("DEBUG", "Speichere neues Vokabel-Set.");
                DBHelper db = new DBHelper(this);
                long id = db.createVocabSet(vs);
                vs.id = id;
                if (id > 0) success = true;
            } else { // Bestehendes Vokabel-Set speichern.
                Log.d("DEBUG", "Speichere bestehendes Vokabel-Set.");
                DBHelper db = new DBHelper(this);
                success = db.updateVocabSet(vs);
            }
        }

        if (success) {
            this.setEtidable(false);
            Toast.makeText(ManageVokabelset.this, "Vokabel-Set erfolgreich gespeichert!", Toast.LENGTH_LONG).show();
        } else {
            AlertDialog dialog = new AlertDialog.Builder(this).create();
            dialog.setTitle("Fehlerhafte Eingabe");
            dialog.setMessage(errmsg);
            dialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", (DialogInterface.OnClickListener)null);
            dialog.show();
        }
    }

    public void btnEditVocab(View v) {
        Intent startIntent;
        startIntent = new Intent(getApplicationContext(), ManageVocabWordGroups.class);
        startIntent.putExtra("id", vs.id);
        startActivity(startIntent);
    }
}
