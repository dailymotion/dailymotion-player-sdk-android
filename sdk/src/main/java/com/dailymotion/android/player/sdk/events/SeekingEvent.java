package com.dailymotion.android.player.sdk.events;

import com.dailymotion.android.player.sdk.PlayerWebView;

public class SeekingEvent extends PlayerEvent {

    private String time;

    SeekingEvent(String time) {
        super(PlayerWebView.EVENT_SEEKING);
        this.time = time;
    }

    public String getTime() {
        return time;
    }
}
