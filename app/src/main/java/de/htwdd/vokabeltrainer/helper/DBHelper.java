package de.htwdd.vokabeltrainer.helper;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by alex on 5/31/16.
 * Hier befindet sich der Code zum lesen und manipulieren der Datenbank.
 */
public class DBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "VokabelTrainerDatabase.db";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME , null, 1);
        //context.deleteDatabase(DATABASE_NAME); // Datenbank immer wieder loeschen, damit onCreate-Methode aufgerufen wird.
    }

    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE VocabSets (SetID INTEGER PRIMARY KEY, LangA INTEGER NOT NULL, LangB INTEGER NOT NULL, Description VARCHAR NOT NULL)");
        db.execSQL("CREATE TABLE VocabWords (WordID INTEGER PRIMARY KEY, Lang INTEGER NOT NULL, Word VARCHAR NOT NULL, Misses INTEGER DEFAULT 0, Hits INTEGER DEFAULT 0)");
        db.execSQL("CREATE TABLE VocabReleation (SetID INTEGER, GroupID INTEGER, WordA INTEGER, WordB INTEGER, PRIMARY KEY (SetID, WordA, WordB))");

        /*** Testcode - spaeter entfernen ***/
        db.execSQL("INSERT INTO VocabSets (LangA, LangB, Description) VALUES (15, 1, \"Alltag\")");
        db.execSQL("INSERT INTO VocabSets (LangA, LangB, Description) VALUES (15, 1, \"IT\")");

        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (1, 1, \"to be allowed to\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (2, 1, \"to have the right to\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (3, 1, \"to may\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (4, 1, \"may\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (5, 33, \"dürfen\")");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 1, 1, 5)");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 1, 2, 5)");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 1, 3, 5)");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 1, 4, 5)");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (6, 1, \"nine\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (7, 33, \"neun\")");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 2, 6, 7)");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (8, 1, \"day\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (9, 33, \"Tag\")");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 3, 8, 9)");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (10, 1, \"which one's\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (11, 1, \"of which\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (12, 1, \"whose\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (13, 33, \"deren\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (14, 33, \"dessen\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (15, 33, \"wessen\")");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 4, 10, 13)");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 4, 10, 14)");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 4, 10, 15)");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 4, 11, 13)");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 4, 11, 14)");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 4, 11, 15)");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 4, 12, 13)");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 4, 12, 14)");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 4, 12, 15)");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (16, 1, \"should\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (17, 1, \"ought to\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (18, 1, \"to have to\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (19, 33, \"müssen\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (20, 33, \"sollen\")");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 5, 16, 19)");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 5, 16, 20)");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 5, 17, 19)");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 5, 17, 20)");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 5, 18, 19)");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 5, 18, 20)");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (21, 1, \"self\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (22, 1, \"itself\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (23, 1, \"herself\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (24, 1, \"himself\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (25, 33, \"selber\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (26, 33, \"selbst\")");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 6, 21, 25)");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 6, 21, 26)");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 6, 22, 25)");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 6, 22, 26)");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 6, 23, 25)");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 6, 23, 26)");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 6, 24, 25)");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 6, 24, 26)");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (27, 1, \"nothing\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (28, 33, \"nichts\")");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 7, 27, 28)");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (29, 1, \"infant\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (30, 33, \"Kind\")");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 8, 29, 30)");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (31, 1, \"one\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (32, 33, \"ein gewisser\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (33, 33, \"einer\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (34, 33, \"irgend einer\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (35, 33, \"irgendwer\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (36, 33, \"jemand\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (37, 33, \"man\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (38, 33, \"ein\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (39, 33, \"eine\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (40, 33, \"eins\")");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 9, 31, 32)");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 9, 31, 33)");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 9, 31, 34)");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 9, 31, 35)");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 9, 31, 36)");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 9, 31, 37)");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 9, 31, 38)");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 9, 31, 39)");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 9, 31, 40)");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (41, 1, \"small\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (42, 33, \"gering\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (43, 33, \"klein\")");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 10, 41, 42)");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 10, 41, 43)");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (44, 1, \"so that\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (45, 33, \"damit\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (46, 33, \"dass\")");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 11, 44, 45)");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 11, 44, 46)");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (47, 1, \"an\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (48, 33, \"ein gewisser\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (49, 33, \"einer\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (50, 33, \"irgend einer\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (51, 33, \"irgendwer\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (52, 33, \"jemand\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (53, 33, \"à\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (54, 33, \"je\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (55, 33, \"zu\")");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 12, 47, 48)");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 12, 47, 49)");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 12, 47, 50)");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 12, 47, 51)");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 12, 47, 52)");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 12, 47, 53)");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 12, 47, 54)");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 12, 47, 55)");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (56, 1, \"at the rate of\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (57, 33, \"à\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (58, 33, \"je\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (59, 33, \"zu\")");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 13, 56, 57)");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 13, 56, 58)");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 13, 56, 59)");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (60, 1, \"man\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (61, 33, \"Mensch\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (62, 33, \"Mann\")");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 14, 60, 61)");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 14, 60, 62)");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (63, 1, \"to be acquainted with\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (64, 33, \"kennen\")");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 15, 63, 64)");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (65, 1, \"six\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (66, 33, \"sechs\")");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 16, 65, 66)");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (67, 1, \"everything\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (68, 33, \"alles\")");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 17, 67, 68)");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (69, 1, \"to lay hold of\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (70, 33, \"fassen\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (71, 33, \"nehmen\")");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 18, 69, 70)");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 18, 69, 71)");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (72, 1, \"or\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (73, 33, \"oder\")");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 19, 72, 73)");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (74, 1, \"ten\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (75, 33, \"zehn\")");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 20, 74, 75)");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (76, 1, \"three\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (77, 33, \"drei\")");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 21, 76, 77)");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (78, 1, \"two\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (79, 33, \"zwei\")");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 22, 78, 79)");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (80, 1, \"five\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (81, 33, \"fünf\")");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 23, 80, 81)");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (82, 1, \"to travel\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (83, 33, \"fahren\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (84, 33, \"reisen\")");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 24, 82, 83)");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 24, 82, 84)");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (85, 1, \"to know\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (86, 33, \"kennen\")");
        db.execSQL("INSERT INTO VocabWords (WordID, Lang, Word) VALUES (87, 33, \"wissen\")");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 25, 85, 86)");
        db.execSQL("INSERT INTO VocabReleation (SetID, GroupID, WordA, WordB) VALUES (1, 25, 85, 87)");

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
     * Gibt die Namen aller Vokabel-Sets als Array zurueck.
     */
    public ArrayList<String> getAllVocabSets()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM VocabSets", null);
        cur.moveToFirst();

        ArrayList<String> al = new ArrayList<>();

        //al.add("Testeintrag");

        while (cur.isAfterLast() == false) {
            al.add(cur.getString(cur.getColumnIndex("Description")));
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
        Random random = new Random();

        return this.getVocabWords(setid, random.nextInt(cnt) + 1);
    }
}
