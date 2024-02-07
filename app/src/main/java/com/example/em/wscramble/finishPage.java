package com.example.em.wscramble;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.PriorityQueue;
import java.util.Scanner;

public class finishPage extends AppCompatActivity {

    TextView foundPercentageTextView, pointsTextView, boardTextView;
    ScrollView wordsOutputTextView;
    ProgressBar wordsBar, pointsBar;
    Button okBtn;

    trieNode trieDict;
    ArrayList<String> found = new ArrayList<>();

    int possibleScore = 0;

    static class trieNode implements Serializable {
        private static final long serialVersionUID = 1L;
        public boolean leaf;
        public trieNode[] child = new trieNode[26];

        public trieNode(){
            leaf = false;
            for(int i = 0; i < 26; i++){
                child[i] = null;
            }
        }
    }

    public static void insert(trieNode r, String dat){
        trieNode temp = r;
        int n = dat.length();

        for(int i = 0; i < n; i++){
            int ind = dat.charAt(i) - 97;
            if(temp.child[ind] == null){
                temp.child[ind] = new trieNode();
            }
            temp = temp.child[ind];
        }
        temp.leaf = true;
    }

    boolean isInRange(int i, int k, boolean visited[][]){
        return i >= 0 && i < 4 && k >= 0 && k < 4 && !visited[i][k];
    }

    void searchWord(trieNode r, char bog[][], int i, int k, boolean visited[][], String word){
        if(r.leaf == true && !found.contains(word)){
            found.add(word);
            possibleScore += getWordScore(word);
        }

        if(isInRange(i, k, visited)){
            visited[i][k] = true;

            for(int j = 0; j < 26; j++){
                if(r.child[j] != null){
                    char ch = (char) (j +97);
                    // Recur for 8 neighbors

                    if(isInRange(i+1, k+1,visited) && bog[i+1][k+1] == ch){
                        searchWord(r.child[j], bog, i+1, k+1,visited, word+ch);
                    }
                    if(isInRange(i, k+1,visited) && bog[i][k+1] == ch){
                        searchWord(r.child[j], bog, i, k+1,visited, word+ch);
                    }
                    if(isInRange(i-1, k+1,visited) && bog[i-1][k+1] == ch){
                        searchWord(r.child[j], bog, i-1, k+1,visited, word+ch);
                    }
                    if(isInRange(i+1, k,visited) && bog[i+1][k] == ch){
                        searchWord(r.child[j], bog, i+1, k,visited, word+ch);
                    }
                    if(isInRange(i+1, k-1,visited) && bog[i+1][k-1] == ch){
                        searchWord(r.child[j], bog, i+1, k-1,visited, word+ch);
                    }
                    if(isInRange(i, k-1,visited) && bog[i][k-1] == ch){
                        searchWord(r.child[j], bog, i, k-1,visited, word+ch);
                    }
                    if(isInRange(i-1, k-1,visited) && bog[i-1][k-1] == ch){
                        searchWord(r.child[j], bog, i-1, k-1,visited, word+ch);
                    }
                    if(isInRange(i-1, k,visited) && bog[i-1][k] == ch){
                        searchWord(r.child[j], bog, i-1, k, visited, word+ch);
                    }
                }
                visited[i][k] = false;
            }
        }
    }

    void findAll(char bog[][], trieNode r){
        boolean[][] visited = new boolean[4][4];
        trieNode temp = r;

        String str = "";

        for(int i = 0; i < 4; i++){
            for(int k = 0; k < 4; k++){
                if(temp != null) {
                    if (temp.child[bog[i][k] - 97] != null) {
                        str = str + bog[i][k];
                        searchWord(temp.child[(bog[i][k]) - 97], bog, i, k, visited, str);
                        str = "";
                    }
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish_page);

        char[][] board = new char[4][4];
        board[0] = getIntent().getCharArrayExtra("charRow1");
        board[1] = getIntent().getCharArrayExtra("charRow2");
        board[2] = getIntent().getCharArrayExtra("charRow3");
        board[3] = getIntent().getCharArrayExtra("charRow4");

        okBtn = (Button)findViewById(R.id.okBtn);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(finishPage.this, MainActivity.class);
                startActivity(intent);
            }
        });

        /*trieDict = new trieNode();
        try {
            DataInputStream tf = new DataInputStream(getAssets().open("dict.d"));
            Scanner bab = new Scanner(tf);
            while(bab.hasNextLine()){
                insert(trieDict, bab.nextLine());
            }
            FileOutputStream fo = getApplicationContext().openFileOutput("trieOut",getApplicationContext().MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fo);
            os.writeObject(trieDict);
            os.close();
            fo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }*/


