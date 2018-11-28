package com.dailymotion.android.player.sdk.events;

import com.dailymotion.android.player.sdk.PlayerWebView;

public class VideoStartEvent extends PlayerEvent {

    private String replay;

    VideoStartEvent(String payload, String replay) {
        super(PlayerWebView.EVENT_VIDEO_START, payload);
        this.replay = replay;
    }

    public String getReplay() {
        return replay;
    }
}
