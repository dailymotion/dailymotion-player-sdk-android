package com.dailymotion.android.player.sdk.events;

import com.dailymotion.android.player.sdk.PlayerWebView;

public class ControlChangeEvent extends PlayerEvent {

    private boolean controls;

    ControlChangeEvent(boolean controls) {
        super(PlayerWebView.EVENT_CONTROLSCHANGE);
        this.controls = controls;
    }

    public boolean isControls() {
        return controls;
    }
}
