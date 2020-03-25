package com.dailymotion.android.player.sdk.events

import com.dailymotion.android.player.sdk.PlayerWebView

class PlayingEvent internal constructor(payload: String?) : PlayerEvent(PlayerWebView.EVENT_PLAYING, payload)