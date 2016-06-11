package de.htwdd.vokabeltrainer.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.DebugUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by alex on 5/31/16.
 * Hier befindet sich der Code zum lesen und manipulieren der Datenbank.
 */
public class DBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "VokabelTrainerDatabase.db";

    /*
     * Haelt Informationen ueber Vokabel-Sets.
     */
    public static class VocabSets {
        public final String description;
        public final String lang1;
        public final String lang2;
        public final int hits;
        public final int misses;
        public final double ratio;

        private VocabSets(String description, String lang1, String lang2) {
            this.description = description;
            this.lang1 = lang1;
            this.lang2 = lang2;
            this.hits = 0;
            this.misses = 0;
            this.ratio = 0;
        }

        private VocabSets(String description, String lang1, String lang2, int hits, int misses) {
            this.description = description;
            this.lang1 = lang1;
            this.lang2 = lang2;
            this.hits = hits;
            this.misses = misses;
            this.ratio = (double) hits / (double) (hits + misses);
        }
    }

    public DBHelper(Context context) {
        super(context, DATABASE_NAME , null, 1);
        //context.deleteDatabase(DATABASE_NAME); // Datenbank immer wieder loeschen, damit onCreate-Methode aufgerufen wird.
    }

    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE VocabSets (SetID INTEGER PRIMARY KEY, Lang1 CHARACTER NOT NULL, Lang2 CHARACTER NOT NULL, Description VARCHAR NOT NULL)");
        db.execSQL("CREATE TABLE VocabWords (WordID INTEGER PRIMARY KEY, Lang CHARACTER NOT NULL, Word VARCHAR NOT NULL)");
        db.execSQL("CREATE TABLE VocabReleation (SetID INTEGER, GroupID INTEGER, WordA INTEGER, WordB INTEGER, Misses INTEGER, Hits INTEGER, PRIMARY KEY (SetID, WordA, WordB))");
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS VocabSets");
        db.execSQL("DROP TABLE IF EXISTS VocabWords");
        db.execSQL("DROP TABLE IF EXISTS VocabReleation");
        onCreate(db);
    }

    /*
     * Gibt einen Cursor auf alle vorhandenen Vokabel-Sets zurueck.
     */
    public Cursor getAllVocabSets()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT SetID AS _id, Description, Lang1, Lang2 FROM VocabSets", null);
        cur.moveToFirst();

        return cur;
    }

    /*
     * Gibt eine Datenstruktur aller vorhadenen Vokabel-Sets mit Statistiken über hits, misses und ratio zurueck.
     */
    public ArrayList<VocabSets> getAllVocabSetsWithRatio()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT s.Description, s.Lang1, s.Lang2, SUM(r.Hits) AS hits, SUM(r.Misses) AS misses " +
                "FROM VocabSets s " +
                "INNER JOIN VocabReleation r ON r.SetID = s.SetID " +
                "GROUP BY s.SetID", null);
        cur.moveToFirst();

        ArrayList<VocabSets> al = new ArrayList<>();

        //al.add("Testeintrag");

        while (cur.isAfterLast() == false) {
            al.add(new VocabSets(cur.getString(0), cur.getString(1), cur.getString(2), cur.getInt(3), cur.getInt(4)));
            cur.moveToNext();
        }

        return al;
    }

    /*
     * Gibt die Namen aller Woerter als Array zurueck. Momentan nur fuer Debuging-Zwecke.
     */
    public ArrayList<String> getAllWords()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM VocabWords", null);
        cur.moveToFirst();

        ArrayList<String> al = new ArrayList<>();

        while (cur.isAfterLast() == false) {
            al.add(cur.getString(cur.getColumnIndex("Word")));
            cur.moveToNext();
        }

        return al;
    }

    /*
     * Gibt die Anzahl der Vokabelgruppen (zusammengehoerende Woerter) in einem Set zurueck.
     */
    public int getVocabGroupCount(int setid) {
        SQLiteDatabase db = this.getReadableDatabase();
        return (int) DatabaseUtils.queryNumEntries(db, "(SELECT COUNT(GroupID) FROM VocabReleation GROUP BY GroupID)");
    }

    /*
     * Gibt eine ArrayListe, die zwei Cursor enthaelt zurueck. Der erste Cursor liefert die Woerter und IDs der SpracheA.
     * Der zweite Cursor die der SpracheB.
     *
     * Params:
     * @setid: ID eines Vokabel-Sets
     * @groupid: ID einer Woertergruppe innerhalb des Vokabel-Sets.
     *
     * Beispiel (Vokabel-Set ID sei 1, Wortgruppen ID ebenso.):
     * --------------------------------
     * ArrayList<Cursor> vocs = db.getVocabWords(1, 1);
     *
     *  for (int i = 0; i < vocs.size(); i++) {
     *      Log.d("DEBUG", "* Woerter Sprache " + Integer.toString(i));
     *
     *      while (vocs.get(i).isAfterLast() == false) {
     *          Log.d("DEBUG", vocs.get(i).getString(1));
     *          vocs.get(i).moveToNext();
     *      }
     *  }
     *
     *  Ausgabe:
     *  --------
     * * Woerter Sprache 0
     * to be allowed to
     * to have the right to
     * to may
     * may
     * * Woerter Sprache 1
     * dürfen
     */
    public ArrayList<Cursor> getVocabWords(int setid, int groupid) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Cursor> al = new ArrayList<>();

        // Woerter Sprache A abrufen.
        Cursor cur1 = db.rawQuery("SELECT WordID, Word FROM VocabWords WHERE WordID IN(SELECT WordA FROM VocabReleation WHERE SetID=" + Integer.toString(setid) + " AND GroupID=" + Integer.toString(groupid) + ")", null);
        cur1.moveToFirst();
        al.add(cur1);

        // Woerter Sprache B abrufen.
        Cursor cur2 = db.rawQuery("SELECT WordID, Word FROM VocabWords WHERE WordID IN(SELECT WordB FROM VocabReleation WHERE SetID=" + Integer.toString(setid) + " AND GroupID=" + Integer.toString(groupid) + ")", null);
        cur2.moveToFirst();
        al.add(cur2);

        return al;
    }

    /*
     * Wie getVocabWords(), aber liefert eine zufaellige Wortgruppe zurueck.
     *
     * Params:
     * @setid: ID eines Vokabel-Sets
     *
     * Beispiel (Vokabel-Set ID sei 1):
     * --------------------------------
     * ArrayList<Cursor> vocs = db.getRandomVocabWords(1);
     * // Rest wie beim Code-Beispiel zu getVocabWords()
     */
    public ArrayList<Cursor> getRandomVocabWords(int setid) {
        int cnt = this.getVocabGroupCount(setid);
        if (cnt <= 0) return new ArrayList<Cursor>();

        Random random = new Random();

        return this.getVocabWords(setid, random.nextInt(cnt) + 1);
    }

    /*
     * Erzeugt ein Vokabel-Set anhand eines JSON-Strings. Gibt true zurueck bei Erfolg, sonst false.
     */
    public boolean createVocabSetFromJSONString(String json) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            JSONObject jsonObject = (new JSONObject(json));

            /*Log.d("DEBUG", "Die Beschreibung lautet: " + jsonObject.getString("description"));
            Log.d("DEBUG", "Die erste Sprache lautet: " + jsonObject.getString("lang1"));
            Log.d("DEBUG", "Die zweite Sprache lautet: " + jsonObject.getString("lang2"));

            //Log.d("DEBUG", "Wordgroups: " + jsonObject.getJSONArray("WordsA").length());
            Log.d("DEBUG", jsonObject.getJSONArray("WordsA").getString(0) + " - " + jsonObject.getJSONArray("WordsB").getString(0));*/

            String lang1 = jsonObject.getString("lang1");
            String lang2 = jsonObject.getString("lang2");

            db.beginTransaction();

            ContentValues contentValues = new ContentValues();
            contentValues.put("Description", jsonObject.getString("description"));
            contentValues.put("Lang1", lang1);
            contentValues.put("Lang2", lang2);

            Long newSetId = db.insert("VocabSets", null, contentValues);
            Log.d("DEBUG", "The new entrys ID is: " + newSetId);

            JSONArray jsonWordsA =  jsonObject.getJSONArray("WordsA");
            JSONArray jsonWordsB =  jsonObject.getJSONArray("WordsB");

            Random random = new Random();

            /* Woerter und Wort-Relationen in DB uebernehmen. */
            for(int i = 0; i < jsonWordsA.length(); i++){
                ArrayList<Long> newWordsAID = new ArrayList();
                ArrayList<Long> newWordsBID = new ArrayList();

                /* Woerter der Sprache 1 uebernehmen. */
                for (String word : jsonWordsA.getString(i).split(";")) {
                    ContentValues cvWord = new ContentValues();

                    cvWord.put("Lang", lang1);
                    cvWord.put("Word", word.trim());

                    newWordsAID.add(db.insert("VocabWords", null, cvWord));
                    //Log.d("DEBUG", "WordA: " + word);
                }

                /* Woerter der Sprache 2 uebernehmen */
                for (String word : jsonWordsB.getString(i).split(";")) {
                    ContentValues cvWord = new ContentValues();

                    cvWord.put("Lang", lang2);
                    cvWord.put("Word", word.trim());

                    newWordsBID.add(db.insert("VocabWords", null, cvWord));
                    //Log.d("DEBUG", "WordB: " + word);
                }

                /* Relation zwischen den Woertern in Datenbank aufnehmen. */
                for (Long aID : newWordsAID) {
                    for (Long bID : newWordsBID) {
                        ContentValues cvWordRelation = new ContentValues();

                        cvWordRelation.put("SetID", newSetId);
                        cvWordRelation.put("GroupID", i + 1);
                        cvWordRelation.put("WordA", aID);
                        cvWordRelation.put("WordB", bID);

                        // TODO: Test-Code, spaeter entfernen!
                        cvWordRelation.put("Misses", random.nextInt(101));
                        cvWordRelation.put("Hits", random.nextInt(101));

                        db.insert("VocabReleation", null, cvWordRelation);
                    }
                }
            }

            db.setTransactionSuccessful();

            Log.d("DEBUG", "createVocabSetFromJSONString(): Fertig!");
        } catch (Exception e) {
            // Nothing....
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }

        return true;
    }

    /*
     * Loescht das Vokabel-Set mit der gegebenen id.
     */
    public void deleteVocabSet(Long id) {
        SQLiteDatabase db = this.getWritableDatabase();

        //db.beginTransaction();
        db.execSQL("DELETE FROM VocabWords WHERE WordID IN(SELECT WordA FROM VocabReleation WHERE SetID=" + Long.toString(id) + ") OR WordID IN(SELECT WordB FROM VocabReleation WHERE SetID=" + Long.toString(id) + ")");
        db.delete("VocabReleation", "SetID=?", new String[] {Long.toString(id)});
        db.delete("VocabSets", "SetID=?", new String[] {Long.toString(id)});
        //db.endTransaction();
    }
}
