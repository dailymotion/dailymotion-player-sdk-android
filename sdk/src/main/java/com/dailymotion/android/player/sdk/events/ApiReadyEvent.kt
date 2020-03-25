package com.dailymotion.android.player.sdk.events

import com.dailymotion.android.player.sdk.PlayerWebView

class ApiReadyEvent internal constructor(payload: String?) : PlayerEvent(PlayerWebView.EVENT_APIREADY, payload)