package com.dailymotion.android.player.sdk.events;

import com.dailymotion.android.player.sdk.PlayerWebView;

public class SeekedEvent extends PlayerEvent {

    private String time;

    SeekedEvent(String payload, String time) {
        super(PlayerWebView.EVENT_SEEKED, payload);
        this.time = time;
    }

    public String getTime() {
        return time;
    }
}
