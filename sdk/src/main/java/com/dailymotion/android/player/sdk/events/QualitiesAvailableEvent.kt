package com.dailymotion.android.player.sdk.events

import com.dailymotion.android.player.sdk.PlayerWebView

class QualitiesAvailableEvent internal constructor(payload: String?, val qualities: List<String>?) : PlayerEvent(PlayerWebView.EVENT_QUALITIES_AVAILABLE, payload)