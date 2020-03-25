package com.dailymotion.android.player.sdk.events

import com.dailymotion.android.player.sdk.PlayerWebView

class LikeRequestedEvent internal constructor(payload: String?) : PlayerEvent(PlayerWebView.EVENT_LIKE_REQUESTED, payload)