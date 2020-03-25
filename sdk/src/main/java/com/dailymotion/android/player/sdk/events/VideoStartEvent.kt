package com.dailymotion.android.player.sdk.events

import com.dailymotion.android.player.sdk.PlayerWebView

class VideoStartEvent internal constructor(payload: String?, val replay: String?) : PlayerEvent(PlayerWebView.EVENT_VIDEO_START, payload)