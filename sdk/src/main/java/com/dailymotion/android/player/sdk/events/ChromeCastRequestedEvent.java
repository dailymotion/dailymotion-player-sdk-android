package com.dailymotion.android.player.sdk.events;

import com.dailymotion.android.player.sdk.PlayerWebView;

public class ChromeCastRequestedEvent extends PlayerEvent {
    ChromeCastRequestedEvent(String payload) {
        super(PlayerWebView.EVENT_CHROME_CAST_REQUESTED, payload);
    }
}
