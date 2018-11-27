package com.dailymotion.android.player.sdk.events;

import com.dailymotion.android.player.sdk.PlayerWebView;

public class WatchLaterRequestedEvent extends PlayerEvent {
    WatchLaterRequestedEvent(String payload) {
        super(PlayerWebView.EVENT_WATCH_LATER_REQUESTED, payload);
    }
}
