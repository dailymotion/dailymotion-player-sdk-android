package com.dailymotion.android.player.sdk.events

import com.dailymotion.android.player.sdk.PlayerWebView

class QualityChangeEvent internal constructor(payload: String?, val quality: String?) : PlayerEvent(PlayerWebView.EVENT_QUALITY_CHANGE, payload)