package de.htwdd.vokabeltrainer;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;


import java.util.ArrayList;
import java.util.Random;

import de.htwdd.vokabeltrainer.helper.DBHelper;

/**
 * Created by Curtis on 24.06.2016.
 */
public class MultiChoiceFragment extends Fragment implements View.OnClickListener {

    public TextView[] choice = new TextView[4];
    DBHelper.VocabWord question;
    private TextView src;
    DBHelper.VocabWord secret;
    public TextView secrettv;
    FragmentActivity listener;
    private Boolean evaluation = true;

    private ArrayList<DBHelper.VocabWord> source;
    private ArrayList<DBHelper.VocabWord> destination;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    public MultiChoiceFragment() {

    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_multichoice, container , false);

        TextView dst1 = (TextView) rootView.findViewById(R.id.choice1);
        TextView dst2 = (TextView) rootView.findViewById(R.id.choice2);
        TextView dst3 = (TextView) rootView.findViewById(R.id.choice3);
        TextView dst4 = (TextView) rootView.findViewById(R.id.choice4);
        src = (TextView) rootView.findViewById(R.id.translatesrc);

        dst1.setOnClickListener(this);
        dst2.setOnClickListener(this);
        dst3.setOnClickListener(this);
        dst4.setOnClickListener(this);

        choice[0] = dst1;
        choice[1] = dst2;
        choice[2] = dst3;
        choice[3] = dst4;

        setVocabularyQuestion();


        return rootView;
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

    public void setVocabularyQuestion() {
        DBHelper db = new DBHelper(listener);
        Random r = new Random();

        SharedPreferences prefs = listener.getSharedPreferences(
                "de.htwdd.vokabeltrainer", listener.MODE_PRIVATE);
        Long setid = prefs.getLong("Set_ID", 0);
        this.evaluation = prefs.getBoolean("evaluation", true);

        ArrayList<ArrayList<DBHelper.VocabWord>> v = db.getRandomVocabWord();



        if (evaluation) {
            this.source = v.get(0);
            this.destination = v.get(1);
        } else {
            this.source = v.get(1);
            this.destination = v.get(0);
        }

        int nr = (r.nextInt(source.size()));

        src.setText(source.get(nr).word);
        question = source.get(nr);

        nr = (r.nextInt(destination.size()));
        this.secret = destination.get(nr);

        nr = (r.nextInt(4));
        int innernr=0;
        int i = 0;
        for (TextView tv : choice) {
            if (i == nr) {
                secrettv = tv;
                tv.setText(this.secret.word);
            } else {
                ArrayList<ArrayList<DBHelper.VocabWord>> newVok = db.getRandomVocabWord();

                if (this.evaluation) {
                    ArrayList<DBHelper.VocabWord> randomDestinationGroup = newVok.get(1);
                    innernr = (r.nextInt(randomDestinationGroup.size()));
                    tv.setText(randomDestinationGroup.get(innernr).word);
                }else {
                    ArrayList<DBHelper.VocabWord> randomDestinationGroup = newVok.get(0);
                    innernr = (r.nextInt(randomDestinationGroup.size()));
                    tv.setText(randomDestinationGroup.get(innernr).word);
                }
            }
            i++;
        }
        for (TextView tv : choice) {
            tv.setBackgroundColor(Color.WHITE);
        }
    }

    @Override
    public void onClick(View view) {
        DBHelper db = new DBHelper(listener);
        TextView v = (TextView) view;

        for (TextView tv : choice) {
            tv.setBackgroundColor(Color.parseColor("#FF4E4E")); // anstatt Color.RED
        }
        //(int setID, int word_ID) {
        if (v.getText().equals(this.secret.word)) {
            db.updateHits(question.setid, question.wordid, secret.wordid, evaluation);
        } else {
            db.updateMisses(question.setid, question.wordid, secret.wordid, evaluation);
        }

        this.secrettv.setBackgroundColor(Color.parseColor("#77FF80")); //anstatt Color.GREEN

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // Actions to do after 10 seconds
                setVocabularyQuestion();
            }
        }, 2000);
    }

}