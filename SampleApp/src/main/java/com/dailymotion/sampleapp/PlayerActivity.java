package com.dailymotion.sampleapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;

import com.dailymotion.sdk.player.DMWebVideoView;
import com.dailymotion.sampleapp.R;

public class PlayerActivity extends Activity {
    public final static String EXTRA_ID = "com.dailymotion.sample.EXTRA_ID";

    private DMWebVideoView mVideoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        setContentView(R.layout.screen_sample);

        mVideoView = ((DMWebVideoView) findViewById(R.id.dmWebVideoView));
        Intent intent = getIntent();
        String id = intent.getStringExtra(EXTRA_ID);
        if (id == null) {
            id = "x26hv6c";
        }

        mVideoView.setBaseUrl(prefs.getString(getString(R.string.keyPlayerBaseUrl), DMWebVideoView.DEFAULT_PLAYER_URL));
        if (prefs.getBoolean(getString(R.string.keyForceVideoId), false)) {
            id = prefs.getString(getString(R.string.keyVideoId), "");
        }
        mVideoView.setVideoId(id);
        mVideoView.setExtraParameters(prefs.getString(getString(R.string.keyExtraParameters), ""));
        mVideoView.setAutoPlay(true);

        mVideoView.load();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_player, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_restart) {
            mVideoView.seek(0);
        }
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
