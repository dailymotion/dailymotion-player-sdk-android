package com.dailymotion.android.player.sdk.events;

import com.dailymotion.android.player.sdk.PlayerWebView;

public class StartEvent extends PlayerEvent {
    StartEvent() {
        super(PlayerWebView.EVENT_START);
    }
}
