package com.dailymotion.sample;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

import com.dailymotion.websdksample.R;

public class AboutActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.about_activity);

        WebView webView = (WebView)findViewById(R.id.webView);
        webView.loadUrl("file:///android_asset/about.html");
    }
}
