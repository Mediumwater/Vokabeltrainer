package de.htwdd.vokabeltrainer.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import de.htwdd.vokabeltrainer.helper.LanguageHelper;

/**
 * Created by alex on 6/9/16.
 */
public class VocabSetsCursorAdapter extends CursorAdapter {
    public VocabSetsCursorAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
    }

    @Override public void bindView(View view, Context context, Cursor cursor) {
        final LanguageHelper lh = new LanguageHelper();
        TextView textView = (TextView) view;
        textView.setText(cursor.getString(1) + " (" + lh.getLanguageNameByCode(cursor.getString(2)) + " - " + lh.getLanguageNameByCode(cursor.getString(3)) + ")");
    }

    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return View.inflate(context, android.R.layout.simple_list_item_1, null);
    }
}
