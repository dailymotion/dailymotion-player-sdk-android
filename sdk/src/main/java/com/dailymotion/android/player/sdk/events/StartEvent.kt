package com.dailymotion.android.player.sdk.events

import com.dailymotion.android.player.sdk.PlayerWebView

class StartEvent internal constructor(payload: String?) : PlayerEvent(PlayerWebView.EVENT_START, payload)