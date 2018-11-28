package com.dailymotion.android.player.sdk.events;

import com.dailymotion.android.player.sdk.PlayerWebView;

public class ControlChangeEvent extends PlayerEvent {

    private boolean controls;

    ControlChangeEvent(String payload, boolean controls) {
        super(PlayerWebView.EVENT_CONTROLSCHANGE, payload);
        this.controls = controls;
    }

    public boolean getControls() {
        return controls;
    }
}
