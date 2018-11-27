package com.dailymotion.android.player.sdk.events;

import com.dailymotion.android.player.sdk.PlayerWebView;

public class ApiReadyEvent extends PlayerEvent {

    ApiReadyEvent() {
        super(PlayerWebView.EVENT_APIREADY);
    }
}
