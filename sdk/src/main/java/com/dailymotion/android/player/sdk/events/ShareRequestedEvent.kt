package com.dailymotion.android.player.sdk.events

import com.dailymotion.android.player.sdk.PlayerWebView

class ShareRequestedEvent internal constructor(payload: String?) : PlayerEvent(PlayerWebView.EVENT_SHARE_REQUESTED, payload)