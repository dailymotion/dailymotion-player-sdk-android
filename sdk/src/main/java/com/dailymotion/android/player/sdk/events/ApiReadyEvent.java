package com.dailymotion.android.player.sdk.events;

import com.dailymotion.android.player.sdk.PlayerWebView;

public class ApiReadyEvent extends PlayerEvent {

    static class Visitor {
        private String country;
        private String continent;
        private String timeZoneOffset;
        private String asNumber;
        private boolean onSite;
        private String trafficSegment;

        Visitor(String country, String continent, String timeZoneOffset, String asNumber, boolean onSite, String trafficSegment) {
            this.country = country;
            this.continent = continent;
            this.timeZoneOffset = timeZoneOffset;
            this.asNumber = asNumber;
            this.onSite = onSite;
            this.trafficSegment = trafficSegment;
        }
    }

    static class Browser {
        private String uaFamily;
        private String osFamily;
        private String userAgent;
        private String locale;
        private String osName;
        private String uaName;
        private String flashVersion;

        Browser(String uaFamily, String osFamily, String userAgent, String locale, String osName, String uaName, String flashVersion) {
            this.uaFamily = uaFamily;
            this.osFamily = osFamily;
            this.userAgent = userAgent;
            this.locale = locale;
            this.osName = osName;
            this.uaName = uaName;
            this.flashVersion = flashVersion;
        }
    }

    static class Consent {
        private boolean perso;
        private boolean storage;
        private boolean ad;
        private boolean audience;
        private boolean xp;

        Consent(boolean perso, boolean storage, boolean ad, boolean audience, boolean xp) {
            this.perso = perso;
            this.storage = storage;
            this.ad = ad;
            this.audience = audience;
            this.xp = xp;
        }
    }

    static class Player {
        private String integration;
        private String env;
        private String instanceUUID;
        private boolean autoPlay;
        private String type;
        private boolean secure;
        private String version;

        Player(String integration, String env, String instanceUUID, boolean autoPlay, String type, boolean secure, String version) {
            this.integration = integration;
            this.env = env;
            this.instanceUUID = instanceUUID;
            this.autoPlay = autoPlay;
            this.type = type;
            this.secure = secure;
            this.version = version;
        }
    }

    static class Device {
        private String type;

        Device(String type) {
            this.type = type;
        }
    }

    private Visitor visitor;
    private Browser browser;
    private Consent consent;
    private Player player;
    private Device device;

    public ApiReadyEvent(Visitor visitor, Browser browser, Consent consent, Player player, Device device) {
        super(PlayerWebView.EVENT_APIREADY);
        this.visitor = visitor;
        this.browser = browser;
        this.consent = consent;
        this.player = player;
        this.device = device;
    }
}
