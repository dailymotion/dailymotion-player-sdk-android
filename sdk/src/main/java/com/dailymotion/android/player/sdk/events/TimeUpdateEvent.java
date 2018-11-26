package com.dailymotion.android.player.sdk.events;

import com.dailymotion.android.player.sdk.PlayerWebView;

public class TimeUpdateEvent extends PlayerEvent {

    private String time;

    public TimeUpdateEvent(String time) {
        super(PlayerWebView.EVENT_TIMEUPDATE);
        this.time = time;
    }
}
