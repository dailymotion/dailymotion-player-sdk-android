package com.dailymotion.android.player.sdk.events;

import com.dailymotion.android.player.sdk.PlayerWebView;

public class ErrorEvent extends PlayerEvent {
    private String errorCode;
    private String message;
    private String title;

    ErrorEvent(String payload, String errorCode, String message, String title) {
        super(PlayerWebView.EVENT_ERROR, payload);
        this.errorCode = errorCode;
        this.message = message;
        this.title = title;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return message;
    }

    public String getTitle() {
        return title;
    }
}
