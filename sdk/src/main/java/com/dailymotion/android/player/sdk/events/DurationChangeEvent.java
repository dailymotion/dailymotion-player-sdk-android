package com.dailymotion.android.player.sdk.events;

import com.dailymotion.android.player.sdk.PlayerWebView;

public class DurationChangeEvent extends PlayerEvent {

    private String duration;

    DurationChangeEvent(String payload, String duration) {
        super(PlayerWebView.EVENT_DURATION_CHANGE, payload);
        this.duration = duration;
    }

    public String getDuration() {
        return duration;
    }
}
