package com.dailymotion.android.player.sdk.events;

import com.dailymotion.android.player.sdk.PlayerWebView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PlayerEventFactory {

    public PlayerEvent createPlayerEvent(String name, Map<String, String> params, String payload) {
        PlayerEvent playerEvent;
        switch (name) {
            case PlayerWebView.EVENT_APIREADY: {
                playerEvent = createApiReadyEvent(payload);
                break;
            }
            case PlayerWebView.EVENT_START: {
                playerEvent = createStartEvent(payload);
                break;
            }
            case PlayerWebView.EVENT_END: {
                playerEvent = createEndEvent(payload);
                break;
            }
            case PlayerWebView.EVENT_PROGRESS: {
                playerEvent = createProgressEvent(payload, params);
                break;
            }
            case PlayerWebView.EVENT_TIMEUPDATE: {
                playerEvent = createTimeUpdateEvent(payload, params);
                break;
            }
            case PlayerWebView.EVENT_DURATION_CHANGE: {
                playerEvent = createDurationChangeEvent(payload, params);
                break;
            }
            case PlayerWebView.EVENT_GESTURE_START: {
                playerEvent = createGestureStartEvent(payload);
                break;
            }
            case PlayerWebView.EVENT_MENU_DID_SHOW: {
                playerEvent = createMenuDidShowEvent(payload);
                break;
            }
            case PlayerWebView.EVENT_GESTURE_END: {
                playerEvent = createGestureEndEvent(payload);
                break;
            }
            case PlayerWebView.EVENT_MENU_DID_HIDE: {
                playerEvent = createMenuDidHideEvent(payload);
                break;
            }
            case PlayerWebView.EVENT_VIDEO_END: {
                playerEvent = createVideoEndEvent(payload);
                break;
            }
            case PlayerWebView.EVENT_PLAY: {
                playerEvent = createPlayEvent(payload);
                break;
            }
            case PlayerWebView.EVENT_PAUSE: {
                playerEvent = createPauseEvent(payload);
                break;
            }
            case PlayerWebView.EVENT_AD_START: {
                playerEvent = createAdStartEvent(payload);
                break;
            }
            case PlayerWebView.EVENT_AD_PLAY: {
                playerEvent = createAdPlayEvent(payload);
                break;
            }
            case PlayerWebView.EVENT_AD_PAUSE: {
                playerEvent = createAdPauseEvent(payload);
                break;
            }
            case PlayerWebView.EVENT_AD_TIME_UPDATE: {
                playerEvent = createAdTimeUpdateEvent(payload);
                break;
            }
            case PlayerWebView.EVENT_AD_END: {
                playerEvent = createAdEndEvent(payload);
                break;
            }
            case PlayerWebView.EVENT_CONTROLSCHANGE: {
                playerEvent = createControlChangeEvent(payload, params);
                break;
            }
            case PlayerWebView.EVENT_VOLUMECHANGE: {
                playerEvent = createVolumeChangeEvent(payload, params);
                break;
            }
            case PlayerWebView.EVENT_LOADEDMETADATA: {
                playerEvent = createLoadedMetaDataEvent(payload);
                break;
            }
            case PlayerWebView.EVENT_QUALITY_CHANGE: {
                playerEvent = createQualityChangeEvent(payload, params);
                break;
            }
            case PlayerWebView.EVENT_QUALITIES_AVAILABLE: {
                playerEvent = createQualitiesAvailableEvent(payload);
                break;
            }
            case PlayerWebView.EVENT_SEEKED: {
                playerEvent = createSeekedEvent(payload, params);
                break;
            }
            case PlayerWebView.EVENT_SEEKING: {
                playerEvent = createSeekingEvent(payload, params);
                break;
            }
            case PlayerWebView.EVENT_FULLSCREEN_TOGGLE_REQUESTED: {
                playerEvent = createFullScreenToggleRequestedEvent(payload);
                break;
            }
            case PlayerWebView.EVENT_VIDEO_START: {
                playerEvent = createVideoStartEvent(payload, params);
                break;
            }
            case PlayerWebView.EVENT_PLAYING: {
                playerEvent = createPlayingEvent(payload);
                break;
            }
            case PlayerWebView.EVENT_ADD_TO_COLLECTION_REQUESTED: {
                playerEvent = createAddToCollectionRequestedEvent(payload);
                break;
            }
            case PlayerWebView.EVENT_LIKE_REQUESTED: {
                playerEvent = createLikeRequestedEvent(payload);
                break;
            }
            case PlayerWebView.EVENT_WATCH_LATER_REQUESTED: {
                playerEvent = createWatchLaterRequestedEvent(payload);
                break;
            }
            case PlayerWebView.EVENT_SHARE_REQUESTED: {
                playerEvent = createShareRequestedEvent(payload);
                break;
            }
            case PlayerWebView.EVENT_PLAYBACK_READY: {
                playerEvent = createPlaybackReadyEvent(payload);
                break;
            }
            case PlayerWebView.EVENT_CHROME_CAST_REQUESTED: {
                playerEvent = createChromeCastRequestedEvent(payload);
                break;
            }
            default:
                playerEvent = createGenericPlayerEvent(payload);
                break;
        }
        return playerEvent;
    }

    private PlayerEvent createChromeCastRequestedEvent(String payload) {
        return new ChromeCastRequestedEvent(payload);
    }

    private ApiReadyEvent createApiReadyEvent(String payload) {
        return new ApiReadyEvent(payload);
    }

    private TimeUpdateEvent createTimeUpdateEvent(String payload, Map<String, String> params) {
        return new TimeUpdateEvent(payload, params.get("time"));
    }

    private DurationChangeEvent createDurationChangeEvent(String payload, Map<String, String> params) {
        return new DurationChangeEvent(payload, params.get("duration"));
    }

    private PlayerEvent createProgressEvent(String payload, Map<String, String> params) {
        return new ProgressEvent(payload, params.get("time"));
    }

    private PlayerEvent createSeekedEvent(String payload, Map<String, String> params) {
        return new SeekedEvent(payload, params.get("time"));
    }

    private PlayerEvent createSeekingEvent(String payload, Map<String, String> params) {
        return new SeekingEvent(payload, params.get("time"));
    }

    private PlayerEvent createGestureStartEvent(String payload) {
        return new GestureStartEvent(payload);
    }

    private PlayerEvent createGestureEndEvent(String payload) {
        return new GestureEndEvent(payload);
    }

    private PlayerEvent createMenuDidShowEvent(String payload) {
        return new MenuDidShowEvent(payload);
    }

    private PlayerEvent createMenuDidHideEvent(String payload) {
        return new MenuDidHideEvent(payload);
    }

    private PlayerEvent createVideoEndEvent(String payload) {
        return new VideoEndEvent(payload);
    }

    private PlayerEvent createPlayEvent(String payload) {
        return new PlayEvent(payload);
    }

    private PlayerEvent createPauseEvent(String payload) {
        return new PauseEvent(payload);
    }

    private PlayerEvent createAdPlayEvent(String payload) {
        return new AdPlayEvent(payload);
    }

    private PlayerEvent createAdPauseEvent(String payload) {
        return new AdPauseEvent(payload);
    }

    private PlayerEvent createControlChangeEvent(String payload, Map<String, String> map) {
        return new ControlChangeEvent(payload, Boolean.parseBoolean(map.get("controls")));
    }

    private PlayerEvent createVolumeChangeEvent(String payload, Map<String, String> map) {
        return new VolumeChangeEvent(payload, map.get("volume"), Boolean.parseBoolean(map.get("muted")));
    }

    private PlayerEvent createLoadedMetaDataEvent(String payload) {
        return new LoadedMetaDataEvent(payload);
    }

    private PlayerEvent createQualityChangeEvent(String payload, Map<String, String> map) {
        return new QualityChangeEvent(payload, map.get("quality"));
    }

    private PlayerEvent createFullScreenToggleRequestedEvent(String payload) {
        return new FullScreenToggleRequestedEvent(payload);
    }

    private PlayerEvent createStartEvent(String payload) {
        return new StartEvent(payload);
    }

    private PlayerEvent createEndEvent(String payload) {
        return new EndEvent(payload);
    }

    private PlayerEvent createQualitiesAvailableEvent(String payload) {
        if (payload == null || payload.isEmpty()) {
            return new QualitiesAvailableEvent(payload, null);
        }
        String[] argList = payload.split("&");
        if (argList.length < 1) {
            return new QualitiesAvailableEvent(payload, null);
        }

        List<String> availableQualities = new ArrayList<>();
        for (String arg : argList) {
            String[] values = arg.split("=");
            if (values.length > 1 && values[0].equalsIgnoreCase("qualities[]")) {
                String value = values[1];
                availableQualities.add(value);
            }
        }
        return new QualitiesAvailableEvent(payload, availableQualities);
    }

    private PlayerEvent createAdTimeUpdateEvent(String payload) {
        return new AdTimeUpdateEvent(payload);
    }

    private PlayerEvent createVideoStartEvent(String payload, Map<String, String> map) {
        return new VideoStartEvent(payload, map.get("replay"));
    }

    private PlayerEvent createAdStartEvent(String payload) {
        return new AdStartEvent(payload);
    }

    private PlayerEvent createPlayingEvent(String payload) {
        return new PlayingEvent(payload);
    }

    private PlayerEvent createAdEndEvent(String payload) {
        return new AdEndEvent(payload);
    }

    private PlayerEvent createGenericPlayerEvent(String payload) {
        return new GenericPlayerEvent(payload);
    }

    private PlayerEvent createAddToCollectionRequestedEvent(String payload) {
        return new AddToCollectionRequestedEvent(payload);
    }

    private PlayerEvent createLikeRequestedEvent(String payload) {
        return new LikeRequestedEvent(payload);
    }

    private PlayerEvent createWatchLaterRequestedEvent(String payload) {
        return new WatchLaterRequestedEvent(payload);
    }

    private PlayerEvent createShareRequestedEvent(String payload) {
        return new ShareRequestedEvent(payload);
    }

    private PlayerEvent createPlaybackReadyEvent(String payload) {
        return new PlaybackReadyEvent(payload);
    }
}
