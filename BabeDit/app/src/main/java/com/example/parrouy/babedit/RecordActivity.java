package com.example.parrouy.babedit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.parrouy.babedit.MainActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class RecordActivity extends Activity
{
    private static final String LOG_TAG = "AudioRecordTest";
    private static String mFileName = null;

    private RecordButton mRecordButton = null;
    private MediaRecorder mRecorder = null;
    private Button retour;

    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        mRecorder.start();
        //mRecorder.setOnInfoListener();
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }

    class RecordButton extends ImageButton {
        boolean mStartRecording = true;

        OnClickListener clicker = new OnClickListener() {
            public void onClick(View v) {
                onRecord(mStartRecording);
                if (mStartRecording) {
                    setBackgroundResource(R.drawable.ico_mic);
                    Toast.makeText(getApplicationContext(),"Début de l'enregistrement",Toast.LENGTH_SHORT).show();
                } else {
                    setBackgroundResource(R.drawable.ico_mic_barre);
                    goToMainActivity();
                }
                mStartRecording = !mStartRecording;
            }
        };

        public RecordButton(Context ctx) {
            super(ctx);
            //setText("Start recording");
            setBackgroundResource(R.drawable.ico_mic);
            setOnClickListener(clicker);
        }
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        File myDir = new File(Environment.getExternalStorageDirectory() +
                File.separator + "SonsBabeDit");
        Boolean success=true;
        if (!myDir.exists()) {
            success = myDir.mkdir();
        }
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/SonsBabeDit/"+message+".3gp";

        TextView title = new TextView(this);
        title.setText("Capturer un son");
        title.setTextColor(Color.rgb(189, 195, 199));
        title.setTextSize(50);

        RelativeLayout ll = new RelativeLayout(this);
        ll.setBackgroundColor(Color.rgb(52, 152, 219));
        mRecordButton = new RecordButton(this);

        //mRecordButton.setTextSize(50);

        RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        param.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        mRecordButton.setLayoutParams(param);
        ll.addView(mRecordButton);

        param = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        param.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        param.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        title.setLayoutParams(param);
        ll.addView(title);

        setContentView(ll);
    }

    public void goToMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }
    }
}