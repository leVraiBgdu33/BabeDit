package com.example.parrouy.babedit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;


public class MainActivity extends Activity {

    private ImageButton start;
    private ImageButton credits;
    private ImageButton quit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        start = (ImageButton) findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               goToAccueilActivity();
            }
        });

        credits = (ImageButton) findViewById(R.id.credit);
        credits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToCreditActivity();
            }
        });

        quit = (ImageButton) findViewById(R.id.quit);
        quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    public void goToAccueilActivity(){
        Intent intent = new Intent(this, AccueilActivity.class);
        startActivity(intent);
    }

    public void goToCreditActivity(){
        //Intent intent = new Intent(this, CreditActivity.class);
        //startActivity(intent);
    }
}
