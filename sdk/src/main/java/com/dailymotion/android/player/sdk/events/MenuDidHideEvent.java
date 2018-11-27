package com.dailymotion.android.player.sdk.events;

import com.dailymotion.android.player.sdk.PlayerWebView;

public class MenuDidHideEvent extends PlayerEvent {
    MenuDidHideEvent() {
        super(PlayerWebView.EVENT_MENU_DID_HIDE);
    }
}
