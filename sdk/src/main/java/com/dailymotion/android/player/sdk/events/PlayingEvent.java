package com.dailymotion.android.player.sdk.events;

import com.dailymotion.android.player.sdk.PlayerWebView;

public class PlayingEvent extends PlayerEvent {
    PlayingEvent() {
        super(PlayerWebView.EVENT_PLAYING);
    }
}
