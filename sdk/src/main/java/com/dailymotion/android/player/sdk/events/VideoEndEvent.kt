package com.dailymotion.android.player.sdk.events

import com.dailymotion.android.player.sdk.PlayerWebView

class VideoEndEvent internal constructor(payload: String?) : PlayerEvent(PlayerWebView.EVENT_VIDEO_END, payload)