package com.dailymotion.android.player.sdk.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerEventFactory {

    public ApiReadyEvent createApiReadyEvent() {
        return new ApiReadyEvent();
    }

    public TimeUpdateEvent createTimeUpdateEvent(Map<String, String> params) {
        return new TimeUpdateEvent(params.get("time"));
    }

    public DurationChangeEvent createDurationChangeEvent(Map<String, String> params) {
        return new DurationChangeEvent(params.get("duration"));
    }

    public PlayerEvent createProgressEvent(HashMap<String, String> params) {
        return new ProgressEvent(params.get("time"));
    }

    public PlayerEvent createSeekedEvent(HashMap<String, String> params) {
        return new SeekedEvent(params.get("time"));
    }

    public PlayerEvent createSeekingEvent(HashMap<String, String> params) {
        return new SeekingEvent(params.get("time"));
    }

    public PlayerEvent createGestureStartEvent() {
        return new GestureStartEvent();
    }

    public PlayerEvent createGestureEndEvent() {
        return new GestureEndEvent();
    }

    public PlayerEvent createMenuDidShowEvent() {
        return new MenuDidShowEvent();
    }

    public PlayerEvent createMenuDidHideEvent() {
        return new MenuDidHideEvent();
    }

    public PlayerEvent createVideoEndEvent() {
        return new VideoEndEvent();
    }

    public PlayerEvent createPlayEvent() {
        return new PlayEvent();
    }

    public PlayerEvent createPauseEvent() {
        return new PauseEvent();
    }

    public PlayerEvent createAdPlayEvent() {
        return new AdPlayEvent();
    }

    public PlayerEvent createAdPauseEvent() {
        return new AdPauseEvent();
    }

    public PlayerEvent createControlChangeEvent(HashMap<String, String> map) {
        return new ControlChangeEvent(Boolean.parseBoolean(map.get("controls")));
    }

    public PlayerEvent createVolumeChangeEvent(HashMap<String, String> map) {
        return new VolumeChangeEvent(map.get("volume"), Boolean.parseBoolean(map.get("muted")));
    }

    public PlayerEvent createLoadedMetaDataEvent() {
        return new LoadedMetaDataEvent();
    }

    public PlayerEvent createQualityChangeEvent(HashMap<String, String> map) {
        return new QualityChangeEvent(map.get("quality"));
    }

    public PlayerEvent createFullScreenToggleRequestedEvent() {
        return new FullScreenToggleRequestedEvent();
    }

    public PlayerEvent createStartEvent() {
        return new StartEvent();
    }

    public PlayerEvent createEndEvent() {
        return new EndEvent();
    }

    public PlayerEvent createQualitiesAvailableEvent(String query) {
        if (query == null || query.isEmpty()) {
            return new QualitiesAvailableEvent(null);
        }
        String[] argList = query.split("&");
        if (argList.length < 1) {
            return new QualitiesAvailableEvent(null);
        }

        List<String> availableQualities = new ArrayList<>();
        for (String arg : argList) {
            String[] values = arg.split("=");
            if (values.length > 1 && values[0].equalsIgnoreCase("qualities[]")) {
                String value = values[1];
                availableQualities.add(value);
            }
        }
        return new QualitiesAvailableEvent(availableQualities);
    }

    public PlayerEvent createAdTimeUpdateEvent() {
        return new AdTimeUpdateEvent();
    }

    public PlayerEvent createVideoStartEvent(HashMap<String, String> map) {
        return new VideoStartEvent(map.get("replay"));
    }

    public PlayerEvent createAdStartEvent() {
        return new AdStartEvent();
    }

    public PlayerEvent createPlayingEvent() {
        return new PlayingEvent();
    }

    public PlayerEvent createAdEndEvent() {
        return new AdEndEvent();
    }
}
