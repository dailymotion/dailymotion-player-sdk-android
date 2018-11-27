package com.dailymotion.android.player.sdk.events;

import com.dailymotion.android.player.sdk.PlayerWebView;

public class PlaybackReadyEvent extends PlayerEvent {
    PlaybackReadyEvent(String payload) {
        super(PlayerWebView.EVENT_PLAYBACK_READY, payload);
    }
}
