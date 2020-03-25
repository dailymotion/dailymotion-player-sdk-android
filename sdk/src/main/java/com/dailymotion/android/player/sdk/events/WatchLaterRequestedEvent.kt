package com.dailymotion.android.player.sdk.events

import com.dailymotion.android.player.sdk.PlayerWebView

class WatchLaterRequestedEvent internal constructor(payload: String?) : PlayerEvent(PlayerWebView.EVENT_WATCH_LATER_REQUESTED, payload)