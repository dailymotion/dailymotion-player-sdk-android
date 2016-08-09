package com.dailymotion.websdksample;

import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.dailymotion.websdk.DMWebVideoView;

public class SampleActivity extends Activity implements View.OnClickListener {

    private DMWebVideoView mVideoView;

    private Button playButton;
    private Button togglePlayButton;
    private Button pauseButton;

    private Button seekButton;
    private Button loadVideoButton;
    private Button setQualityButton;
    private Button setSubtitleButton;

    private Button toggleControlsButton;
    private Button showControlsButton;
    private Button hideControlsButton;

    private TextView logText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_sample);

        mVideoView = ((DMWebVideoView) findViewById(R.id.dmWebVideoView));
        mVideoView.setVideoId("x26hv6c");
        mVideoView.setAutoPlay(true);
        mVideoView.load();

        mVideoView.setEventListener(new DMWebVideoView.Listener() {
            @Override
            public void onEvent(String event) {
                switch (event)
                {
                    case "apiready": log("apiready"); break;
                    case "start": log("start"); break;
                    case "loadedmetadata": log("loadedmetadata"); break;
                    case "progress": log(event + " (bufferedTime: " + mVideoView.bufferedTime + ")"); break;
                    case "durationchange": log(event + " (duration: " + mVideoView.duration + ")"); break;
                    case "timeupdate":
                    case "ad_timeupdate":
                    case "seeking":
                    case "seeked": log(event + " (currentTime: " + mVideoView.currentTime + ")"); break;
                    case "video_start":
                    case "ad_start":
                    case "ad_play":
                    case "playing":
                    case "end": log(event + " (ended: " + mVideoView.ended + ")"); break;
                    case "ad_pause":
                    case "ad_end":
                    case "video_end":
                    case "play":
                    case "fullscreenchange": log(event + " (fullscreen: " + mVideoView.fullscreen + ")"); break;
                    case "pause": log(event + " (paused: " + mVideoView.paused + ")"); break;
                    case "error": log(event + " (error: " + mVideoView.error.toString() + ")"); break;
                    case "rebuffer": log(event + " (rebuffering: " + mVideoView.rebuffering + ")"); break;
                    case "qualitiesavailable": log(event + " (qualities: " + mVideoView.qualities + ")"); break;
                    case "qualitychange": log(event + " (quality: " + mVideoView.quality + ")"); break;
                    case "subtitlesavailable": log(event + " (subtitles: " + mVideoView.subtitles + ")"); break;
                    case "subtitlechange": log(event + " (subtitle: " + mVideoView.subtitle + ")"); break;
                }
            }
        });

        playButton = ((Button) findViewById(R.id.btnTogglePlay));
        playButton.setOnClickListener(SampleActivity.this);
        togglePlayButton = ((Button) findViewById(R.id.btnPlay));
        togglePlayButton.setOnClickListener(SampleActivity.this);
        pauseButton = ((Button) findViewById(R.id.btnPause));
        pauseButton.setOnClickListener(SampleActivity.this);

        seekButton = ((Button) findViewById(R.id.btnSeek));
        seekButton.setOnClickListener(SampleActivity.this);
        loadVideoButton = ((Button) findViewById(R.id.btnLoadVideo));
        loadVideoButton.setOnClickListener(SampleActivity.this);
        setQualityButton = ((Button) findViewById(R.id.btnSetQuality));
        setQualityButton.setOnClickListener(SampleActivity.this);
        setSubtitleButton = ((Button) findViewById(R.id.btnSetSubtitle));
        setSubtitleButton.setOnClickListener(SampleActivity.this);

        toggleControlsButton = ((Button) findViewById(R.id.btnToggleControls));
        toggleControlsButton.setOnClickListener(SampleActivity.this);
        showControlsButton = ((Button) findViewById(R.id.btnShowControls));
        showControlsButton.setOnClickListener(SampleActivity.this);
        hideControlsButton = ((Button) findViewById(R.id.btnHideControls));
        hideControlsButton.setOnClickListener(SampleActivity.this);

        logText = ((TextView) findViewById(R.id.logText));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnPlay) {
            mVideoView.play();
        }
        else if (v.getId() == R.id.btnTogglePlay) {
            mVideoView.togglePlay();
        }
        else if (v.getId() == R.id.btnPause) {
            mVideoView.pause();
        }
        else if (v.getId() == R.id.btnSeek) {
            mVideoView.seek(30);
        }
        else if (v.getId() == R.id.btnLoadVideo) {
            mVideoView.setVideoId("x19b6ui");
            mVideoView.load();
        }
        else if (v.getId() == R.id.btnSetQuality) {
            mVideoView.setQuality("240");
        }
        else if (v.getId() == R.id.btnSetSubtitle) {
            mVideoView.setSubtitle("en");
        }
        else if (v.getId() == R.id.btnToggleControls) {
            mVideoView.toggleControls();
        }
        else if (v.getId() == R.id.btnShowControls) {
            mVideoView.setControls(true);
        }
        else if (v.getId() == R.id.btnHideControls) {
            mVideoView.setControls(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sample, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        mVideoView.handleBackPress(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mVideoView.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mVideoView.onResume();
        }
    }

    private void log(String text) {
        logText.append("\n" + text);
        final int scroll = logText.getLayout().getLineTop(logText.getLineCount()) - logText.getHeight();
        if (scroll > 0) {
            logText.scrollTo(0, scroll);
        }
        else {
            logText.scrollTo(0, 0);
        }
    }
}
