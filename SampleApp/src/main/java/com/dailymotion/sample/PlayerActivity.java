package com.dailymotion.sample;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;

import com.dailymotion.sdk.player.DMWebVideoView;
import com.dailymotion.websdksample.R;

public class PlayerActivity extends Activity {
    public final static String EXTRA_ID = "com.dailymotion.sample.EXTRA_ID";

    private DMWebVideoView mVideoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_sample);

        mVideoView = ((DMWebVideoView) findViewById(R.id.dmWebVideoView));
        Intent intent = getIntent();
        String id = intent.getStringExtra(EXTRA_ID);
        if (id == null) {
            id = "x2frsoi";
        }
        mVideoView.setVideoId(id, false);
        // Uncomment if you need autoplay
        // mVideoView.setAutoPlay(true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
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
}
