package com.dailymotion.sampleapp;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;

import com.dailymotion.sdk.api.model.Video;
import com.dailymotion.sdk.player.DMWebVideoView;

public class ScreenPlayer extends Screen {
    private final Video mVideo;
    private DMWebVideoView mVideoView;

    public ScreenPlayer(MainActivity activity, Video video) {
        super(activity);
        mVideo = video;
    }

    @Override
    protected View onCreateView() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mActivity);

        mVideoView = new DMWebVideoView(mActivity);

        mVideoView.setBaseUrl(prefs.getString(mActivity.getString(R.string.keyPlayerBaseUrl), DMWebVideoView.DEFAULT_PLAYER_URL));
        String id = mVideo.id;
        if (prefs.getBoolean(mActivity.getString(R.string.keyForceVideoId), false)) {
            id = prefs.getString(mActivity.getString(R.string.keyVideoId), "");
        }
        mVideoView.setVideoId(id);
        mVideoView.setExtraParameters(prefs.getString(mActivity.getString(R.string.keyExtraParameters), ""));
        mVideoView.setAutoPlay(true);

        mVideoView.load();

        return mVideoView;
    }
}
