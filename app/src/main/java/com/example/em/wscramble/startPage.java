package com.example.em.wscramble;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Scanner;

public class startPage extends AppCompatActivity {
    // Must be global to support use in overridden onClick methods
    final int numWords = 79245;
    StringBuilder curWord = new StringBuilder();

    boolean isAdj[] = new boolean[16];
    boolean isUsed[] = new boolean[16];
    Button[] buttons = new Button[16];
    char[] charRow1 = new char[4];
    char[] charRow2 = new char[4];
    char[] charRow3 = new char[4];
    char[] charRow4 = new char[4];


    TextView letters;
    TextView points;
    TextView timerText;
    TextView startTimerText;

    String[] dict;
    Queue<String> foundWords;
    int numPoints;
    int index;

    // How long until time is up
    CountDownTimer timeLeft = new CountDownTimer(91000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            long mins = ((millisUntilFinished - 1000) / 1000) / 60;
            long secs = ((millisUntilFinished - 1000) / 1000) % 60;

            if(secs >= 10) {
                timerText.setText(mins + ":" + secs);
                if(mins == 0 && secs == 10){
                    timerText.setTextColor(Color.RED);
                }
            }
            else {
                timerText.setText(mins + ":0" + secs);
            }
            if(secs == 0 && mins == 0){
                slideDown(findViewById(R.id.slidingLayout));
                timerText.setText("0:00");
                startTimerText.setText("Time's Up!");
                startTimerText.setTextSize(30);
                findViewById(R.id.startTimerText).setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onFinish() {


            Intent intent = new Intent(startPage.this, finishPage.class);
            intent.putExtra("charRow1", charRow1);
            intent.putExtra("charRow2", charRow2);
            intent.putExtra("charRow3", charRow3);
            intent.putExtra("charRow4", charRow4);
            intent.putExtra("foundWords", (Serializable)foundWords);
            intent.putExtra("numPoints", numPoints);
            startActivity(intent);
        }
    };

    // 3, 2, 1
    CountDownTimer startTimer = new CountDownTimer(3000, 100) {
        @Override
        public void onTick(long millisUntilFinished) {
            startTimerText.setText(((millisUntilFinished / 1000) + 1) + "");
        }

        @Override
        public void onFinish() {
            findViewById(R.id.startTimerText).setVisibility(View.INVISIBLE);
            slideUp(findViewById(R.id.slidingLayout));
            startTimerText.setText("");
            timeLeft.start();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);



        // Build the dictionary array
        Scanner bab = null;
        try {
            InputStream fileIn = getAssets().open("dict.ser");
            ObjectInputStream inObj = new ObjectInputStream(fileIn);
            dict = (String[]) inObj.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }



        letters = (TextView) findViewById(R.id.letters);
        letters.setTextColor(Color.GRAY);
        points = (TextView) findViewById(R.id.points);
        timerText = (TextView) findViewById(R.id.timer);
        startTimerText = (TextView) findViewById(R.id.startTimerText);

        allTrue(isAdj);
        allFalse(isUsed);

        buttons[0] = (Button) findViewById(R.id.button1);
        buttons[1] = (Button) findViewById(R.id.button2);
        buttons[2] = (Button) findViewById(R.id.button3);
        buttons[3] = (Button) findViewById(R.id.button4);
        buttons[4] = (Button) findViewById(R.id.button5);
        buttons[5] = (Button) findViewById(R.id.button6);
        buttons[6] = (Button) findViewById(R.id.button7);
        buttons[7] = (Button) findViewById(R.id.button8);
        buttons[8] = (Button) findViewById(R.id.button9);
        buttons[9] = (Button) findViewById(R.id.button10);
        buttons[10] = (Button) findViewById(R.id.button11);
        buttons[11] = (Button) findViewById(R.id.button12);
        buttons[12] = (Button) findViewById(R.id.button13);
        buttons[13] = (Button) findViewById(R.id.button14);
        buttons[14] = (Button) findViewById(R.id.button15);
        buttons[15] = (Button) findViewById(R.id.button16);

        // Create the random board
        getDistribution(buttons);

        // To avoid making serializable
        for(int i = 0; i < 4; i++){
            for(int k = 0; k < 4; k++) {
                switch (i) {
                    case 0:
                        charRow1[k] = (char)(buttons[i * 4 + k].getText().charAt(0) + 32);
                    case 1:
                        charRow2[k] = (char)(buttons[i * 4 + k].getText().charAt(0) + 32);
                    case 2:
                        charRow3[k] = (char)(buttons[i * 4 + k].getText().charAt(0) + 32);
                    case 3:
                        charRow4[k] = (char)(buttons[i * 4 + k].getText().charAt(0) + 32);
                }
            }
        }

        // Set general listener for buttons
        for (index = 0; index < 16; index++) {
            buttons[index].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Find which button is being pressed
                    int curBtn = 0;
                    for (int i = 0; i < 16; i++) {
                        if (v.getId() == buttons[i].getId()) {
                            curBtn = i;
                            break;
                        }
                    }
                    if (isAdj[curBtn] && !isUsed[curBtn]) {
                        // Add letter to word and update all helper arrays
                        isUsed[curBtn] = true;
                        curWord.append(buttons[curBtn].getText());
                        buttons[curBtn].getBackground().setColorFilter(Color.TRANSPARENT, PorterDuff.Mode.MULTIPLY);
                        setAdj(isAdj, curBtn);
                        // Update the text
                        letters.setText(curWord.toString());
                    }
                    letters.setTextColor(Color.GRAY);

                }
            });
        }


        Button okButton = (Button) findViewById(R.id.buttonOk);
        foundWords = new PriorityQueue<>();
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Reset buttons and change letters
                for(int i = 0; i < 16; i++){
                    buttons[i].getBackground().clearColorFilter();
                }
                allTrue(isAdj);
                allFalse(isUsed);

                // Search for word in dictionary
                int ind = Arrays.binarySearch(dict, curWord.toString().toLowerCase());
                if(ind >= 0 && !foundWords.contains(curWord.toString())){
                    letters.setTextColor(Color.GREEN);
                    foundWords.add(curWord.toString());

                    switch(curWord.toString().length()){
                        case 3:
                            numPoints += 3;
                            break;
                        case 4:
                            numPoints += 4;
                            break;
                        case 5:
                            numPoints += 6;
                            break;
                        case 6:
                            numPoints += 8;
                            break;
                        case 7:
                            numPoints += 11;
                            break;
                        case 8:
                            numPoints += 14;
                            break;
                        case 9:
                            numPoints += 17;
                            break;
                        case 10:
                            numPoints += 20;
                            break;
                        case 11:
                            numPoints += 23;
                            break;
                        case 12:
                            numPoints += 26;
                            break;
                        case 13:
                            numPoints += 29;
                            break;
                        case 14:
                            numPoints += 32;
                            break;
                        case 15:
                            numPoints += 35;
                            break;
                        case 16:
                            numPoints += 50;
                            break;
                    }
                    points.setText(numPoints + "");
                } else {
                    if(foundWords.contains(curWord.toString()))
                        letters.setTextColor(Color.BLUE);
                    else
                        letters.setTextColor(Color.RED);
                }
                curWord = new StringBuilder();
            }
        });
        findViewById(R.id.slidingLayout).setVisibility(View.INVISIBLE);
        startTimer.start();
    }

    void getDistribution(Button[] buttons){
        int[] letterWeights = { 6, // A
                2, // B
                3, // C, etc.
                3, 10, 2, 2, 6, 6, 1, 2, 4, 3, 6, 7, 3, 1, 5, 6, 10, 3, 2, 3, 1, 3, 1};
        double totalChance = 0;
        for(int k = 0; k < 26; k++){
            totalChance += letterWeights[k];
        }
        for(int i = 0; i < 16; i++){
            double theRand = Math.random() * totalChance;
            double counted = 0;
            for(int k = 0; k < 26; k++){
                counted += letterWeights[k];
                if(theRand <= counted){
                    char letter = (char) (k + 65);
                    String l = letter + "";
                    buttons[i].setText(l);
                    break;
                }
            }
        }
    }

    void allFalse(boolean[] bool){
        for(int i = 0; i < bool.length; i++){
            bool[i] = false;
        }
    }

    void allTrue(boolean[] bool){
        for(int i = 0; i < bool.length; i++){
            bool[i] = true;
        }
    }

    void setAdj(boolean[] isAdj, int index){
        allFalse(isAdj);
        int[] possibleAdj = new int[8];
        possibleAdj[0] = index - 5;
        possibleAdj[1] = index - 4;
        possibleAdj[2] = index - 3;
        possibleAdj[3] = index - 1;
        possibleAdj[4] = index + 1;
        possibleAdj[5] = index + 3;
        possibleAdj[6] = index + 4;
        possibleAdj[7] = index + 5;
        for(int i = 0; i < 8; i++){
            if(possibleAdj[i] >= 0 && possibleAdj[i] <= 15){
                isAdj[possibleAdj[i]] = true;
            }
        }
    }

    public void slideDown(View view){
        TranslateAnimation anim = new TranslateAnimation(0,0,0,view.getHeight());
        anim.setDuration(300);
        anim.setFillAfter(true);
        view.startAnimation(anim);
    }

    public void slideUp(View view){
        TranslateAnimation anim = new TranslateAnimation(0,0,view.getHeight(),0);
        anim.setDuration(300);
        anim.setFillAfter(true);
        findViewById(R.id.slidingLayout).setVisibility(View.VISIBLE);
        view.startAnimation(anim);
    }

    public void onBackPressed() {
        timeLeft.cancel();
        startTimer.cancel();
        super.onBackPressed();
    }
}
