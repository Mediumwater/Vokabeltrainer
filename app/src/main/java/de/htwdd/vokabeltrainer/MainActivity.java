package de.htwdd.vokabeltrainer;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import java.util.ArrayList;

import de.htwdd.vokabeltrainer.helper.DBHelper;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /**
         * TODO: zuletzt ausgeführte Trainingsmethode mit samt zugehörigemVokabelset und Sprachrichtung laden (aus shared Preferences)
         */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent startIntent;
        Fragment fragment =null;

        if (id == R.id.nav_vocab) {
            startIntent = new Intent(getApplicationContext(), VokverActivity.class);
            startActivity(startIntent);
            return true;

        } else if (id == R.id.nav_stats) {
            startIntent = new Intent(getApplicationContext(), StatistikActivity.class);
            startActivity(startIntent);
            return true;

        } else if (id == R.id.nav_changeset) {
            //=====================================================================================================
            //Auswahl des zu trainierenden Vokabelsets treffen
            //=====================================================================================================
            /**
             * TODO: Vokabelsets aus DB auslesen und generisch zur Auswahl zur Verfügung stellen, sowie merken, welches Set zuletzt ausgewählt war (shared preference)
             */
            final SharedPreferences.Editor prefs =  getSharedPreferences("de.htwdd.vokabeltrainer", MODE_PRIVATE).edit();

            DBHelper db = new DBHelper(this);
            final ArrayList<DBHelper.VocabSet> al = db.getAllVocabSetsForMain();
            final String choice_set[] = new String[al.size()];
            int i=0;
            for (DBHelper.VocabSet vs : al){
                choice_set[i]= vs.description;
            }
            
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Vokabelset auswählen")
                    .setItems(choice_set, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            prefs.putString("Set_Name" ,al.get(which).description);
                            prefs.putLong("Set_ID" , al.get(which).id );
                            prefs.commit();
                        }
                    });
            builder.create().show();

            fragment = new FreitextFragment();
            FragmentManager fragmentManager = getFragmentManager();

            fragmentManager.beginTransaction().replace(R.id.mainframe, fragment).commit();

        } else if (id == R.id.nav_changelang) {
            //=====================================================================================================
            //Auswahl der zu tainierenden Sprachrichtung treffen
            //=====================================================================================================
            /**
             * TODO: verfügbare Sprachrichtungen aus DB auslesen und generisch zur Auswahl zur Verfügung stellen, sowie merken, welche Sprachrichtung zuletzt ausgewählt war (shared preference)
             */
            String choice_lang[]= {"Deutsch - Englisch", "Engisch - Deutsch", "Deutsch - Spanisch", "Spanisch - Deutsch"};
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Sprachrichtung auswählen")
                    .setItems(choice_lang, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // The 'which' argument contains the index position
                            // of the selected item
                        }
                    });
            //SharedPreferences.Editor prefss =  getSharedPreferences("de.htwdd.vokabeltrainer", MODE_PRIVATE).edit();

            builder.create().show();

        }else if (id == R.id.nav_mc) {
            //=====================================================================================================
            //Auf Trainingsmodus Multiple Choice umschalten, indem Fragment geladen wird
            //=====================================================================================================
            fragment = new MultiChoiceFragment();
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.mainframe, fragment).commit();

        } else if (id == R.id.nav_freetext) {
            //=====================================================================================================
            //Auf Trainingsmodus Freitext umschalten, indem Fragment geladen wird
            //=====================================================================================================
            /**
             * TODO: Modus im entsprechenden Fragment implementieren und zuletzt verwendeten Modus merken (shared preference)
             */

            fragment = new FreitextFragment();
            FragmentManager fragmentManager = getFragmentManager();

            fragmentManager.beginTransaction().replace(R.id.mainframe, fragment).commit();

            // startIntent = new Intent(getApplicationContext(), FreitextActivity.class);
            //startActivity(startIntent);
            //return true;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



}
