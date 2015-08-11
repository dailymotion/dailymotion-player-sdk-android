package com.dailymotion.sdk.api;

import android.text.TextUtils;

import com.dailymotion.sdk.util.DMLog;
import com.dailymotion.sdk.util.DigestUtils;
import com.dailymotion.sdk.api.model.Token;
import com.dailymotion.sdk.httprequest.HttpRequest;
import com.dailymotion.sdk.httprequest.JsonRequest;
import com.dailymotion.sdk.httprequest.RequestQueue;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class Api {

    private static Token sToken;
    private static String sClientSecret;
    private static String sClientId;
    private static String sBaseUrl = "https://api.dailymotion.com/";
    private static boolean sTokenPending;
    private static Queue<ApiRequest> sPendingRequests = new LinkedList<>();
    private static Queue<HttpRequest.RequestListener> sPendingListeners = new LinkedList<>();

    private static String sPassword;
    private static String sLogin;
    private static String sCnonce;

    private static HttpRequest.RequestListener sTokenListener1 = new HttpRequest.RequestListener() {
        @Override
        public void onRequestCompleted(HttpRequest request, Object response, HttpRequest.Error error) {
            if (error == null || error.httpError != 401) {
                DMLog.d(DMLog.REQUEST, "unexpected response while reading nonce");
                tokenError();
                return;
            }

            String header = (String)request.headers.get("WWW-Authenticate");
            String[] fields = header.split(",");
            HashMap<String, String> params = new HashMap<String, String>();
            for (int i = 0; i < fields.length; i++) {
                String[] items = fields[i].split("=");
                params.put(items[0], items[1].replace("\"", ""));
            }

            DMLog.d(DMLog.REQUEST, "nonce: " + params.get("nonce"));

            StringBuilder builder = new StringBuilder();
            builder.append(sClientId).append(":").append(params.get("Digest realm")).append(":").append(sClientSecret);
            String ha1 = DigestUtils.getMd5Hash(builder.toString());
            builder = new StringBuilder();
            builder.append("POST").append(":").append("/oauth/token");
            String ha2 = DigestUtils.getMd5Hash(builder.toString());
            builder = new StringBuilder();
            builder.append(ha1).append(":").append(params.get("nonce")).append(":").append("00000001").append(":")
                    .append(sCnonce).append(":").append(params.get("qop")).append(":").append(ha2);
            String clientDigest = DigestUtils.getMd5Hash(builder.toString());

            StringBuilder authorizationBuilder = new StringBuilder();
            authorizationBuilder.append(String.format("Digest username=\"%s\",", sClientId));
            authorizationBuilder.append(String.format("cnonce=\"%s\",", sCnonce));
            authorizationBuilder.append(String.format("realm=\"%s\",", params.get("Digest realm")));
            authorizationBuilder.append(String.format("nonce=\"%s\",", params.get("nonce")));
            authorizationBuilder.append(String.format("response=\"%s\",", clientDigest));
            authorizationBuilder.append(String.format("opaque=\"%s\",", params.get("opaque")));
            authorizationBuilder.append(String.format("uri=\"%s\",", "/oauth/token"));
            authorizationBuilder.append(String.format("nc=\"%s\",", "00000001"));
            authorizationBuilder.append(String.format("qop=\"%s\"", params.get("qop")));
            String authorization = authorizationBuilder.toString();

            HashMap<String, String> parameters = new HashMap<String, String>();
            if (sPassword != null) {
                // get token for the given password
                parameters.put("grant_type", "password");
                parameters.put("token_type", "Bearer");
                parameters.put("scope", "manage_favorites userinfo manage_videos manage_comments manage_playlists manage_tiles manage_subscriptions manage_friends manage_groups feed");
                parameters.put("username", TextUtils.htmlEncode(sLogin));
                parameters.put("password", TextUtils.htmlEncode(sPassword));
            } else if (sToken != null && sToken.refresh_token != null) {
                // try to refresh this token
                parameters.put("grant_type", "refresh_token");
                parameters.put("token_type", "Bearer");
                parameters.put("refresh_token", sToken.refresh_token);
            } else {
                // get token without password
                parameters.put("grant_type", "client_credentials");
                parameters.put("token_type", "Bearer");
                parameters.put("scope", "");
            }

            parameters.put("client_id", sClientId);

            HashMap<String, String> headers = new HashMap<>();
            headers.put("Authorization", authorization);

            HttpRequest<Token> tokenRequest = new TokenRequest(parameters, headers);
            RequestQueue.add(tokenRequest, sTokenListener2);
        }
    };

    private static HttpRequest.RequestListener<Token> sTokenListener2 = new HttpRequest.RequestListener<Token>() {
        @Override
        public void onRequestCompleted(HttpRequest request, Token response, HttpRequest.Error error) {
            if (response != null) {
                response.fetch_date = System.currentTimeMillis();
            }
            sTokenPending = false;

            if (!isTokenValid(response)) {
                tokenError();
                return;
            }

            sToken = response;
            ApiRequest.setAccessToken(sToken.access_token);

            DMLog.d(DMLog.REQUEST, "got token: " + sToken.access_token);

            while (sPendingRequests.peek() != null) {
                ApiRequest pendingRequest = sPendingRequests.remove();
                HttpRequest.RequestListener pendingListener = sPendingListeners.remove();

                // replay the request...
                RequestQueue.add(pendingRequest, pendingListener);
            }
        }
    };

    private static void tokenError() {
        DMLog.e(DMLog.REQUEST, "Cannot get a valid token");

        sTokenPending = false;

        while (sPendingRequests.peek() != null) {
            ApiRequest pendingRequest = sPendingRequests.remove();
            HttpRequest.RequestListener pendingListener = sPendingListeners.remove();

            HttpRequest.Error error = new HttpRequest.Error();
            if (pendingListener != null) {
                pendingListener.onRequestCompleted(pendingRequest, null, error);
            }
        }
    }

    private static boolean isTokenValid(Token sToken) {
        if (sToken == null) {
            return false;
        }
        if (sToken.error != null) {
            return false;
        }
        if ((System.currentTimeMillis() - sToken.fetch_date)/1000 > sToken.expires_in) {
            return  false;
        }
        return true;
    }

    public static Token getToken() {
        return sToken;
    }

    public static void setToken(Token token) {
        sToken = token;
        if (sToken != null) {
            ApiRequest.setAccessToken(sToken.access_token);
        } else {
            ApiRequest.setAccessToken(null);
        }
    }

    public static String getBaseUrl() {
        return sBaseUrl;
    }

    public static void setLogin(String login, String password) {
        sPassword = password;
        sLogin = login;
    }

    public static String computeFlagsFor(Class<?> clazz) {
        String fields = "";
        for (Field f :clazz.getDeclaredFields()) {
            if (!fields.equals("")) {
                fields += ",";
            }
            fields += JsonRequest.javaToJson(f.getName());
        }

        return fields;
    }

    static class TokenRequest extends JsonRequest<Token> {
        private HashMap<String, String> mPostParams;
        private HashMap<String, String> mGetParams;
        private HashMap<String, String> mHeaders;

        public TokenRequest(HashMap<String, String> postParams, HashMap<String, String> headers) {
            super(POST, "oauth/token", new TypeToken<Token>(){}.getType());
            mPostParams = postParams;
            mHeaders = headers;
        }

        @Override
        public HashMap<String, String> getGetParams() {
            return mGetParams;
        }

        @Override
        protected Map<String, String> getPostParams() {
            return mPostParams;
        }

        @Override
        protected String getBaseUrl() {
            return sBaseUrl;
        }

        @Override
        public Map<String, String> getHeaders() {
            return mHeaders;
        }

    }

    public static void setBaseUrl(String baseUrl) {
        sBaseUrl = baseUrl;
    }

    private static void refreshTokenFor(ApiRequest request, HttpRequest.RequestListener listener, String tag) {
        sPendingRequests.add(request);
        sPendingListeners.add(listener);

        if (sTokenPending) {
            return;
        }
        sTokenPending = true;

        DMLog.d(DMLog.REQUEST, "refreshing token...");

        HashMap<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Digest");
        HashMap<String, String> postParams = new HashMap<>();
        postParams.put("token_type", "Bearer");
        postParams.put("grant_type", "authorization_code");
        postParams.put("client_id", sClientId);
        TokenRequest tokenRequest = new TokenRequest(null, headers);
        RequestQueue.add(tokenRequest, sTokenListener1);
    }

    public static ApiRequest queue(ApiRequest request, final HttpRequest.RequestListener listener) {
        return queue(request, listener, "DEFAULT_TAG");
    }

    public static ApiRequest queue(ApiRequest request, final HttpRequest.RequestListener listener, final String tag) {
        if (!request.requiresOAuth) {
            // no need for Oauth, just queue the request
            RequestQueue.add(request, listener, tag);
            return request;
        }

        if (!isTokenValid(sToken)) {
            // the token is not valid anymore...
            refreshTokenFor(request, listener, tag);
            return request;
        }

        HttpRequest.RequestListener wrappedListener = new HttpRequest.RequestListener() {
            @Override
            public void onRequestCompleted(HttpRequest request, Object response, HttpRequest.Error error) {
                if (error != null && error.httpError == 401) {
                    refreshTokenFor((ApiRequest)request, listener, tag);
                } else {
                    if (listener != null) {
                        listener.onRequestCompleted(request, response, error);
                    }
                }
            }
        };

        RequestQueue.add(request, wrappedListener, tag);
        return request;
    }

    public static void init(String clientId, String clientSecret, String cnonce) {
        sClientId = clientId;
        sClientSecret = clientSecret;
        sCnonce = cnonce;
    }

}
