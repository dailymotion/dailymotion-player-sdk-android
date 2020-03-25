package com.dailymotion.android.player.sdk.events

import com.dailymotion.android.player.sdk.PlayerWebView

class AddToCollectionRequestedEvent internal constructor(payload: String?) : PlayerEvent(PlayerWebView.EVENT_ADD_TO_COLLECTION_REQUESTED, payload)