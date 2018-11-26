package com.dailymotion.android.player.sdk.events;

import java.util.Map;

public class PlayerEventFactory {

    public ApiReadyEvent createApiReadyEvent(Map<String, String> params) {
        ApiReadyEvent.Visitor visitor = new ApiReadyEvent.Visitor(params.get("info[visitor][country]"),
                params.get("info[visitor][continent]"),
                params.get("info[visitor][timezone_offset]"),
                params.get("info[visitor][as_number]"),
                "true".equalsIgnoreCase(params.get("info[visitor][onsite]")),
                params.get("info[visitor][traffic_segment]"));

        ApiReadyEvent.Browser browser = new ApiReadyEvent.Browser(params.get("info[browser][ua_family]"),
                params.get("info[browser][os_family]"),
                params.get("info[browser][user_agent]"),
                params.get("info[browser][locale]"),
                params.get("info[browser][os_name]"),
                params.get("info[browser][ua_name]"),
                params.get("info[browser][flash_version]"));

        ApiReadyEvent.Consent consent = new ApiReadyEvent.Consent("true".equalsIgnoreCase(params.get("info[consent][perso]")),
                "true".equalsIgnoreCase(params.get("info[consent][storage]")),
                "true".equalsIgnoreCase(params.get("info[consent][ad]")),
                "true".equalsIgnoreCase(params.get("info[consent][audience]")),
                "true".equalsIgnoreCase(params.get("info[consent][xp]")));

        ApiReadyEvent.Player player = new ApiReadyEvent.Player(params.get("info[player][integration]"),
                params.get("info[player][env]"),
                params.get("info[player][instance_uuid]"),
                "true".equalsIgnoreCase(params.get("info[player][autoplay]")),
                params.get("info[player][type]"),
                "true".equalsIgnoreCase(params.get("info[player][secure]")),
                params.get("info[player][version]"));

        ApiReadyEvent.Device device = new ApiReadyEvent.Device(params.get("info[device][type]"));

        return new ApiReadyEvent(visitor, browser, consent, player, device);
    }

    public TimeUpdateEvent createTimeUpdateEvent(Map<String, String> params) {
        return new TimeUpdateEvent(params.get("time"));
    }
}
