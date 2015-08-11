package com.dailymotion.sampleapp;

import android.view.View;

public abstract class Screen {
    private View mView;
    protected MainActivity mActivity;

    public Screen(MainActivity activity) {
        mActivity = activity;
    }

    public void onStart() {};
    public void onStop() {};
    public void onDestroy() {};

    protected abstract View onCreateView();

    public View getView() {
        if (mView == null) {
            mView = onCreateView();
        }

        return mView;
    }
}
