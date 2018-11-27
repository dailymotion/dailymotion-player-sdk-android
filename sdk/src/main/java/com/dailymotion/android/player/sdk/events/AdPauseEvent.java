package com.dailymotion.android.player.sdk.events;

import com.dailymotion.android.player.sdk.PlayerWebView;

public class AdPauseEvent extends PlayerEvent {
    AdPauseEvent(String payload) {
        super(PlayerWebView.EVENT_AD_PAUSE, payload);
    }
}
