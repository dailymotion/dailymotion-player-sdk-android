package com.dailymotion.sdk.api;

import com.dailymotion.sdk.httprequest.JsonRequest;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Base class for all api request, that is requests that:
 *      - have a json response
 *      - a family filter parameter
 *      - potentially an OAuth token
 *      - ...
 */
public class ApiRequest<T> extends JsonRequest<T> {
    protected boolean requiresOAuth = false;
    protected boolean forceFamilyFilter = false;
    protected String password;

    private static String sAccessToken;
    private static String sLocalization;
    private static String sClientVersion;
    private static boolean sFamilyFilter;
    private static String sClientType = "dailymotion_sdk";

    public static void setAccessToken(String accessToken) {
        sAccessToken = accessToken;
    }
    public static void setLocalization(String localization) {
        sLocalization = localization;
    }
    public static void setClientVersion(String clientVersion) {
        sClientVersion = clientVersion;
    }
    public static void setsFamilyFilter(boolean familyFilter) {
        sFamilyFilter = familyFilter;
    }
    public static void setClientType(String clientType) {
        sClientType = clientType;
    }

    @Override
    public Map<String, String> getHeaders() {
        Map<String, String> headers = new HashMap<String, String>();

        if(requiresOAuth) {
            headers.put("Authorization", String.format("OAuth2 %s", sAccessToken));
        }

        headers.put("Accept-Language", Locale.getDefault().toString());

        return headers;
    }

    public ApiRequest(int method, String endpoint, Type clazz) {
        super(method, endpoint, clazz);
    }

    @Override
    protected String getBaseUrl(){
        return Api.getBaseUrl();
    }

    @Override
    protected Map<String, String> getGetParams() {
        return getCommonParams();
    }

    @Override
    protected Map<String, String> getPostParams() {
        return getCommonParams();
    }

    private Map<String, String> getCommonParams() {
        HashMap<String, String> globalParams = new HashMap<String, String>();

        globalParams.put("client_type", sClientType);
        globalParams.put("family_filter", forceFamilyFilter ? String.valueOf(1) : String.valueOf(sFamilyFilter));
        if (sClientVersion != null) {
            globalParams.put("client_version", sClientVersion);
        }
        if (sLocalization != null) {
            globalParams.put("localization", sLocalization);
        }

        return globalParams;
    }
}
