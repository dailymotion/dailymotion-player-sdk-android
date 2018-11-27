package com.dailymotion.android.player.sdk.events;

import com.dailymotion.android.player.sdk.PlayerWebView;

public class GestureStartEvent extends PlayerEvent {
    GestureStartEvent() {
        super(PlayerWebView.EVENT_GESTURE_START);
    }
}
