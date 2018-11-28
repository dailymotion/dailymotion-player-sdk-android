package com.dailymotion.android.player.sdk.events;

import com.dailymotion.android.player.sdk.PlayerWebView;

public class GestureEndEvent extends PlayerEvent {
    GestureEndEvent(String payload) {
        super(PlayerWebView.EVENT_GESTURE_END, payload);
    }
}
