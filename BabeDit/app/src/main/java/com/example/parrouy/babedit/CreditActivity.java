package com.example.parrouy.babedit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

/**
 * Created by Parrouy on 22/01/2015.
 */
public class CreditActivity extends Activity {
    private ImageButton retour;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.creditactivity);

        retour = (ImageButton) findViewById(R.id.retour);
        retour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMainActivity();
                finish();
            }
        });
    }

    public void goToMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
