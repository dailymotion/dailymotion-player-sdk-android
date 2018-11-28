package com.dailymotion.android.player.sdk.events;

import com.dailymotion.android.player.sdk.PlayerWebView;

import java.util.List;

public class QualitiesAvailableEvent extends PlayerEvent {

    private List<String> qualities;

    QualitiesAvailableEvent(String payload, List<String> qualities) {
        super(PlayerWebView.EVENT_QUALITIES_AVAILABLE, payload);
        this.qualities = qualities;
    }

    public List<String> getQualities() {
        return qualities;
    }
}
