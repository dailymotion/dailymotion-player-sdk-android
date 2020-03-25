package com.dailymotion.android.player.sdk.events

import com.dailymotion.android.player.sdk.PlayerWebView

class ChromeCastRequestedEvent internal constructor(payload: String?) : PlayerEvent(PlayerWebView.EVENT_CHROME_CAST_REQUESTED, payload)