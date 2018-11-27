package com.dailymotion.android.player.sdk.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerEventFactory {

    public ApiReadyEvent createApiReadyEvent(String payload) {
        return new ApiReadyEvent(payload);
    }

    public TimeUpdateEvent createTimeUpdateEvent(String payload, Map<String, String> params) {
        return new TimeUpdateEvent(payload, params.get("time"));
    }

    public DurationChangeEvent createDurationChangeEvent(String payload, Map<String, String> params) {
        return new DurationChangeEvent(payload, params.get("duration"));
    }

    public PlayerEvent createProgressEvent(String payload, Map<String, String> params) {
        return new ProgressEvent(payload, params.get("time"));
    }

    public PlayerEvent createSeekedEvent(String payload, Map<String, String> params) {
        return new SeekedEvent(payload, params.get("time"));
    }

    public PlayerEvent createSeekingEvent(String payload, Map<String, String> params) {
        return new SeekingEvent(payload, params.get("time"));
    }

    public PlayerEvent createGestureStartEvent(String payload) {
        return new GestureStartEvent(payload);
    }

    public PlayerEvent createGestureEndEvent(String payload) {
        return new GestureEndEvent(payload);
    }

    public PlayerEvent createMenuDidShowEvent(String payload) {
        return new MenuDidShowEvent(payload);
    }

    public PlayerEvent createMenuDidHideEvent(String payload) {
        return new MenuDidHideEvent(payload);
    }

    public PlayerEvent createVideoEndEvent(String payload) {
        return new VideoEndEvent(payload);
    }

    public PlayerEvent createPlayEvent(String payload) {
        return new PlayEvent(payload);
    }

    public PlayerEvent createPauseEvent(String payload) {
        return new PauseEvent(payload);
    }

    public PlayerEvent createAdPlayEvent(String payload) {
        return new AdPlayEvent(payload);
    }

    public PlayerEvent createAdPauseEvent(String payload) {
        return new AdPauseEvent(payload);
    }

    public PlayerEvent createControlChangeEvent(String payload, Map<String, String> map) {
        return new ControlChangeEvent(payload, Boolean.parseBoolean(map.get("controls")));
    }

    public PlayerEvent createVolumeChangeEvent(String payload, Map<String, String> map) {
        return new VolumeChangeEvent(payload, map.get("volume"), Boolean.parseBoolean(map.get("muted")));
    }

    public PlayerEvent createLoadedMetaDataEvent(String payload) {
        return new LoadedMetaDataEvent(payload);
    }

    public PlayerEvent createQualityChangeEvent(String payload, Map<String, String> map) {
        return new QualityChangeEvent(payload, map.get("quality"));
    }

    public PlayerEvent createFullScreenToggleRequestedEvent(String payload) {
        return new FullScreenToggleRequestedEvent(payload);
    }

    public PlayerEvent createStartEvent(String payload) {
        return new StartEvent(payload);
    }

    public PlayerEvent createEndEvent(String payload) {
        return new EndEvent(payload);
    }

    public PlayerEvent createQualitiesAvailableEvent(String payload) {
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

    public PlayerEvent createAdTimeUpdateEvent(String payload) {
        return new AdTimeUpdateEvent(payload);
    }

    public PlayerEvent createVideoStartEvent(String payload, Map<String, String> map) {
        return new VideoStartEvent(payload, map.get("replay"));
    }

    public PlayerEvent createAdStartEvent(String payload) {
        return new AdStartEvent(payload);
    }

    public PlayerEvent createPlayingEvent(String payload) {
        return new PlayingEvent(payload);
    }

    public PlayerEvent createAdEndEvent(String payload) {
        return new AdEndEvent(payload);
    }

    public PlayerEvent createGenericPlayerEvent(String payload) {
        return new GenericPlayerEvent(payload);
    }

    public PlayerEvent createAddToCollectionRequestedEvent(String payload) {
        return new AddToCollectionRequestedEvent(payload);
    }

    public PlayerEvent createLikeRequestedEvent(String payload) {
        return new LikeRequestedEvent(payload);
    }

    public PlayerEvent createWatchLaterRequestedEvent(String payload) {
        return new WatchLaterRequestedEvent(payload);
    }

    public PlayerEvent createShareRequestedEvent(String payload) {
        return new ShareRequestedEvent(payload);
    }

    public PlayerEvent createPlaybackReadyEvent(String payload) {
        return new PlaybackReadyEvent(payload);
    }
}
