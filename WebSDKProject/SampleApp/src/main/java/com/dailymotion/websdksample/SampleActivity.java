package com.dailymotion.websdksample;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;

import com.dailymotion.websdk.DMWebVideoView;

public class SampleActivity extends Activity {

    private DMWebVideoView mVideoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_sample);

        mVideoView = ((DMWebVideoView) findViewById(R.id.dmWebVideoView));
        //mVideoView.loadUrl("http://orange.jobs/jobs/mobi.do?do=getOffer&lang=FR&id=28866");
        mVideoView.setVideoId("x10iisk");


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
        if(mVideoView.isFullscreen()){
            mVideoView.hideVideoView();
        } else {
            super.onBackPressed();
        }
    }
}
