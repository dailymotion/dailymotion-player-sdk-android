package com.dailymotion.android.player.sdk.events;

public abstract class PlayerEvent {

    private String name;
    private String payload;

    PlayerEvent(String name, String payload) {
        this.name = name;
        this.payload = payload;
    }

    public String getName() {
        return name;
    }

    public String getPayload() {
        return payload;
    }
}
