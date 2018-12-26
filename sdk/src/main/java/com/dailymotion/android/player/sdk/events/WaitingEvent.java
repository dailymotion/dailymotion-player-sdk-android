package com.dailymotion.android.player.sdk.events;

import com.dailymotion.android.player.sdk.PlayerWebView;

public class WaitingEvent extends PlayerEvent {
    WaitingEvent(String payload) {
        super(PlayerWebView.EVENT_WAITING, payload);
    }
}
