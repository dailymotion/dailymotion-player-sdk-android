package com.dailymotion.android.player.sdk.events;

import com.dailymotion.android.player.sdk.PlayerWebView;

public class ProgressEvent extends PlayerEvent {

    private String time;

    ProgressEvent(String payload, String time) {
        super(PlayerWebView.EVENT_PROGRESS, payload);
        this.time = time;
    }

    public String getTime() {
        return time;
    }
}
