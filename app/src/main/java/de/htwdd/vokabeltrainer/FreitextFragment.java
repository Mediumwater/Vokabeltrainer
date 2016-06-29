package de.htwdd.vokabeltrainer;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

import de.htwdd.vokabeltrainer.helper.DBHelper;

/**
 * Created by BigT on 27.06.2016.
 */
//public class FreitextActivity extends Fragment implements AdapterView.OnItemSelectedListener{

public class FreitextFragment extends Fragment implements View.OnClickListener {

    FragmentActivity listener;
    private TextView src;
    private TextView destwordA;
    private TextView destwordB;
    private TextView CheatTV;
    private EditText et;
    private DBHelper db;

    private Boolean evaluation;

    private ArrayList<DBHelper.VocabWord> destination;
    private ArrayList<DBHelper.VocabWord> source;



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

        View rootView = inflater.inflate(R.layout.fragment_freitext, container , false);

        src = (TextView) rootView.findViewById(R.id.tvsrc);
        Button bcheck = (Button) rootView.findViewById(R.id.bsetcheck);
        this.et = (EditText) rootView.findViewById(R.id.etans);
        this.destwordA = (TextView) rootView.findViewById(R.id.destwordA);
        this.destwordB = (TextView) rootView.findViewById(R.id.destwordB);
        this.CheatTV = (TextView) rootView.findViewById(R.id.CheatTV);




/*
        Button speech = (Button) rootView.findViewById(R.id.speech);

        speech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "You may speak!");
                intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
                startActivityForResult(intent, 1);
            }
        });
*/
        bcheck.setOnClickListener(this);
        setVocabularyQuestion();
        return rootView;
    }


/*
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Fragment fragment = getFragmentManager().findFragmentById(R.id.mainframe);
        fragment.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {//RESULT_OK become red
            ArrayList<String> results;
            results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            TextView speechText = (TextView)findViewById(R.id.textView1);//findViewByID become red
            String str="";
            for(int i=0;i<results.size();i++){
                str+= results.get(i);
            }
            if (str.equals("five")) {
                speechText.setText(str);
            }else{
                speechText.setText("It's not: " + str);
            }
        }
    }
*/








    public void setVocabularyQuestion() {
        db = new DBHelper(listener);
        SharedPreferences prefs = listener.getSharedPreferences(
                "de.htwdd.vokabeltrainer", listener.MODE_PRIVATE);
        Long setid = prefs.getLong("Set_ID", 0);
        this.evaluation = prefs.getBoolean("evaluation", true);

        if (setid == 0)
            return;
        ArrayList<ArrayList<DBHelper.VocabWord>> v = db.getRandomVocabWord(setid.intValue());
        if (v.isEmpty())
            return;

        if (evaluation) {
            this.source = v.get(0);
            this.destination = v.get(1);
        } else {
            this.source = v.get(1);
            this.destination = v.get(0);
        }


        //this.CheatTV.setText(destination.get(0).word); // unkomment to see the right answer in the View

        Random r = new Random();
        int nr = (r.nextInt(source.size()));
        src.setText(source.get(nr).word);
        et.setText("");
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

    @Override
    public void onClick(View v) {
        Button b = (Button) v;
        String answer = this.et.getText().toString();
        String question = this.src.getText().toString();

        DBHelper.VocabWord answerVW = null;
        DBHelper.VocabWord questionVW = null;

        boolean fail = true;

        String displayAnswerA = "";
        int beginColoringA = 0;
        int endColoringA = 0;
        for (DBHelper.VocabWord vwA : destination){
            if (vwA.word.equals(answer)){
                //richtig
                beginColoringA = displayAnswerA.length();
                endColoringA = beginColoringA + vwA.word.length();
                answerVW = vwA;
                fail = false;
            }
            displayAnswerA += vwA.word + "\n\n";
        }

        String displayAnswerB = "";
        int beginColoringB = 0;
        int endColoringB = 0;
        for (DBHelper.VocabWord vwB : source){
            if (vwB.word.equals(question)){
                beginColoringB = displayAnswerB.length();
                endColoringB = beginColoringB + vwB.word.length();
                questionVW = vwB;
                Log.d("Hier", "Hier bei der DB");
                Log.d("Hier", "fail = " + fail);
                if (!fail)
                    db.updateHits(questionVW.setid, answerVW.wordid, questionVW.wordid, this.evaluation);
            }
            displayAnswerB += vwB.word + "\n\n";
        }

        if (fail){
            Log.d("Hier", "Hier bei der DB");
            Log.d("Hier", "fail = " + fail);
            db.updateMissesword(questionVW.setid, questionVW.wordid, this.evaluation);
        }
        Spannable spannableA = new SpannableString(displayAnswerA);
        spannableA.setSpan(new ForegroundColorSpan(Color.parseColor("#42A5F5")),
                beginColoringA, endColoringA, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        this.destwordB.setText(spannableA, TextView.BufferType.SPANNABLE);


        Spannable spannableB = new SpannableString(displayAnswerB);
        spannableB.setSpan(new ForegroundColorSpan(Color.parseColor("#42A5F5")),
                beginColoringB, endColoringB, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        this.destwordA.setText(spannableB, TextView.BufferType.SPANNABLE);


        //src.setBackgroundColor(Color.parseColor("#42A5F5"));
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // Actions to do after 10 seconds
                //src.setBackgroundColor(Color.parseColor("#FFFFFF"));
                setVocabularyQuestion();
            }
        }, 3000);


        //this.destwordA.setText(displayAnswer);
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        //   void onFragmentInteraction(Uri uri);
    }
}