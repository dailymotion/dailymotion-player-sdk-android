package com.dailymotion.android.player.sdk.events

import com.dailymotion.android.player.sdk.PlayerWebView

class AdPlayEvent internal constructor(payload: String?) : PlayerEvent(PlayerWebView.EVENT_AD_PLAY, payload)