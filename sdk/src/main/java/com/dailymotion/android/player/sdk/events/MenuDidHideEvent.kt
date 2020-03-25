package com.dailymotion.android.player.sdk.events

import com.dailymotion.android.player.sdk.PlayerWebView

class MenuDidHideEvent internal constructor(payload: String?) : PlayerEvent(PlayerWebView.EVENT_MENU_DID_HIDE, payload)