        try {
            InputStream in = getAssets().open("trieOut");
            ObjectInputStream inObj = new ObjectInputStream(in);
            trieDict = (trieNode) inObj.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        findAll(board, trieDict);

        ArrayList<String> foundWordsList = new ArrayList<>();
        PriorityQueue<String> foundWords = (PriorityQueue<String>)getIntent().getSerializableExtra("foundWords");
        while(!foundWords.isEmpty()){
            String s = foundWords.poll();
            if(found.contains(s.toLowerCase().trim())){
                foundWordsList.add(s.toLowerCase().trim());
                found.remove(s);
            }
        }
        Collections.sort(found);
        Collections.sort(foundWordsList);

        StringBuilder wordsOutput = new StringBuilder();
        for(int i = 0; i < foundWordsList.size(); i++){
            wordsOutput.append(String.format("%-25s ✓\n", foundWordsList.get(i)));
        }
        wordsOutput.append("---------------------------\n");
        for(int i = 0; i < found.size(); i++){
            wordsOutput.append(String.format("%-20s\n", found.get(i)));
        }
        wordsOutputTextView = (ScrollView) findViewById(R.id.wordsOutput);
        TextView tv = new TextView(this);
        tv.setText(wordsOutput.toString());
        tv.setTypeface(Typeface.create("monospace", Typeface.NORMAL));
        wordsOutputTextView.addView(tv);


        //Set the progress bars
        int numPoints = 0;
        numPoints = getIntent().getIntExtra("numPoints", 0);

        wordsBar = (ProgressBar)findViewById(R.id.foundWordsBar);
        pointsBar = (ProgressBar)findViewById(R.id.pointsBar);
        foundPercentageTextView = (TextView)findViewById(R.id.foundWordsText);
        pointsTextView = (TextView)findViewById(R.id.pointsText);

        double wordsProgress = ((double)foundWordsList.size() / (double) found.size()) * 100;
        wordsBar.setProgress((int)wordsProgress);
        foundPercentageTextView.setText(foundWordsList.size() + "/" + found.size() + " words found");

        double progress = (((double) numPoints / (double) possibleScore) * 100);
        pointsBar.setProgress((int)progress);
        pointsTextView.setText(numPoints + "/" + possibleScore + " points earned");


        // Put in the board
        boardTextView = (TextView)findViewById(R.id.boardText);
        String t = "";
        for(int i = 0; i < 4; i++){
            for(int k = 0; k < 4; k++){
                t = t + (char)(board[i][k] - 32) + " ";
            }
            t = t + "\n";
        }
        boardTextView.setText(t);

        // Check if it's a record, and record if it is
        boolean isRecord = false;
        double[] records = new double[4]; // Best numwords, best numpoints, best %words, best %points
        try {
            InputStream ip = getApplicationContext().openFileInput("records");
            if(ip != null){
                InputStreamReader ipr = new InputStreamReader(ip);
                Scanner bab = new Scanner(ipr);
                for(int i = 0; i < 2; i++){
                    int n = Integer.parseInt(bab.nextLine());
                    records[i] = n;
                }
                for(int i = 2; i < 4; i++){
                    double n = Double.parseDouble(bab.nextLine());
                    records[i] = n;
                }
                ip.close();
                bab.close();
            }
        } catch (FileNotFoundException e) {
            isRecord = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(records[0] < foundWordsList.size() || records[1] < numPoints || records[2] < wordsProgress || records[3] < progress){
            isRecord = true;
        }
        if(isRecord){
            try {
                FileOutputStream op = openFileOutput("records", Context.MODE_PRIVATE);
                if(records[0] < foundWordsList.size())
                    op.write((foundWordsList.size() + "\n").getBytes());
                else
                    op.write((records[0] + "\n").getBytes());

                if(records[1] < numPoints)
                    op.write((numPoints + "\n").getBytes());
                else
                    op.write((records[1] + "\n").getBytes());

                if(records[2] < wordsProgress)
                    op.write((wordsProgress + "\n").getBytes());
                else
                    op.write((records[2] + "\n").getBytes());

                if(records[3] < progress)
                    op.write((progress + "\n").getBytes());
                else
                    op.write((records[3] + "\n").getBytes());
        } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    int getWordScore(String word){
        int numPoints = 0;
        switch(word.length()){
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
        return numPoints;
    }
}
