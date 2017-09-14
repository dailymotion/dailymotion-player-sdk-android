package com.dailymotion.android.player.sampleapp;

import android.app.Application;

import com.dailymotion.websdksample.BuildConfig;

import timber.log.Timber;

/**
 * Created by hugo
 * on 14/09/2017.
 */

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }
}
