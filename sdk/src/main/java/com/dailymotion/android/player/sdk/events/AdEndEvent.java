package com.dailymotion.android.player.sdk.events;

import com.dailymotion.android.player.sdk.PlayerWebView;

public class AdEndEvent extends PlayerEvent {
    AdEndEvent() {
        super(PlayerWebView.EVENT_AD_END);
    }
}
