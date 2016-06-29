package de.htwdd.vokabeltrainer;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import de.htwdd.vokabeltrainer.helper.DBHelper;

/**
 * Created by BigT on 27.06.2016.
 */
//public class FreitextActivity extends Fragment implements AdapterView.OnItemSelectedListener{

public class FreitextFragment extends Fragment {

    FragmentActivity listener;

    public FreitextFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        DBHelper db = new DBHelper(listener);
        View RootView = inflater.inflate(R.layout.fragment_freitext, container , false);

        SharedPreferences prefs = listener.getSharedPreferences(
                "de.htwdd.vokabeltrainer", listener.MODE_PRIVATE);
        Long setid = prefs.getLong("Set_ID", 0 );


        ArrayList<ArrayList<DBHelper.VocabWord>> v = db.getRandomVocabWord(setid.intValue());

        ArrayList<DBHelper.VocabWord> source = v.get(0);
        ArrayList<DBHelper.VocabWord> destination = v.get(1);

        




        return RootView;
    }

    // This event is triggered soon after onCreateView().
    // onViewCreated() is only called if the view returned from onCreateView() is non-null.
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        //ListView lv = (ListView) view.findViewById(R.id.lvSome);
        //lv.setAdapter(adapter);
    }

    @Override
    public void onAttach (Context context){
        super.onAttach(context);
        if (context instanceof Activity) {
            this.listener = (FragmentActivity) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    // This method is called after the parent Activity's onCreate() method has completed.
    // Accessing the view hierarchy of the parent activity must be done in the onActivityCreated.
    // At this point, it is safe to search for activity View objects by their ID, for example.
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        //   void onFragmentInteraction(Uri uri);
    }
}