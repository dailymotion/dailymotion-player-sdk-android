package com.dailymotion.android.player.sdk.events

import com.dailymotion.android.player.sdk.PlayerWebView

class VideoChangeEvent internal constructor(payload: String?) : PlayerEvent(PlayerWebView.EVENT_VIDEO_CHANGE, payload)