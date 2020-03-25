package com.dailymotion.android.player.sdk.events

import com.dailymotion.android.player.sdk.PlayerWebView

class VolumeChangeEvent internal constructor(payload: String?, val volume: String?, val isMuted: Boolean) : PlayerEvent(PlayerWebView.EVENT_VOLUMECHANGE, payload)