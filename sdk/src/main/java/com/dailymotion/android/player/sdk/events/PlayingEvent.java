package com.dailymotion.android.player.sdk.events;

import com.dailymotion.android.player.sdk.PlayerWebView;

public class PlayingEvent extends PlayerEvent {
    PlayingEvent(String payload) {
        super(PlayerWebView.EVENT_PLAYING, payload);
    }
}
