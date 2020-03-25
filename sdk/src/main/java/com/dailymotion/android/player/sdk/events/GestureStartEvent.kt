package com.dailymotion.android.player.sdk.events

import com.dailymotion.android.player.sdk.PlayerWebView

class GestureStartEvent internal constructor(payload: String?) : PlayerEvent(PlayerWebView.EVENT_GESTURE_START, payload)