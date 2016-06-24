package de.htwdd.vokabeltrainer;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

import de.htwdd.vokabeltrainer.helper.DBHelper;
import de.htwdd.vokabeltrainer.helper.LanguageHelper;

/**
 * Created by Curtis on 24.06.2016.
 */
public class MultiChoiceActivity extends AppCompatActivity implements View.OnClickListener {

    public TextView[] choice = new TextView[4] ;
    private TextView src;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multichoice);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Multiple Choice");
        setSupportActionBar(toolbar);

        TextView dst1 = (TextView) findViewById(R.id.choice1);
        TextView dst2 = (TextView) findViewById(R.id.choice2);
        TextView dst3 = (TextView) findViewById(R.id.choice3);
        TextView dst4 = (TextView) findViewById(R.id.choice4);
        src = (TextView) findViewById(R.id.translatesrc);

        dst1.setOnClickListener(this);
        dst2.setOnClickListener(this);
        dst3.setOnClickListener(this);
        dst4.setOnClickListener(this);

        Log.d("DEBUG","!");

        choice[0] = dst1;
        choice[1] = dst2;
        choice[2] = dst3;
        choice[3] = dst4;

        Log.d("DEBUG","!");

        setVocabularyQuestion();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    public void setVocabularyQuestion(){
        DBHelper db = new DBHelper(this);
        Log.d("DEBUG","Oder Hier");

        ArrayList<DBHelper.VocabWord> v = db.getRandomVocabWord();
        Log.d("DEBUG","Hier");
        Log.d("DEBUG", v.get(0).word);

        src.setText(v.get(0).word);

        Random r = new Random();
        int nr = (r.nextInt(4));

        Log.d("DEBUG","asasas");
        int i = 0;
        for (TextView tv : choice) {
            if (i == nr){
                Log.d("DEBUG","aadjhkjjk");
                tv.setText(v.get(1).word);
            }else {
                Log.d("DEBUG","aadjhkjjasdasdasdasdk");
                ArrayList<DBHelper.VocabWord> newVok = db.getRandomVocabWord();
                tv.setText(newVok.get(1).word);
            }
            i++;
        }






        LanguageHelper lh = LanguageHelper.getInstance();

    }



    @Override
    public void onClick(View view) {
        // do whatever you want here based on the view being passed
        TextView v = (TextView) view;
        switch(v.getId()) {
            case R.id.choice1:
                view.setBackgroundColor(Color.GREEN);
                break;
            case R.id.choice2:
                view.setBackgroundColor(Color.GREEN);
                break;
            case R.id.choice3:
                view.setBackgroundColor(Color.GREEN);
                break;
            case R.id.choice4:
                view.setBackgroundColor(Color.GREEN);
                break;
        }
    }
}