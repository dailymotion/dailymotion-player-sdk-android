package com.dailymotion.android.player.sdk.events

import com.dailymotion.android.player.sdk.PlayerWebView

class FullScreenToggleRequestedEvent internal constructor(payload: String?) : PlayerEvent(PlayerWebView.EVENT_FULLSCREEN_TOGGLE_REQUESTED, payload)