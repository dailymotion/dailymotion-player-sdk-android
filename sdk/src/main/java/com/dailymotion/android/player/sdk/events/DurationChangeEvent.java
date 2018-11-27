package com.dailymotion.android.player.sdk.events;

import com.dailymotion.android.player.sdk.PlayerWebView;

public class DurationChangeEvent extends PlayerEvent {

    private String duration;

    DurationChangeEvent(String duration) {
        super(PlayerWebView.EVENT_DURATION_CHANGE);
        this.duration = duration;
    }

    public String getDuration() {
        return duration;
    }
}
