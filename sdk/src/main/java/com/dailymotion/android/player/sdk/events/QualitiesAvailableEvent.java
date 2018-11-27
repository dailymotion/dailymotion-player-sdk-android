package com.dailymotion.android.player.sdk.events;

import com.dailymotion.android.player.sdk.PlayerWebView;

import java.util.List;

public class QualitiesAvailableEvent extends PlayerEvent {

    private List<String> qualities;

    QualitiesAvailableEvent(List<String> qualities) {
        super(PlayerWebView.EVENT_QUALITIES_AVAILABLE);
        this.qualities = qualities;
    }

    public List<String> getQualities() {
        return qualities;
    }
}
