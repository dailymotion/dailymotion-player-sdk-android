package com.dailymotion.android.player.sampleapp;

import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dailymotion.android.player.sdk.PlayerWebView;
import com.dailymotion.websdksample.R;

import java.util.HashMap;

import timber.log.Timber;

public class SampleActivity extends AppCompatActivity implements View.OnClickListener {

    private PlayerWebView mVideoView;
    private TextView mLogText;
    private LinearLayout mActionLayout;
    private boolean mFullscreen = false;
    private Toolbar mToolbar;

    public void onFullScreenToggleRequested() {
        setFullScreenInternal(!mFullscreen);
        LinearLayout.LayoutParams params;

        if (mFullscreen) {
            mToolbar.setVisibility(View.GONE);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        } else {
            mToolbar.setVisibility(View.VISIBLE);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
            params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (215 * getResources().getDisplayMetrics().density));
        }
        mVideoView.setLayoutParams(params);
    }

    private void setFullScreenInternal(boolean fullScreen) {
        mFullscreen = fullScreen;
        if (mActionLayout != null) {
            if (mFullscreen) {
                mActionLayout.setVisibility(View.GONE);
            } else {
                mActionLayout.setVisibility(View.VISIBLE);
            }
        }

        mVideoView.setFullscreenButton(mFullscreen);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Timber.plant(new Timber.DebugTree());
        setContentView(R.layout.new_screen_sample);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        if (mToolbar != null) {
            mToolbar.setVisibility(View.VISIBLE);
            mToolbar.setBackgroundColor(getResources().getColor(android.R.color.background_dark));
            mToolbar.setTitleTextColor(getResources().getColor(android.R.color.white));

            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(getString(R.string.app_name));
            }
        }

        mActionLayout = (LinearLayout) findViewById(R.id.action_layout);
        mVideoView = (PlayerWebView) findViewById(R.id.dm_player_web_view);

        if ((getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0) {
            mVideoView.setIsWebContentsDebuggingEnabled(true);
        }

        mVideoView.load("x26hv6c");

        mVideoView.setEventListener(new PlayerWebView.EventListener() {
            @Override
            public void onEvent(String event, HashMap<String, String> map) {
                switch (event) {
                    case "apiready":
                        log("apiready");
                        break;
                    case "start":
                        log("start");
                        break;
                    case "loadedmetadata":
                        log("loadedmetadata");
                        break;
                    case "progress":
                        log(event + " (bufferedTime: " + mVideoView.getBufferedTime() + ")");
                        break;
                    case "durationchange":
                        log(event + " (duration: " + mVideoView.getDuration() + ")");
                        break;
                    case "timeupdate":
                    case "ad_timeupdate":
                    case "seeking":
                    case "seeked":
                        log(event + " (currentTime: " + mVideoView.getPosition() + ")");
                        break;
                    case "video_start":
                    case "ad_start":
                    case "ad_play":
                    case "playing":
                    case "end":
                        log(event + " (ended: " + mVideoView.isEnded() + ")");
                        break;
                    case "ad_pause":
                    case "ad_end":
                    case "video_end":
                    case "play":
                    case "pause":
                        log(event + " (paused: " + mVideoView.getVideoPaused() + ")");
                        break;
                    case "qualitychange":
                        log(event + " (quality: " + mVideoView.getQuality() + ")");
                        break;
                    case PlayerWebView.EVENT_FULLSCREEN_TOGGLE_REQUESTED:
                        onFullScreenToggleRequested();
                        break;
                    default:
                        break;
                }
            }
        });

        Button playButton = ((Button) findViewById(R.id.btnTogglePlay));
        playButton.setOnClickListener(SampleActivity.this);
        Button togglePlayButton = ((Button) findViewById(R.id.btnPlay));
        togglePlayButton.setOnClickListener(SampleActivity.this);
        Button pauseButton = ((Button) findViewById(R.id.btnPause));
        pauseButton.setOnClickListener(SampleActivity.this);

        Button seekButton = ((Button) findViewById(R.id.btnSeek));
        seekButton.setOnClickListener(SampleActivity.this);
        Button loadVideoButton = ((Button) findViewById(R.id.btnLoadVideo));
        loadVideoButton.setOnClickListener(SampleActivity.this);
        Button setQualityButton = ((Button) findViewById(R.id.btnSetQuality));
        setQualityButton.setOnClickListener(SampleActivity.this);
        Button setSubtitleButton = ((Button) findViewById(R.id.btnSetSubtitle));
        setSubtitleButton.setOnClickListener(SampleActivity.this);

        Button toggleControlsButton = ((Button) findViewById(R.id.btnToggleControls));
        toggleControlsButton.setOnClickListener(SampleActivity.this);
        Button showControlsButton = ((Button) findViewById(R.id.btnShowControls));
        showControlsButton.setOnClickListener(SampleActivity.this);
        Button hideControlsButton = ((Button) findViewById(R.id.btnHideControls));
        hideControlsButton.setOnClickListener(SampleActivity.this);

        mLogText = ((TextView) findViewById(R.id.logText));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnPlay) {
            mVideoView.play();
        } else if (v.getId() == R.id.btnTogglePlay) {
            mVideoView.togglePlay();
        } else if (v.getId() == R.id.btnPause) {
            mVideoView.pause();
        } else if (v.getId() == R.id.btnSeek) {
            mVideoView.seek(30);
        } else if (v.getId() == R.id.btnLoadVideo) {
            mVideoView.load("x19b6ui");
        } else if (v.getId() == R.id.btnSetQuality) {
            mVideoView.setQuality("240");
        } else if (v.getId() == R.id.btnSetSubtitle) {
            mVideoView.setSubtitle("en");
        } else if (v.getId() == R.id.btnToggleControls) {
            mVideoView.toggleControls();
        } else if (v.getId() == R.id.btnShowControls) {
            mVideoView.showControls(true);
        } else if (v.getId() == R.id.btnHideControls) {
            mVideoView.showControls(false);
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
        mVideoView.goBack();
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
        if (mActionLayout == null || mActionLayout.getVisibility() == View.GONE) {
            return;
        }

        mLogText.append("\n" + text);
        final int scroll = mLogText.getLayout().getLineTop(mLogText.getLineCount()) - mLogText.getHeight();
        if (scroll > 0) {
            mLogText.scrollTo(0, scroll);
        } else {
            mLogText.scrollTo(0, 0);
        }
    }
}
