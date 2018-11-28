package com.dailymotion.android.player.sdk.events;

import com.dailymotion.android.player.sdk.PlayerWebView;

public class ApiReadyEvent extends PlayerEvent {
    ApiReadyEvent(String payload) {
        super(PlayerWebView.EVENT_APIREADY, payload);
    }
}
