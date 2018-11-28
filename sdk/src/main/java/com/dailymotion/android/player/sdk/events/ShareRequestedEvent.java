package com.dailymotion.android.player.sdk.events;

import com.dailymotion.android.player.sdk.PlayerWebView;

public class ShareRequestedEvent extends PlayerEvent {
    ShareRequestedEvent(String payload) {
        super(PlayerWebView.EVENT_SHARE_REQUESTED, payload);
    }
}
