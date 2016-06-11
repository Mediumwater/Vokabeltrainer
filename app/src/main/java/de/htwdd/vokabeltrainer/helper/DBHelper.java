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

        /*** Testcode - spaeter entfernen ***/
        /*db.execSQL("INSERT INTO VocabSets (Lang1, Lang2, Description) VALUES (\"en\", \"de\", \"Alltag\")");
        db.execSQL("INSERT INTO VocabSets (Lang1, Lang2, Description) VALUES (\"en\", \"de\", \"IT\")");

        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (1, \"en\", \"to be allowed to\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (2, \"en\", \"to have the right to\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (3, \"en\", \"to may\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (4, \"en\", \"may\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (5, \"de\", \"dürfen\")");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 1, 1, 5)");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 1, 2, 5)");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 1, 3, 5)");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 1, 4, 5)");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (6, \"en\", \"nine\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (7, \"de\", \"neun\")");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 2, 6, 7)");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (8, \"en\", \"day\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (9, \"de\", \"Tag\")");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 3, 8, 9)");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (10, \"en\", \"which one's\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (11, \"en\", \"of which\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (12, \"en\", \"whose\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (13, \"de\", \"deren\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (14, \"de\", \"dessen\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (15, \"de\", \"wessen\")");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 4, 10, 13)");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 4, 10, 14)");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 4, 10, 15)");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 4, 11, 13)");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 4, 11, 14)");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 4, 11, 15)");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 4, 12, 13)");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 4, 12, 14)");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 4, 12, 15)");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (16, \"en\", \"should\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (17, \"en\", \"ought to\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (18, \"en\", \"to have to\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (19, \"de\", \"müssen\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (20, \"de\", \"sollen\")");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 5, 16, 19)");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 5, 16, 20)");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 5, 17, 19)");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 5, 17, 20)");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 5, 18, 19)");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 5, 18, 20)");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (21, \"en\", \"self\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (22, \"en\", \"itself\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (23, \"en\", \"herself\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (24, \"en\", \"himself\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (25, \"de\", \"selber\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (26, \"de\", \"selbst\")");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 6, 21, 25)");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 6, 21, 26)");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 6, 22, 25)");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 6, 22, 26)");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 6, 23, 25)");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 6, 23, 26)");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 6, 24, 25)");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 6, 24, 26)");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (27, \"en\", \"nothing\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (28, \"de\", \"nichts\")");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 7, 27, 28)");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (29, \"en\", \"infant\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (30, \"de\", \"Kind\")");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 8, 29, 30)");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (31, \"en\", \"one\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (32, \"de\", \"ein gewisser\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (33, \"de\", \"einer\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (34, \"de\", \"irgend einer\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (35, \"de\", \"irgendwer\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (36, \"de\", \"jemand\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (37, \"de\", \"man\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (38, \"de\", \"ein\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (39, \"de\", \"eine\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (40, \"de\", \"eins\")");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 9, 31, 32)");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 9, 31, 33)");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 9, 31, 34)");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 9, 31, 35)");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 9, 31, 36)");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 9, 31, 37)");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 9, 31, 38)");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 9, 31, 39)");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 9, 31, 40)");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (41, \"en\", \"small\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (42, \"de\", \"gering\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (43, \"de\", \"klein\")");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 10, 41, 42)");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 10, 41, 43)");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (44, \"en\", \"so that\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (45, \"de\", \"damit\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (46, \"de\", \"dass\")");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 11, 44, 45)");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 11, 44, 46)");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (47, \"en\", \"an\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (48, \"de\", \"ein gewisser\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (49, \"de\", \"einer\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (50, \"de\", \"irgend einer\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (51, \"de\", \"irgendwer\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (52, \"de\", \"jemand\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (53, \"de\", \"à\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (54, \"de\", \"je\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (55, \"de\", \"zu\")");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 12, 47, 48)");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 12, 47, 49)");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 12, 47, 50)");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 12, 47, 51)");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 12, 47, 52)");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 12, 47, 53)");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 12, 47, 54)");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 12, 47, 55)");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (56, \"en\", \"at the rate of\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (57, \"de\", \"à\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (58, \"de\", \"je\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (59, \"de\", \"zu\")");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 13, 56, 57)");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 13, 56, 58)");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 13, 56, 59)");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (60, \"en\", \"man\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (61, \"de\", \"Mensch\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (62, \"de\", \"Mann\")");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 14, 60, 61)");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 14, 60, 62)");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (63, \"en\", \"to be acquainted with\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (64, \"de\", \"kennen\")");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 15, 63, 64)");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (65, \"en\", \"six\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (66, \"de\", \"sechs\")");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 16, 65, 66)");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (67, \"en\", \"everything\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (68, \"de\", \"alles\")");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 17, 67, 68)");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (69, \"en\", \"to lay hold of\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (70, \"de\", \"fassen\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (71, \"de\", \"nehmen\")");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 18, 69, 70)");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 18, 69, 71)");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (72, \"en\", \"or\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (73, \"de\", \"oder\")");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 19, 72, 73)");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (74, \"en\", \"ten\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (75, \"de\", \"zehn\")");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 20, 74, 75)");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (76, \"en\", \"three\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (77, \"de\", \"drei\")");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 21, 76, 77)");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (78, \"en\", \"two\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (79, \"de\", \"zwei\")");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 22, 78, 79)");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (80, \"en\", \"five\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (81, \"de\", \"fünf\")");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 23, 80, 81)");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (82, \"en\", \"to travel\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (83, \"de\", \"fahren\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (84, \"de\", \"reisen\")");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 24, 82, 83)");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 24, 82, 84)");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (85, \"en\", \"to know\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (86, \"de\", \"kennen\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (87, \"de\", \"wissen\")");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 25, 85, 86)");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 25, 85, 87)");*/

        Log.d("DEBUG", "Datenbank kreiert.");
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
