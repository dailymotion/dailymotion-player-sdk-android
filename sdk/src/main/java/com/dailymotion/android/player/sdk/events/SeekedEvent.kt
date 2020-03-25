package com.dailymotion.android.player.sdk.events

import com.dailymotion.android.player.sdk.PlayerWebView

class SeekedEvent internal constructor(payload: String?, val time: String?) : PlayerEvent(PlayerWebView.EVENT_SEEKED, payload)