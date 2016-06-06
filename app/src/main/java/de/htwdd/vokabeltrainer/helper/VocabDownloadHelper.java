package de.htwdd.vokabeltrainer.helper;

import android.content.Context;
import android.os.Debug;
import android.util.Log;
import android.util.Xml;

import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import de.htwdd.vokabeltrainer.VokverActivity;

/**
 * Created by alex on 6/3/16.
 */
public class VocabDownloadHelper {
    private Context ctx;
    public static final String DOWNLOAD_BASE_URL = "http://hpvag.de/vokabeltrainer/";
    public static final String CONTENT_FILE = "content.xml";

    public VocabDownloadHelper(Context ctx) {
        this.ctx = ctx;
    }

    /*
     * Haelt Informationen ueber downloadbare Vokabeldateien. Wird beim
     * Abrufen der verfuegbaren (herunterladbaren) Vokabel-Sets benoetigt.
     */
    public static class DownloadableVocSet {
        public final String description;
        public final String downloadurl;
        public final String lang1;
        public final String lang2;

        private DownloadableVocSet(String description, String downloadurl, String lang1, String lang2) {
            this.description = description;
            this.downloadurl = downloadurl;
            this.lang1 = lang1;
            this.lang2 = lang2;
        }
    }

    /*
     * Oeffnet eine HTTP-Verbindung zum Herunterladen einer Datei (url) und gibt einen InputStream zurueck.
     */
    private static InputStream downloadFile(String url) throws Exception {
        URL urlobj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) urlobj.openConnection();
        connection.connect();

        return connection.getInputStream();
    }

    /*
     * Laed Informationen ueber die, auf dem Server vorhandenen und herunterladbaren, Vokabel-Sets
     * herunter und gibt diese als ArrayList vom Typ DownloadableVocSet zurueck.
     */
    public static ArrayList<DownloadableVocSet> getDownloadableVocabSets() {
        InputStream xml_input_stream;
        ArrayList<DownloadableVocSet> vocs = new ArrayList();

        try {
            xml_input_stream = downloadFile(DOWNLOAD_BASE_URL + CONTENT_FILE);

            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(xml_input_stream, null);
            parser.nextTag();

            parser.require(XmlPullParser.START_TAG, null, "vocabsets");

            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }

                String name = parser.getName();

                if (name.equals("vocabset")) {
                    Log.d("DEBUG", "vocabset gefunden");
                    vocs.add(xml_read_vocabset(parser));
                } else {
                    skip_xml_tags(parser);
                }
            }
        } catch (Exception e) {
            // Nothing....
            e.printStackTrace();
        }

        return vocs;
    }

    /*
     * Parst ein vocabset-Element der XML-Struktur mit Informationen ueber ein herunterladbares Vokabel-Set.
     */
    private static DownloadableVocSet xml_read_vocabset(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, null, "vocabset");

        String description = parser.getAttributeValue(null, "description");
        String lang1 = parser.getAttributeValue(null, "lang1");
        String lang2 = parser.getAttributeValue(null, "lang2");
        String downloadurl = null;

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();

            if (name.equals("downloadurl")) {
                downloadurl = xml_read_downloadurl(parser);
            } else {
                skip_xml_tags(parser);
            }
        }

        return new DownloadableVocSet(description, downloadurl, lang1, lang2);
    }

    /*
     * Parst die downloadurl-Elemente der XML-Struktur, welche die URLs enthalten, ueber welche die Vokabel-Sets
     * heruntergeladen werden koennen.
     */
    private static String xml_read_downloadurl(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, null, "downloadurl");
        String url = xml_read_text(parser);
        parser.require(XmlPullParser.END_TAG, null, "downloadurl");

        return url;
    }

    /*
     * Gibt den Text-Inhalt eines Knotens in der XML-Struktur zurueck.
     */
    private static String xml_read_text(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";

        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }

        return result;
    }


    /*
     * Arbeitet alle Kindelemente eines Elements in der XML-Struktur ab. Wird benoetigt, um Elemente auf zu
     * konsumieren, welche nicht benoetigt werden.
     */
    private static void skip_xml_tags(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

    public void downloadAndInstallVocabSet(String filename) {
        try {
            InputStream json_input_stream;
            json_input_stream = downloadFile(DOWNLOAD_BASE_URL + filename);

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(json_input_stream, "utf-8"), 8);

            StringBuilder stringBuilder = new StringBuilder();
            String line = null;

            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line + "\n");
            }

            DBHelper db = new DBHelper(this.ctx);
            db.createVocabSetFromJSONString(stringBuilder.toString());
        } catch (Exception e) {
            // Nothing....
            e.printStackTrace();
        }
    }
}
