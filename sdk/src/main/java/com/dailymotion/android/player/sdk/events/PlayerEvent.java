package com.dailymotion.android.player.sdk.events;

public abstract class PlayerEvent {

    private String name;

    PlayerEvent(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
