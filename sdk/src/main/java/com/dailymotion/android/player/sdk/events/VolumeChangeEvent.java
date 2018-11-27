package com.dailymotion.android.player.sdk.events;

import com.dailymotion.android.player.sdk.PlayerWebView;

public class VolumeChangeEvent extends PlayerEvent {

    private String volume;
    private boolean muted;

    VolumeChangeEvent(String payload, String volume, boolean muted) {
        super(PlayerWebView.EVENT_VOLUMECHANGE, payload);
        this.volume = volume;
        this.muted = muted;
    }

    public String getVolume() {
        return volume;
    }

    public boolean isMuted() {
        return muted;
    }
}
