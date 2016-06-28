package de.htwdd.vokabeltrainer.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import de.htwdd.vokabeltrainer.R;

/**
 * Created by alex on 6/27/16.
 */
public class VocabWordGroupTextualArrayListAdapter extends ArrayAdapter<String[]> {
    public VocabWordGroupTextualArrayListAdapter(Context context, int resource, List<String[]> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(this.getContext()).inflate(R.layout.fragment_fragment_vocab_word_group_list_item, parent, false);
        }

        final TextView textView1 = (TextView) convertView.findViewById(R.id.listView_item1);
        textView1.setText(getItem(position)[0]);

        final TextView textView2 = (TextView) convertView.findViewById(R.id.listView_item2);
        textView2.setText(getItem(position)[1]);

        return convertView;
    }
}
