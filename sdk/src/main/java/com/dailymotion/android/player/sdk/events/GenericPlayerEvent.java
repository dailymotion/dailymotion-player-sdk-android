package com.dailymotion.android.player.sdk.events;

public class GenericPlayerEvent extends PlayerEvent {
    GenericPlayerEvent(String payload) {
        super("_generic_", payload);
    }
}
