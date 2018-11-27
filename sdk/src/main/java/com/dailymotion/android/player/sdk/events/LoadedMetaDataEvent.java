package com.dailymotion.android.player.sdk.events;

import com.dailymotion.android.player.sdk.PlayerWebView;

public class LoadedMetaDataEvent extends PlayerEvent {
    LoadedMetaDataEvent() {
        super(PlayerWebView.EVENT_LOADEDMETADATA);
    }
}
