package com.dailymotion.android.player.sdk.events

import com.dailymotion.android.player.sdk.PlayerWebView

class PlayEvent internal constructor(payload: String?) : PlayerEvent(PlayerWebView.EVENT_PLAY, payload)