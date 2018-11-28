package com.dailymotion.android.player.sdk.events;

import com.dailymotion.android.player.sdk.PlayerWebView;

public class PlayEvent extends PlayerEvent {
    PlayEvent(String payload) {
        super(PlayerWebView.EVENT_PLAY, payload);
    }
}
