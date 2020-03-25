package com.dailymotion.android.player.sdk.events

import com.dailymotion.android.player.sdk.PlayerWebView

class ControlChangeEvent internal constructor(payload: String?, val controls: Boolean) : PlayerEvent(PlayerWebView.EVENT_CONTROLSCHANGE, payload)