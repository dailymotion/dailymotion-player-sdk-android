package com.dailymotion.android.player.sdk.events;

import com.dailymotion.android.player.sdk.PlayerWebView;

public class AdStartEvent extends PlayerEvent {
    AdStartEvent() {
        super(PlayerWebView.EVENT_AD_START);
    }
}
