package de.htwdd.vokabeltrainer.helper;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

        private VocabSet(int id, String description, String lang1, String lang2, int hits, int misses) {
            this.id = id;
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
        public long wordid = 0;
        public String word = "";
        public int setid;

        private VocabWord(long wordid, String word, int setid) {
            this.wordid = wordid;
            this.word = word;
            this.setid = setid;
        }

    }

    public DBHelper(Context context) {
        super(context, DATABASE_NAME , null, 1);
        //context.deleteDatabase(DATABASE_NAME); // Datenbank immer wieder loeschen, damit onCreate-Methode aufgerufen wird.
    }

    @Override public void onCreate(SQLiteDatabase db) {
        Log.d("DEBUG", "DBHelper.onCreate()");
        db.execSQL("CREATE TABLE VocabSets (SetID INTEGER PRIMARY KEY, Lang1 CHARACTER NOT NULL, Lang2 CHARACTER NOT NULL, Description VARCHAR NOT NULL)");
        db.execSQL("CREATE TABLE VocabWords (WordID INTEGER PRIMARY KEY, Lang CHARACTER NOT NULL, Word VARCHAR NOT NULL)");
        db.execSQL("CREATE TABLE VocabReleation (SetID INTEGER, GroupID INTEGER, WordA INTEGER, WordB INTEGER, Misses INTEGER DEFAULT 0, Hits INTEGER DEFAULT 0, PRIMARY KEY (SetID, WordA, WordB))");
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
        Cursor cur = db.rawQuery("SELECT SetID AS _id, Description, Lang1, Lang2 FROM VocabSets ORDER BY Description", null);
        cur.moveToFirst();
        return cur;
    }

    public  ArrayList<VocabSet> getAllVocabSetsForMain() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT SetID AS _id, Description, Lang1, Lang2 FROM VocabSets", null);
        cur.moveToFirst();
        ArrayList<VocabSet> al = new ArrayList<VocabSet>();
        do {
            al.add(new VocabSet(cur.getInt(0), cur.getString(1), cur.getString(2), cur.getString(3)));
        } while (cur.moveToNext());

        return al;
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
        while (cur.isAfterLast() == false) {
            al.add(new VocabSet(cur.getString(0), cur.getString(1), cur.getString(2), cur.getInt(3), cur.getInt(4)));
            cur.moveToNext();
        }
        return al;
    }

    public ArrayList<VocabSet> getAllVocabSetsWithRadioandID()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT s.Description, s.Lang1, s.Lang2, SUM(r.Hits) AS hits, SUM(r.Misses) AS misses " +
                "FROM VocabSets s " +
                "INNER JOIN VocabReleation r ON r.SetID = s.SetID " +
                "GROUP BY s.SetID", null);
        cur.moveToFirst();
        ArrayList<VocabSet> al = new ArrayList<>();
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
        if (cnt <= 0) {
            Log.d("DEBUG", "getVocabGroupCount(setid) is empty in  getRandomVocabWords(int setid) with id: " + setid);
            return new ArrayList<Cursor>();
        }
        Random random = new Random();
        return this.getVocabWords(setid, random.nextInt(cnt)+1);
    }

    public ArrayList<ArrayList<VocabWord>> getRandomVocabWord(int setid) {

        ArrayList<ArrayList<VocabWord>> al = new ArrayList<ArrayList<VocabWord>>();

        ArrayList<Cursor> c = this.getRandomVocabWords(setid);
        if (c.isEmpty()) {Log.d("DEBUG", "getRandomVocabWords() is empty in getRandomVocabWord(int setid)"); return al;}
        int i = 0;
        al.add(new ArrayList<VocabWord>());
        for (Cursor cur : c) {
            cur.moveToFirst();
            do {
                al.get(i).add(new VocabWord(cur.getInt(0), cur.getString(1), setid));
            } while (cur.moveToNext());
            al.add(new ArrayList<VocabWord>());
            i++;
        }
        return al;
    }


    public boolean updateMisses(int setID, Long wordA_ID, Long wordB_ID, boolean evaluation ) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (evaluation) {
            db.execSQL("UPDATE VocabReleation SET Misses = Misses + 1 WHERE WordA = " + wordA_ID + " and SetID = " + setID
                    + " and WordB = " + wordB_ID);
        } else {
            db.execSQL("UPDATE VocabReleation SET Misses = Misses + 1 WHERE WordA = " + wordB_ID + " and SetID = " + setID
                    + " and WordB = " + wordA_ID);
        }
        return true;
    }


    // "CREATE TABLE VocabReleation (SetID INTEGER, GroupID INTEGER, WordA INTEGER, WordB INTEGER, Misses INTEGER, Hits INTEGER, PRIMARY KEY (SetID, WordA, WordB))");
    public boolean updateMissesword(int setID, Long wordA_ID, boolean evaluation) {
        SQLiteDatabase db = this.getWritableDatabase();
       //Log.d("Hier", "Hier bei der DB bei updatemisseswordA");
/*
        Log.d("setID", "" + setID);
        Log.d("wordA_ID", "" + wordA_ID);

        SharedPreferences prefs = listener.getSharedPreferences(
                "de.htwdd.vokabeltrainer", listener.MODE_PRIVATE);
        Long setid = prefs.getLong("Set_ID", 0);
        Boolean evaluation = prefs.getBoolean("evaluation", true);
*/
        if (evaluation) {
            db.execSQL("UPDATE VocabReleation SET Misses = Misses + 1 WHERE WordA = " + Integer.toString(wordA_ID.intValue()) + " and SetID = " + Integer.toString(setID));
        } else {
            db.execSQL("UPDATE VocabReleation SET Misses = Misses + 1 WHERE WordB = " + Integer.toString(wordA_ID.intValue()) + " and SetID = " + Integer.toString(setID));
        }
/*
        db.execSQL("UPDATE VocabReleation SET Misses = Misses + 1 WHERE WordB = " + 918 + " and SetID = " + 2);



        SQLiteDatabase dbs = this.getReadableDatabase();
        Cursor cur1 = dbs.rawQuery("Select * from VocabReleation where WordB = " + 918 + " and SetID = " + 2, null);
        cur1.moveToFirst();
        Log.d("Hier: ", cur1.getString(0) + " " + cur1.getString(1) + " " + cur1.getString(2) + " " + cur1.getString(3) + " " + cur1.getString(4) + " " + cur1.getString(5) );
*/

        return true;
    }

    public boolean updateHits(int setID, Long wordA_ID, Long wordB_ID, boolean evaluations) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (evaluations){
            db.execSQL("UPDATE VocabReleation SET Hits = Hits + 1 WHERE WordA = " + wordA_ID + " and SetID = " + setID
                    + " and WordB = " + wordB_ID);
        } else {
            db.execSQL("UPDATE VocabReleation SET Hits = Hits + 1 WHERE WordA = " + wordB_ID + " and SetID = " + setID
                    + " and WordB = " + wordA_ID);
        }
        return true;
    }

    /*
     * ArrayList<VocabSet> getRandomVocabWords(), liefert eine zufällige
     * Wortgruppe aus einem zufälligen Set zurück.
     *
     */
    public ArrayList<ArrayList<VocabWord>> getRandomVocabWord() {
        Cursor cursor = this.getAllVocabSets();
        int setcount = cursor.getCount();
        ArrayList<ArrayList<VocabWord>> al = new ArrayList<ArrayList<VocabWord>>();
        if (setcount <= 0) {Log.d("DEBUG", "setcount = " + setcount); return al;}
        ArrayList<String> sl = new ArrayList<>();
        try {
            do {
                sl.add(cursor.getString(0));
            } while (cursor.moveToNext());
        } finally {
            cursor.close();
        }
        Random r = new Random();
        int nr = (r.nextInt(setcount));
        int setid = Integer.parseInt(sl.get(nr));
        ArrayList<Cursor> c = getRandomVocabWords(setid);
        if (c.isEmpty()) {Log.d("DEBUG", "getRandomVocabWords() is empty in DBHelper"); return al;}
        int i = 0;
        al.add(new ArrayList<VocabWord>());
        for (Cursor cur : c) {
            cur.moveToFirst();
            do {
                al.get(i).add(new VocabWord(cur.getInt(0), cur.getString(1), setid));
            } while (cur.moveToNext());
            al.add(new ArrayList<VocabWord>());
            i++;
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
                        //cvWordRelation.put("Misses", random.nextInt(101));
                        //cvWordRelation.put("Hits", random.nextInt(101));

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

        vs.description = vs.description.trim();
        if (vs.description.equals("")) return 0;

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

        vs.description = vs.description.trim();
        if (vs.description.equals("")) return false;

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cur = db.rawQuery("SELECT Lang1 FROM VocabSets WHERE SetID=" + Long.toString(vs.id), null);

        cur.moveToFirst();

        if (cur.isAfterLast()) return false;

        String old_lang1 = cur.getString(0);

        db.execSQL("UPDATE VocabSets SET Description='" + vs.description + "', Lang1='" + vs.lang1 + "', Lang2='" + vs.lang2 + "' WHERE SetID=" + Long.toString(vs.id));

        // Wort-Datenbank aktualisieren.
        db.execSQL("UPDATE VocabWords SET Lang = " +
                "(CASE WHEN Lang = '" + old_lang1 + "' THEN '" + vs.lang2 + "' ELSE '" + vs.lang1 + "' END) WHERE " +
                "WordID IN(SELECT WordA FROM VocabReleation WHERE SetID=" + Long.toString(vs.id) + ") OR " +
                "WordID IN(SELECT WordB FROM VocabReleation WHERE SetID=" + Long.toString(vs.id) + ")");

        return true;
    }

    /*
     * Legt eine neue Wortgruppe in einem Vorhandenen Vokabel-Set an.
     *
     * Params:
     * @setid: ID des Vokabel-Sets, in dem die neue Wort-Gruppe angelegt werden soll.
     * @words1: Array mit den Wörtern der ersten Sprache.
     * @words2: Array mit den Wörtern der zweiten Sprache.
     *
     * Return:
     * Die ID der neuen Wort-Gruppe oder 0 im Fehlerfall.
     */
    public int insertWordGroup(long setid, String[] words1, String[] words2) {
        SQLiteDatabase db = this.getWritableDatabase();

        int next_word_group_id = (int) DatabaseUtils.queryNumEntries(db, "(SELECT COUNT(GroupID) FROM VocabReleation WHERE SetID = " + Long.toString(setid) + " GROUP BY GroupID)") + 1;

        Log.d("DEBUG", "Next id is " + Integer.toString(next_word_group_id));

        return this._insertWordGroup(db, setid, next_word_group_id, words1, words2);
    }

    /*
     * Aktualisiert die Vokabeln in einer Wortgruppe.
     *
     * Params:
     * @setid: ID des Vokabel-Sets, dem die Wortgruppe angehört.
     * @wordgroupid: ID der Wortgruppe, welche aktualisiert werden soll.
     * @words1: Array mit den Wörtern der ersten Sprache.
     * @words2: Array mit den Wörtern der zweiten Sprache.
     *
     * Return:
     * Gibt true bei Erfolg zurück, sonst false.
     */
    public boolean updateWordGroup(long setid, int wordgroupid, String[] words1, String[] words2) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Ist die ID der zu aktualisierenden Wort-Gruppe größer als die größte der vorkommenden IDs im Set, gibt es die Wort-Gruppe nicht.
        if (DatabaseUtils.queryNumEntries(db, "(SELECT COUNT(GroupID) FROM VocabReleation WHERE SetID = " + Long.toString(setid) + " GROUP BY GroupID)") < wordgroupid) return false;

        db.beginTransaction();

        //db.execSQL("DELETE FROM VocabWords WHERE WordID IN(SELECT WordA FROM VocabReleation WHERE GroupID=" + Integer.toString(wordgroupid) + ") OR WordID IN(SELECT WordB FROM VocabReleation WHERE GroupID=" + Integer.toString(wordgroupid) + ")");
        db.execSQL("DELETE FROM VocabWords WHERE " +
                "WordID IN(SELECT WordA FROM VocabReleation WHERE GroupID=" + Integer.toString(wordgroupid) + " AND SetID=" + Long.toString(setid) + ") OR " +
                "WordID IN(SELECT WordB FROM VocabReleation WHERE GroupID=" + Integer.toString(wordgroupid) + " AND SetID=" + Long.toString(setid) + ")");

        db.delete("VocabReleation", "SetID=? AND GroupID=?", new String[] {Long.toString(setid), Integer.toString(wordgroupid)});

        if (this._insertWordGroup(db, setid, wordgroupid, words1, words2) == 0) {
            db.endTransaction();
            return false;
        }

        db.setTransactionSuccessful();
        db.endTransaction();

        return true;
    }

    /*
     * Legt eine neue Wortgruppe in einem Vorhandenen Vokabel-Set an. (Methode für den privaten Aufruf).
     *
     * Params:
     * @db: SQLiteDatabase-Objekt.
     * @setid: ID des Vokabel-Sets, in dem die neue Wort-Gruppe angelegt werden soll.
     * @wordgroupid: ID, die die neue Wort-Gruppe haben soll.
     * @words1: Array mit den Wörtern der ersten Sprache.
     * @words2: Array mit den Wörtern der zweiten Sprache.
     *
     * Return:
     * Die ID der neuen Wort-Gruppe oder 0 im Fehlerfall.
     */
    private int _insertWordGroup(SQLiteDatabase db, long setid, int wordgroupid, String[] words1, String[] words2) {
        Cursor cur = db.rawQuery("SELECT Lang1, Lang2 FROM VocabSets WHERE SetID=" + Long.toString(setid), null);

        cur.moveToFirst();

        if (cur.isAfterLast()) return 0;

        int lang1 = cur.getInt(0);
        int lang2 = cur.getInt(1);

        db.beginTransaction();
        ArrayList<Long> newWords1ID = new ArrayList();
        ArrayList<Long> newWords2ID = new ArrayList();

        /* Woerter der Sprache 1 uebernehmen. */
        for (String word : words1) {
            word = word.trim();

            if (!word.equals("")) {
                ContentValues cvWord = new ContentValues();

                cvWord.put("Lang", lang1);
                cvWord.put("Word", word);

                newWords1ID.add(db.insert("VocabWords", null, cvWord));
            }
        }

        /* Woerter der Sprache 2 uebernehmen. */
        for (String word : words2) {
            word = word.trim();

            if (!word.equals("")) {
                ContentValues cvWord = new ContentValues();

                cvWord.put("Lang", lang2);
                cvWord.put("Word", word);

                newWords2ID.add(db.insert("VocabWords", null, cvWord));
            }
        }

        if (newWords1ID.size() == 0 || newWords2ID.size() == 0) {
            db.endTransaction();
            return 0;
        }

        /* Relation zwischen den Woertern in Datenbank aufnehmen. */
        for (Long ID1 : newWords1ID) {
            for (Long ID2 : newWords2ID) {
                ContentValues cvWordRelation = new ContentValues();

                cvWordRelation.put("SetID", setid);
                cvWordRelation.put("GroupID", wordgroupid);
                cvWordRelation.put("WordA", ID1);
                cvWordRelation.put("WordB", ID2);

                db.insert("VocabReleation", null, cvWordRelation);
            }
        }

        db.setTransactionSuccessful();
        db.endTransaction();

        return wordgroupid;
    }

    /*
     * Entfernt eine Wortgruppe aus einem Vokabel-Set.
     *
     * Params:
     * @setid: ID des Vokabel-Sets, in dem sich die zu löschende Wortgruppe befindet.
     * @wordgroupid: ID der Wortgruppe, welche gelöscht werden soll.
     */
    public void deleteWordGroup(long setid, int wordgroupid) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("DELETE FROM VocabWords WHERE " +
                "WordID IN(SELECT WordA FROM VocabReleation WHERE GroupID=" + Integer.toString(wordgroupid) + " AND SetID=" + Long.toString(setid) + ") OR " +
                "WordID IN(SELECT WordB FROM VocabReleation WHERE GroupID=" + Integer.toString(wordgroupid) + " AND SetID=" + Long.toString(setid) + ")");

        db.delete("VocabReleation", "SetID=? AND GroupID=?", new String[] {Long.toString(setid), Integer.toString(wordgroupid)});

        db.execSQL("UPDATE VocabReleation SET GroupID = GroupID - 1 WHERE SetID=" + Long.toString(setid) + " AND GroupID > " + Integer.toString(wordgroupid));
    }
}
