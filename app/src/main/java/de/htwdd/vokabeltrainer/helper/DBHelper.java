package de.htwdd.vokabeltrainer.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Debug;
import android.util.DebugUtils;
import android.util.Log;

import com.github.mikephil.charting.data.BarEntry;

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
    public static class VocabSet {
        public long id = 0;
        public String description = "";
        public String lang1 = "";
        public String lang2 = "";
        public int hits = 0;
        public int misses = 0;
        public double ratio = 0;
        public int countVocabGroups = 0;
        public int countVocabWords = 0;

        public VocabSet() {}

        private VocabSet(String description, String lang1, String lang2) {
            this.id = 0;
            this.description = description;
            this.lang1 = lang1;
            this.lang2 = lang2;
            this.hits = 0;
            this.misses = 0;
            this.ratio = 0;
        }

        private VocabSet(long id, String description, String lang1, String lang2) {
            this.id = id;
            this.description = description;
            this.lang1 = lang1;
            this.lang2 = lang2;
            this.hits = 0;
            this.misses = 0;
            this.ratio = 0;
        }

        private VocabSet(String description, String lang1, String lang2, int hits, int misses) {
            this.id = 0;
            this.description = description;
            this.lang1 = lang1;
            this.lang2 = lang2;
            this.hits = hits;
            this.misses = misses;
            this.ratio = (double) hits / (double) (hits + misses);
        }
    }

    //Ist der Rückgabewert zur Anfrage nach Vokabeln
    public static class VocabWord {
        public long id = 0;
        public long id1 = 0;
        public String word = "";
        public String word1 = "";

        private VocabWord(long id, String word) {
            this.id = id;
            this.word = word;
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
     * Gibt die Grunddaten des Vokabel-Sets mit der gegebenen ID zurück.
     */
    public VocabSet getVocabSet(long id)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cur = db.rawQuery("SELECT Description, Lang1, Lang2 FROM VocabSets WHERE SetID=" + Long.toString(id), null);
        cur.moveToFirst();

        VocabSet vs = new VocabSet(id, cur.getString(0), cur.getString(1), cur.getString(2));

        cur = db.rawQuery("SELECT Max(GroupID) FROM VocabReleation WHERE SetID=" + Long.toString(id), null);
        cur.moveToFirst();
        if (!cur.isAfterLast()) vs.countVocabGroups = cur.getInt(0);

        cur = db.rawQuery("SELECT Count(*) FROM VocabWords WHERE WordID IN(SELECT WordA FROM VocabReleation WHERE SetID=" + Long.toString(id) + ") OR WordID IN(SELECT WordB FROM VocabReleation WHERE SetID=" + Long.toString(id) + ")", null);
        cur.moveToFirst();
        if (!cur.isAfterLast()) vs.countVocabWords = cur.getInt(0);

        return vs;
    }

    /*
     * Gibt eine Datenstruktur aller vorhadenen Vokabel-Sets mit Statistiken über hits, misses und ratio zurueck.
     */
    public ArrayList<VocabSet> getAllVocabSetsWithRatio()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT s.Description, s.Lang1, s.Lang2, SUM(r.Hits) AS hits, SUM(r.Misses) AS misses " +
                "FROM VocabSets s " +
                "INNER JOIN VocabReleation r ON r.SetID = s.SetID " +
                "GROUP BY s.SetID", null);
        cur.moveToFirst();

        ArrayList<VocabSet> al = new ArrayList<>();

        //al.add("Testeintrag");

        while (cur.isAfterLast() == false) {
            al.add(new VocabSet(cur.getString(0), cur.getString(1), cur.getString(2), cur.getInt(3), cur.getInt(4)));
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
        return (int) DatabaseUtils.queryNumEntries(db, "(SELECT COUNT(GroupID) FROM VocabReleation WHERE SetID = " + Integer.toString(setid) + " GROUP BY GroupID)");
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
        return this.getVocabWords(setid, random.nextInt(cnt));
    }

    /*
     * ArrayList<VocabSet> getRandomVocabWords(), liefert eine zufällige
     * Wortgruppe aus einem zufälligen Set zurück.
     *
     */
    public ArrayList<VocabWord> getRandomVocabWord() {
        Cursor cursor = this.getAllVocabSets();
        int setcount = cursor.getColumnCount(); //Anzahl der Sets
        ArrayList<VocabWord> al = new ArrayList<>();
        if (setcount <= 0) return al; //return if there is no set

        Log.d("DEBUGaaa", String.valueOf(setcount));

        Random r = new Random();
        int nr = (r.nextInt(setcount)); // Auswahl zufälliger Set id
        ArrayList<Cursor> c = getRandomVocabWords(1); //reset this to get real random, but not all set match the
        if (c.isEmpty()) {Log.d("DEBUG", "getRandomVocabWords() is empty in DBHelper"); return al;}
        for (Cursor cur : c) {
           al.add(new VocabWord(cur.getInt(0) ,cur.getString(1)));
            cur.moveToNext();
        }
        return al;
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

    /*
     * Legt ein neues leeres Vokabel-Set an.
     *
     * Params:
     * @vs: Object vom Typ VocabSet, welches den Beschreibungstext und die IDs der beiden Sprachen
     * enthalten muss
     *
     * Return:
     * ID des neu generierten Vokabel-Sets oder 0 bei einem Fehlschlag.
     */
    public long createVocabSet(VocabSet vs) {
        if (vs.lang1.equals(vs.lang2)) return 0; // Beide Sprachen dieselben sind nicht erlaubt.

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("Description", vs.description);
        cv.put("Lang1", vs.lang1);
        cv.put("Lang2", vs.lang2);

        return db.insert("VocabSets", null, cv);
    }

    /*
     * Modifiziert die Basisdaten eines Vokabel-Sets.
     *
     * Params:
     * @vs: Object vom Typ VocabSet, welches die ID des zu modifizierenden Vokabel-Sets, den
     * Beschreibungstext und die IDs der beiden Sprachen enthalten muss.
     *
     * Return:
     * true, wenn es funktioniert hat, sonst false.
     */
    public boolean updateVocabSet(VocabSet vs) {
        if (vs.lang1.equals(vs.lang2)) return false; // Beide Sprachen dieselben sind nicht erlaubt.

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cur = db.rawQuery("SELECT Lang1 FROM VocabSets WHERE SetID=" + Long.toString(vs.id), null);

        cur.moveToFirst();

        if (cur.isAfterLast()) return false;

        String old_lang1 = cur.getString(0);

        db.execSQL("UPDATE VocabSets SET Description='" + vs.description + "', Lang1='" + vs.lang1 + "', Lang2='" + vs.lang2 + "' WHERE SetID=" + Long.toString(vs.id));
        Log.d("DEBUG", "UPDATE VocabSets SET Description='" + vs.description + "', Lang1='" + vs.lang1 + "', Lang2='" + vs.lang2 + "' WHERE SetID=" + Long.toString(vs.id));

        // Wort-Datenbank aktualisieren.
        db.execSQL("UPDATE VocabWords SET Lang = " +
                "(CASE WHEN Lang = '" + old_lang1 + "' THEN '" + vs.lang2 + "' ELSE '" + vs.lang1 + "' END) WHERE " +
                "WordID IN(SELECT WordA FROM VocabReleation WHERE SetID=" + Long.toString(vs.id) + ") OR " +
                "WordID IN(SELECT WordB FROM VocabReleation WHERE SetID=" + Long.toString(vs.id) + ")");

        return true;
    }
}
