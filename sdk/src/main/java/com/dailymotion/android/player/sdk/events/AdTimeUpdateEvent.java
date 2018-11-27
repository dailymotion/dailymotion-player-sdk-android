package com.dailymotion.android.player.sdk.events;

import com.dailymotion.android.player.sdk.PlayerWebView;

public class AdTimeUpdateEvent extends PlayerEvent {
    AdTimeUpdateEvent(String payload) {
        super(PlayerWebView.EVENT_AD_TIME_UPDATE, payload);
    }
}
