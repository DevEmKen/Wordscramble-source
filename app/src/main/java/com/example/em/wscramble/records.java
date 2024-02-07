package com.example.em.wscramble;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

public class records extends AppCompatActivity {

    TextView bestNumWordsText, bestPointsText, bestPercentWordsText, bestPercentPointsText;
    ProgressBar bestPercentWordsBar, bestPercentPointsBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);

        bestNumWordsText = (TextView) findViewById(R.id.bestNumWordsText);
        bestPointsText = (TextView)findViewById(R.id.bestPointsText);
        bestPercentWordsText = (TextView)findViewById(R.id.bestPercentWordsText);
        bestPercentPointsText = (TextView)findViewById(R.id.bestPercentPointsText);

        bestPercentWordsBar = (ProgressBar)findViewById(R.id.bestPercentWordsBar);
        bestPercentPointsBar = (ProgressBar)findViewById(R.id.bestPercentPointsBar);

        double[] records = {0,0,0,0}; // Best numwords, best numpoints, best %words, best %points
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

        } catch (IOException e) {
            e.printStackTrace();
        }

        bestNumWordsText.setText((int)records[0] + "");
        bestPointsText.setText((int)records[1] + "");

        int temp = (int)(records[2] * 10);
        double rounded = temp / 10;
        bestPercentWordsText.setText("Best percent of possible words found: " + rounded + "%");
        bestPercentWordsBar.setProgress((int)records[2]);

        temp = (int)(records[3] * 10);
        rounded = temp / 10;
        bestPercentPointsText.setText("Best percent of possible points achieved: " + rounded + "%");
        bestPercentPointsBar.setProgress((int)records[3]);

    }
}
