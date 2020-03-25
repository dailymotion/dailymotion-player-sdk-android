package com.dailymotion.android.player.sdk.events

import com.dailymotion.android.player.sdk.PlayerWebView

class DurationChangeEvent internal constructor(payload: String?, val duration: String?) : PlayerEvent(PlayerWebView.EVENT_DURATION_CHANGE, payload)