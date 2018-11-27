package com.dailymotion.android.player.sdk.events;

import com.dailymotion.android.player.sdk.PlayerWebView;

public class SeekingEvent extends PlayerEvent {

    private String time;

    SeekingEvent(String payload, String time) {
        super(PlayerWebView.EVENT_SEEKING, payload);
        this.time = time;
    }

    public String getTime() {
        return time;
    }
}
