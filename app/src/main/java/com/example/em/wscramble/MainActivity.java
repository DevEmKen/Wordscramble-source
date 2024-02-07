package com.example.em.wscramble;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView timerText;
    boolean vis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timerText = (TextView) findViewById(R.id.startTimerText);

        Button startBtn = (Button) findViewById(R.id.btnStart);
        Button recordsBtn = (Button) findViewById(R.id.btnRecords);
        Button instructionsBtn = (Button)findViewById(R.id.btnInstructions);
        final TextView instruc = (TextView)findViewById(R.id.instructionsText);
        vis = false;
        instruc.setVisibility(View.INVISIBLE);

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, startPage.class);
                startActivity(intent);
            }
        });

        recordsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, records.class);
                startActivity(intent);
            }
        });

        instructionsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!vis) {
                    instruc.setVisibility(View.VISIBLE);
                    vis = true;
                }
                else {
                    instruc.setVisibility(View.INVISIBLE);
                    vis = false;
                }
            }
        });
    }

    public void slideDown(View view){
        TranslateAnimation anim = new TranslateAnimation(0,0,0,view.getHeight());
        anim.setDuration(300);
        anim.setFillAfter(true);
        view.startAnimation(anim);
    }
}
