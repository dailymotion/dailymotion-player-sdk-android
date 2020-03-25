package com.dailymotion.android.player.sdk.events

import com.dailymotion.android.player.sdk.PlayerWebView

class EndEvent internal constructor(payload: String?) : PlayerEvent(PlayerWebView.EVENT_END, payload)