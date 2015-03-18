package com.dailymotion.sample;

import android.app.Application;

import com.dailymotion.sdk.api.ApiRequest;
import com.dailymotion.sdk.httprequest.RequestQueue;
import com.dailymotion.sdk.util.DMLog;

public class SampleApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        RequestQueue.init(this);
        DMLog.setEnabled(true);
        ApiRequest.setClientType("dailymotionSdkAndroid");
    }
}
