package com.dailymotion.android.player.sdk.events

import com.dailymotion.android.player.sdk.PlayerWebView

class PauseEvent internal constructor(payload: String?) : PlayerEvent(PlayerWebView.EVENT_PAUSE, payload)