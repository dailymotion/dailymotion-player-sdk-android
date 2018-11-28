package com.dailymotion.android.player.sdk.events;

import com.dailymotion.android.player.sdk.PlayerWebView;

public class PauseEvent extends PlayerEvent {
    PauseEvent(String payload) {
        super(PlayerWebView.EVENT_PAUSE, payload);
    }
}
