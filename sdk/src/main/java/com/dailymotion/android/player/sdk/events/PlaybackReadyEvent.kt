package com.dailymotion.android.player.sdk.events

import com.dailymotion.android.player.sdk.PlayerWebView

class PlaybackReadyEvent internal constructor(payload: String?) : PlayerEvent(PlayerWebView.EVENT_PLAYBACK_READY, payload)