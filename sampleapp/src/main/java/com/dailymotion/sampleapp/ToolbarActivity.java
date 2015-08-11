package com.dailymotion.sampleapp;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.FrameLayout;

public class ToolbarActivity extends AppCompatActivity {
    protected Toolbar mToolbar;
    protected FrameLayout mContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toolbar);

        mToolbar = (Toolbar)findViewById(R.id.toolbar);
        mToolbar.setTitleTextColor(Color.WHITE);
        mToolbar.setTitle(getString(R.string.app_name));

        setSupportActionBar(mToolbar);

        mContainer = (FrameLayout)findViewById(R.id.container);
    }

    public Toolbar getToolbar() {
        return mToolbar;
    }
}
