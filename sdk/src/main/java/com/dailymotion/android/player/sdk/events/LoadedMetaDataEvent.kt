package com.dailymotion.android.player.sdk.events

import com.dailymotion.android.player.sdk.PlayerWebView

class LoadedMetaDataEvent internal constructor(payload: String?) : PlayerEvent(PlayerWebView.EVENT_LOADEDMETADATA, payload)