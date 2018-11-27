package com.dailymotion.android.player.sdk.events;

import com.dailymotion.android.player.sdk.PlayerWebView;

public class AdTimeUpdateEvent extends PlayerEvent {
    AdTimeUpdateEvent() {
        super(PlayerWebView.EVENT_AD_TIME_UPDATE);
    }
}
