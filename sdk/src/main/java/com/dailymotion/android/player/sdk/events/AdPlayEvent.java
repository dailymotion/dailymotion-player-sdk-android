package com.dailymotion.android.player.sdk.events;

import com.dailymotion.android.player.sdk.PlayerWebView;

public class AdPlayEvent extends PlayerEvent {
    AdPlayEvent() {
        super(PlayerWebView.EVENT_AD_PLAY);
    }
}
