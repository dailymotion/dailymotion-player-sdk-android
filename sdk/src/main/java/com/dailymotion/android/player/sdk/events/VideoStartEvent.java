package com.dailymotion.android.player.sdk.events;

import com.dailymotion.android.player.sdk.PlayerWebView;

public class VideoStartEvent extends PlayerEvent {

    private String replay;

    VideoStartEvent(String replay) {
        super(PlayerWebView.EVENT_VIDEO_START);
        this.replay = replay;
    }

    public String getReplay() {
        return replay;
    }
}
