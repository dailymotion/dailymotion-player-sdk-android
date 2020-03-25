package com.dailymotion.android.player.sdk.events

import com.dailymotion.android.player.sdk.PlayerWebView

class AdEndEvent internal constructor(payload: String?) : PlayerEvent(PlayerWebView.EVENT_AD_END, payload)