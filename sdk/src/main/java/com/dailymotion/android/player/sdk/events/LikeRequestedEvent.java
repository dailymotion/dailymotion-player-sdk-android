package com.dailymotion.android.player.sdk.events;

import com.dailymotion.android.player.sdk.PlayerWebView;

public class LikeRequestedEvent extends PlayerEvent {
    LikeRequestedEvent(String payload) {
        super(PlayerWebView.EVENT_LIKE_REQUESTED, payload);
    }
}
