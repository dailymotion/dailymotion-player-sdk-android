package com.dailymotion.android.player.sdk.events;

import com.dailymotion.android.player.sdk.PlayerWebView;

public class MenuDidShowEvent extends PlayerEvent {
    MenuDidShowEvent(String payload) {
        super(PlayerWebView.EVENT_MENU_DID_SHOW, payload);
    }
}
