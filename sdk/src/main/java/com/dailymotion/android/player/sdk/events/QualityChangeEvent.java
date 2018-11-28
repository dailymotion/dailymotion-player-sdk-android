package com.dailymotion.android.player.sdk.events;

import com.dailymotion.android.player.sdk.PlayerWebView;

public class QualityChangeEvent extends PlayerEvent {

    private String quality;

    QualityChangeEvent(String payload, String quality) {
        super(PlayerWebView.EVENT_QUALITY_CHANGE, payload);
        this.quality = quality;
    }

    public String getQuality() {
        return quality;
    }
}
