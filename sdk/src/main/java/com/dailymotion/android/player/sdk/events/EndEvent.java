package com.dailymotion.android.player.sdk.events;

import com.dailymotion.android.player.sdk.PlayerWebView;

public class EndEvent extends PlayerEvent {
    EndEvent() {
        super(PlayerWebView.EVENT_END);
    }
}
