package com.dailymotion.android.player.sdk.events;

import com.dailymotion.android.player.sdk.PlayerWebView;

public class FullScreenToggleRequestedEvent extends PlayerEvent {
    FullScreenToggleRequestedEvent(String payload) {
        super(PlayerWebView.EVENT_FULLSCREEN_TOGGLE_REQUESTED, payload);
    }
}
