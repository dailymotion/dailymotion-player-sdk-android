package com.dailymotion.android.player.sdk.events;

import com.dailymotion.android.player.sdk.PlayerWebView;

public class TimeUpdateEvent extends PlayerEvent {

    private String time;

    TimeUpdateEvent(String payload, String time) {
        super(PlayerWebView.EVENT_TIMEUPDATE, payload);
        this.time = time;
    }

    public String getTime() {
        return time;
    }
}
