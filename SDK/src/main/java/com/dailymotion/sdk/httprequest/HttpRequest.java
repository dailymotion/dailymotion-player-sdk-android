package com.dailymotion.sdk.httprequest;

import android.net.Uri;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class HttpRequest<T> {
    public static final int GET = 0;
    public static final int POST = 1;
    public static final int PUT = 2;
    public static final int DELETE = 3;
    public static final int HEAD = 4;
    public static final int OPTIONS = 5;
    public static final int TRACE = 6;
    public static final int PATCH = 7;

    private static final String DEFAULT_ENCODING = "UTF-8";

    private final String mEndpoint;
    private int mMethod;
    public byte[] data;
    public Map<String, String> headers;

    public static class Error {
        public int error;
        public int httpError;
    }

    public int getMethod() {
        return mMethod;
    }
    public String getUrl() {
        String url = computeGetParams(getBaseUrl() + mEndpoint);
        return url;
    }

    public interface RequestListener<T>{
        public void onRequestCompleted(HttpRequest<T> request, T response, Error error);
    }

    public HttpRequest(int method, String endpoint) {
        this.mMethod = method;
        this.mEndpoint = endpoint;
    }

    protected abstract T parseResponse(byte[] data, Map<String, String> headers) throws Exception;
    protected Map<String, String> getHeaders() {
        return new HashMap<String, String>();
    }
    protected Map<String, String> getGetParams() {
        return new HashMap<String, String>();
    }
    protected Map<String, String> getPostParams() {
        return new HashMap<String, String>();
    }
    protected abstract String getBaseUrl();
    protected String getBodyContentType() {
        return "application/x-www-form-urlencoded; charset=";
    }
    public byte[] getBody() {
        Map<String, String> params = getPostParams();
        if (params != null && params.size() > 0) {
            return encodeParameters(params, DEFAULT_ENCODING);
        }
        return null;
    }

    /**
     * Converts <code>params</code> into an application/x-www-form-urlencoded encoded string.
     */
    private byte[] encodeParameters(Map<String, String> params, String paramsEncoding) {
        StringBuilder encodedParams = new StringBuilder();
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                encodedParams.append(URLEncoder.encode(entry.getKey(), paramsEncoding));
                encodedParams.append('=');
                encodedParams.append(URLEncoder.encode(entry.getValue(), paramsEncoding));
                encodedParams.append('&');
            }
            return encodedParams.toString().getBytes(paramsEncoding);
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException("Encoding not supported: " + paramsEncoding, uee);
        }
    }

    protected String computeGetParams(String url){
        if(mMethod == GET){
            Uri.Builder builder = Uri.parse(url).buildUpon();
            Map<String, String> params = getGetParams();
            Iterator it = params.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, String> pairs = (Map.Entry) it.next();
                builder.appendQueryParameter(pairs.getKey(), pairs.getValue());
            }
            return builder.build().toString();
        } else {
            return url;
        }
    }

    static protected String serializeList(List<String> list) {
        StringBuilder sbFields = new StringBuilder();
        if(list != null && list.size() > 0){
            for (String field : list) {
                if (sbFields.length() > 0) {
                    sbFields.append(",");
                }
                sbFields.append(field);
            }
        }

        return sbFields.toString();
    }

}
