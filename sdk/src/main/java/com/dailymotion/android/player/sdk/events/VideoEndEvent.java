package com.dailymotion.android.player.sdk.events;

import com.dailymotion.android.player.sdk.PlayerWebView;

public class VideoEndEvent extends PlayerEvent {
    VideoEndEvent() {
        super(PlayerWebView.EVENT_VIDEO_END);
    }
}
