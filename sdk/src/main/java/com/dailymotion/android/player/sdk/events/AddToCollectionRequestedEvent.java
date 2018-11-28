package com.dailymotion.android.player.sdk.events;

import com.dailymotion.android.player.sdk.PlayerWebView;

public class AddToCollectionRequestedEvent extends PlayerEvent {
    AddToCollectionRequestedEvent(String payload) {
        super(PlayerWebView.EVENT_ADD_TO_COLLECTION_REQUESTED, payload);
    }
}
