package com.example.parrouy.babedit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.audiofx.Equalizer;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;

/**
 * Created by Parrouy on 20/01/2015.
 */
public class ListenActivity extends Activity {
    private static String mFileName = null;
    private PlayButton   mPlayButton = null;
    private MediaPlayer mPlayer = new MediaPlayer();
    private Visualizer mVisualizer;
    private Equalizer mEqualizer;
    private VisualizerView mVisualizerView;
    private static final float VISUALIZER_HEIGHT_DIP = 50f;
    private LinearLayout ll;
    SeekBar seek_bar;
    Handler seekHandler = new Handler();

    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }

    private void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mFileName);
            mPlayer.prepare();
            seek_bar.setMax(mPlayer.getDuration());
            mPlayer.start();
            setupVisualizerFxAndUI();
            mVisualizer.setEnabled(true);
        } catch (IOException e) {
            Log.e("play", "prepare() failed");
        }
    }

    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
        mVisualizer.setEnabled(false);
    }

    Runnable run = new Runnable() {
        @Override
        public void run()
        {
            seekUpdation(seek_bar,mPlayer);
        }
    };

    public void seekUpdation(SeekBar seek, MediaPlayer m) {
        seek.setProgress(m.getCurrentPosition());
        seekHandler.postDelayed(run, 1000);
    }


    class PlayButton extends Button {
        boolean mStartPlaying = true;

        OnClickListener clicker = new OnClickListener() {
            public void onClick(View v) {
                onPlay(mStartPlaying);
                if (mStartPlaying) {
                    setText("Stop playing");
                } else {
                    setText("Start playing");
                }
                mStartPlaying = !mStartPlaying;
            }
        };

        public PlayButton(Context ctx) {
            super(ctx);
            setText("Start playing");
            setOnClickListener(clicker);
        }
    }

    private void setupVisualizerFxAndUI() {
        mVisualizerView = new VisualizerView(this);
       /* mVisualizerView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                (int) (VISUALIZER_HEIGHT_DIP * getResources().getDisplayMetrics().density)));
        ll.addView(mVisualizerView);*/

        RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (int) (VISUALIZER_HEIGHT_DIP * getResources().getDisplayMetrics().density));
        mVisualizerView.setLayoutParams(param);
        //ll.addView(mVisualizerView);

        // Create the Visualizer object and attach it to our media player.
        //YOU NEED android.permission.RECORD_AUDIO for that in AndroidManifest.xml
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
        seek_bar = new SeekBar(this);
        seekUpdation(seek_bar,mPlayer);

        RelativeLayout ll = new RelativeLayout(this);
        ll.setBackgroundColor(Color.rgb(52, 152, 219));

        TextView title = new TextView(this);
        title.setText("Écouter le son");
        title.setTextColor(Color.rgb(189, 195, 199));
        title.setTextSize(50);

        mPlayButton = new PlayButton(this);
        RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        param.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        mPlayButton.setLayoutParams(param);
        ll.addView(mPlayButton);

        param = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        param.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        param.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        title.setLayoutParams(param);
        ll.addView(title);

        seek_bar.setLayoutParams(param);
        ll.addView(seek_bar);
        setContentView(ll);
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
