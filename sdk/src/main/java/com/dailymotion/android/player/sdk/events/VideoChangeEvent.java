package com.dailymotion.android.player.sdk.events;

import com.dailymotion.android.player.sdk.PlayerWebView;

public class VideoChangeEvent extends PlayerEvent {
    VideoChangeEvent(String payload) {
        super(PlayerWebView.EVENT_VIDEO_CHANGE, payload);
    }
}
