package com.dailymotion.android.player.sdk.events;

import com.dailymotion.android.player.sdk.PlayerWebView;

public class QualityChangeEvent extends PlayerEvent {

    private String quality;

    QualityChangeEvent(String quality) {
        super(PlayerWebView.EVENT_QUALITY_CHANGE);
        this.quality = quality;
    }

    public String getQuality() {
        return quality;
    }
}
