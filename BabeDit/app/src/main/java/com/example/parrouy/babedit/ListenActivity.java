package com.example.parrouy.babedit;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.media.MediaPlayer;
import android.media.audiofx.Equalizer;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.StringTokenizer;

/**
 * Created by Parrouy on 20/01/2015.
 */
public class ListenActivity extends Activity {
    private static String mFileName = null;
    private MediaPlayer mPlayer = new MediaPlayer();
    private Visualizer mVisualizer;
    private Equalizer mEqualizer;
    private VisualizerView mVisualizerView;
    private static final float VISUALIZER_HEIGHT_DIP = 50f;
    private LinearLayout ll;
    private LinearLayout titre_layout;
    SeekBar seek_bar;
    Handler seekHandler = new Handler();
    private ImageButton start;
    private ImageButton pause;
    private ImageButton reprendre;
    private Button test;
    int t=0;

    private void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mFileName);
            mPlayer.prepare();
            seek_bar.setProgress(0);
            seek_bar.setMax(mPlayer.getDuration());
            mPlayer.start();
        } catch (IOException e) {
            Log.e("play", "prepare() failed");
        }
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            public void onCompletion(MediaPlayer mp) {
                start.setVisibility(View.VISIBLE);
                pause.setVisibility(View.GONE);
                reprendre.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "Lecture finie", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupVisualizerFxAndUI() {
        mVisualizerView = new VisualizerView(this);
        //ll.addView(mVisualizerView);

        mVisualizer = new Visualizer(mPlayer.getAudioSessionId());
        mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
        mVisualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
            public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes,
                                              int samplingRate) {
                mVisualizerView.updateVisualizer(bytes);
            }

            public void onFftDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {
            }
        }, Visualizer.getMaxCaptureRate() / 2, true, false);
    }

    public void startPlayProgressUpdater() {
        seek_bar.setProgress(mPlayer.getCurrentPosition());

        if (mPlayer.isPlaying()) {
            Runnable notification = new Runnable() {
                public void run() {
                    startPlayProgressUpdater();
                }
            };
            seekHandler.postDelayed(notification,1000);
        }else{
            mPlayer.pause();
            seek_bar.setProgress(0);
        }
    }

    // This is event handler thumb moving event
    private void seekChange(View v){
        if(mPlayer.isPlaying()){
            SeekBar sb = (SeekBar)v;
            mPlayer.seekTo(sb.getProgress());
        }
    }

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.listenactivity);

        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/SonsBabeDit/"+message;
        Log.e("file",mFileName);

        mPlayer = new MediaPlayer();
        seek_bar = (SeekBar) findViewById(R.id.seek_bar);
        seek_bar.setMax(mPlayer.getDuration());
        seek_bar.setOnTouchListener(new View.OnTouchListener() {@Override public boolean onTouch(View v, MotionEvent event) {
            seekChange(v);
            return false; }
        });
        StringTokenizer motTitre = new StringTokenizer(message, ".3gp");
        String mot = motTitre.nextToken();
        TextView titre = (TextView) findViewById(R.id.titre_texte);
        titre.setText(titre.getText().toString()+mot);

        start = (ImageButton) findViewById(R.id.start);
        pause = (ImageButton) findViewById(R.id.pause);
        reprendre = (ImageButton) findViewById(R.id.reprendre);
        test = (Button) findViewById(R.id.test);
        test.setVisibility(View.GONE);


        start.setVisibility(View.VISIBLE);
        pause.setVisibility(View.GONE);
        reprendre.setVisibility(View.GONE);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPlaying();
                startPlayProgressUpdater();
                setupVisualizerFxAndUI();
                mVisualizer.setEnabled(true);
                start.setVisibility(View.GONE);
                pause.setVisibility(View.VISIBLE);
            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayer.pause();
                t = mPlayer.getCurrentPosition();
                pause.setVisibility(View.GONE);
                reprendre.setVisibility(View.VISIBLE);
            }
        });

        reprendre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayer.seekTo(t);
                mPlayer.start();
                startPlayProgressUpdater();
                pause.setVisibility(View.VISIBLE);
                reprendre.setVisibility(View.GONE);
            }
        });


    }

    @Override
    public void onPause() {
        super.onPause();
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }
}
