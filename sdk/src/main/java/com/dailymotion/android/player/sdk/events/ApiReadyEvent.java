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

        public String getCountry() {
            return country;
        }

        public String getContinent() {
            return continent;
        }

        public String getTimeZoneOffset() {
            return timeZoneOffset;
        }

        public String getAsNumber() {
            return asNumber;
        }

        public boolean isOnSite() {
            return onSite;
        }

        public String getTrafficSegment() {
            return trafficSegment;
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

        public String getUaFamily() {
            return uaFamily;
        }

        public String getOsFamily() {
            return osFamily;
        }

        public String getUserAgent() {
            return userAgent;
        }

        public String getLocale() {
            return locale;
        }

        public String getOsName() {
            return osName;
        }

        public String getUaName() {
            return uaName;
        }

        public String getFlashVersion() {
            return flashVersion;
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

        public boolean isPerso() {
            return perso;
        }

        public boolean isStorage() {
            return storage;
        }

        public boolean isAd() {
            return ad;
        }

        public boolean isAudience() {
            return audience;
        }

        public boolean isXp() {
            return xp;
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

        public String getIntegration() {
            return integration;
        }

        public String getEnv() {
            return env;
        }

        public String getInstanceUUID() {
            return instanceUUID;
        }

        public boolean isAutoPlay() {
            return autoPlay;
        }

        public String getType() {
            return type;
        }

        public boolean isSecure() {
            return secure;
        }

        public String getVersion() {
            return version;
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

    ApiReadyEvent(Visitor visitor, Browser browser, Consent consent, Player player, Device device) {
        super(PlayerWebView.EVENT_APIREADY);
        this.visitor = visitor;
        this.browser = browser;
        this.consent = consent;
        this.player = player;
        this.device = device;
    }

    public Visitor getVisitor() {
        return visitor;
    }

    public Browser getBrowser() {
        return browser;
    }

    public Consent getConsent() {
        return consent;
    }

    public Player getPlayer() {
        return player;
    }

    public Device getDevice() {
        return device;
    }
}
