package com.dailymotion.android.player.sdk.events

import com.dailymotion.android.player.sdk.PlayerWebView

class GestureEndEvent internal constructor(payload: String?) : PlayerEvent(PlayerWebView.EVENT_GESTURE_END, payload)