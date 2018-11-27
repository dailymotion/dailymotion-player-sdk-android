package com.dailymotion.android.player.sdk.events;

import com.dailymotion.android.player.sdk.PlayerWebView;

public class PlayEvent extends PlayerEvent {
    PlayEvent() {
        super(PlayerWebView.EVENT_PLAY);
    }
}
