package com.dailymotion.android.player.sdk.events

import com.dailymotion.android.player.sdk.PlayerWebView

sealed class PlayerEvent(val name: String, open val payload: String?) {

    data class GenericPlayerEvent internal constructor(override val payload: String?) : PlayerEvent("_generic_", payload)

    data class AddToCollectionRequestedEvent internal constructor(override val payload: String?) : PlayerEvent(PlayerWebView.EVENT_ADD_TO_COLLECTION_REQUESTED, payload)
    data class AdEndEvent internal constructor(override val payload: String?) : PlayerEvent(PlayerWebView.EVENT_AD_END, payload)
    data class AdPauseEvent internal constructor(override val payload: String?) : PlayerEvent(PlayerWebView.EVENT_AD_PAUSE, payload)
    data class AdPlayEvent internal constructor(override val payload: String?) : PlayerEvent(PlayerWebView.EVENT_AD_PLAY, payload)
    data class AdStartEvent internal constructor(override val payload: String?) : PlayerEvent(PlayerWebView.EVENT_AD_START, payload)
    data class AdTimeUpdateEvent internal constructor(override val payload: String?, val time: String?) : PlayerEvent(PlayerWebView.EVENT_AD_TIME_UPDATE, payload)
    data class ApiReadyEvent internal constructor(override val payload: String?) : PlayerEvent(PlayerWebView.EVENT_APIREADY, payload)
    data class ChromeCastRequestedEvent internal constructor(override val payload: String?) : PlayerEvent(PlayerWebView.EVENT_CHROME_CAST_REQUESTED, payload)
    data class ControlChangeEvent internal constructor(override val payload: String?, val controls: Boolean) : PlayerEvent(PlayerWebView.EVENT_CONTROLSCHANGE, payload)
    data class DurationChangeEvent internal constructor(override val payload: String?, val duration: String?) : PlayerEvent(PlayerWebView.EVENT_DURATION_CHANGE, payload)
    data class EndEvent internal constructor(override val payload: String?) : PlayerEvent(PlayerWebView.EVENT_END, payload)
    data class FullScreenToggleRequestedEvent internal constructor(override val payload: String?) : PlayerEvent(PlayerWebView.EVENT_FULLSCREEN_TOGGLE_REQUESTED, payload)
    data class GestureEndEvent internal constructor(override val payload: String?) : PlayerEvent(PlayerWebView.EVENT_GESTURE_END, payload)
    data class GestureStartEvent internal constructor(override val payload: String?) : PlayerEvent(PlayerWebView.EVENT_GESTURE_START, payload)
    data class LikeRequestedEvent internal constructor(override val payload: String?) : PlayerEvent(PlayerWebView.EVENT_LIKE_REQUESTED, payload)
    data class LoadedMetaDataEvent internal constructor(override val payload: String?) : PlayerEvent(PlayerWebView.EVENT_LOADEDMETADATA, payload)
    data class MenuDidHideEvent internal constructor(override val payload: String?) : PlayerEvent(PlayerWebView.EVENT_MENU_DID_HIDE, payload)
    data class MenuDidShowEvent internal constructor(override val payload: String?) : PlayerEvent(PlayerWebView.EVENT_MENU_DID_SHOW, payload)
    data class PauseEvent internal constructor(override val payload: String?) : PlayerEvent(PlayerWebView.EVENT_PAUSE, payload)
    data class PlaybackReadyEvent internal constructor(override val payload: String?) : PlayerEvent(PlayerWebView.EVENT_PLAYBACK_READY, payload)
    data class PlayEvent internal constructor(override val payload: String?) : PlayerEvent(PlayerWebView.EVENT_PLAY, payload)
    data class PlayingEvent internal constructor(override val payload: String?) : PlayerEvent(PlayerWebView.EVENT_PLAYING, payload)
    data class ProgressEvent internal constructor(override val payload: String?, val time: String?) : PlayerEvent(PlayerWebView.EVENT_PROGRESS, payload)
    data class QualitiesAvailableEvent internal constructor(override val payload: String?, val qualities: List<String>?) : PlayerEvent(PlayerWebView.EVENT_QUALITIES_AVAILABLE, payload)
    data class QualityChangeEvent internal constructor(override val payload: String?, val quality: String?) : PlayerEvent(PlayerWebView.EVENT_QUALITY_CHANGE, payload)
    data class SeekedEvent internal constructor(override val payload: String?, val time: String?) : PlayerEvent(PlayerWebView.EVENT_SEEKED, payload)
    data class SeekingEvent internal constructor(override val payload: String?, val time: String?) : PlayerEvent(PlayerWebView.EVENT_SEEKING, payload)
    data class ShareRequestedEvent internal constructor(override val payload: String?) : PlayerEvent(PlayerWebView.EVENT_SHARE_REQUESTED, payload)
    data class StartEvent internal constructor(override val payload: String?) : PlayerEvent(PlayerWebView.EVENT_START, payload)
    data class TimeUpdateEvent internal constructor(override val payload: String?, val time: String?) : PlayerEvent(PlayerWebView.EVENT_TIMEUPDATE, payload)
    data class VideoChangeEvent internal constructor(override val payload: String?) : PlayerEvent(PlayerWebView.EVENT_VIDEO_CHANGE, payload)
    data class VideoEndEvent internal constructor(override val payload: String?) : PlayerEvent(PlayerWebView.EVENT_VIDEO_END, payload)
    data class VideoStartEvent internal constructor(override val payload: String?, val replay: String?) : PlayerEvent(PlayerWebView.EVENT_VIDEO_START, payload)
    data class VolumeChangeEvent internal constructor(override val payload: String?, val volume: String?, val isMuted: Boolean) : PlayerEvent(PlayerWebView.EVENT_VOLUMECHANGE, payload)
    data class WatchLaterRequestedEvent internal constructor(override val payload: String?) : PlayerEvent(PlayerWebView.EVENT_WATCH_LATER_REQUESTED, payload)
}