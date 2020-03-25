package com.dailymotion.android.player.sdk.events

import com.dailymotion.android.player.sdk.PlayerWebView

class AdTimeUpdateEvent internal constructor(payload: String?, val time: String?) : PlayerEvent(PlayerWebView.EVENT_AD_TIME_UPDATE, payload)