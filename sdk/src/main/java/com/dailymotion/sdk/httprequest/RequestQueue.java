package com.dailymotion.sdk.httprequest;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;
import com.dailymotion.sdk.util.DMLog;

import java.util.Map;
import java.util.concurrent.ExecutionException;

public class RequestQueue {
    private static com.android.volley.RequestQueue sRequestQueue;

    public static void init(Context context) {
        sRequestQueue = Volley.newRequestQueue(context, new HurlStack());
    }

    public static <T> Request<T> getVolleyRequest(final HttpRequest<T> httpRequest, String tag, final HttpRequest.RequestListener requestListener, final RequestFuture<T> future) {
        String url = httpRequest.getUrl();
        Request<T> volleyRequest = new Request<T>(httpRequest.getMethod(), url, null) {
            @Override
            protected Response<T> parseNetworkResponse(NetworkResponse response) {
                if (response != null) {
                    httpRequest.data = response.data;
                    httpRequest.headers = response.headers;
                }
                try {
                    T result = httpRequest.parseResponse(response.data, response.headers);
                    return Response.success(result, HttpHeaderParser.parseCacheHeaders(response));
                } catch (Exception e) {
                    return Response.error(new ParseError(e));
                }
            }

            @Override
            protected VolleyError parseNetworkError(VolleyError volleyError) {
                if (volleyError.networkResponse != null) {
                    httpRequest.data = volleyError.networkResponse.data;
                    httpRequest.headers = volleyError.networkResponse.headers;
                }

                return super.parseNetworkError(volleyError);
            }

            @Override
            protected void deliverResponse(T response) {
                if (requestListener != null) {
                    requestListener.onRequestCompleted(httpRequest, response, null);
                }
                if (future != null) {
                    future.onResponse(response);
                }
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return httpRequest.getHeaders();
            }

            @Override
            public void deliverError(VolleyError error) {
                HttpRequest.Error httpError = new HttpRequest.Error();
                DMLog.d(DMLog.REQUEST, "error: " + httpRequest.getUrl());

                if (error.networkResponse != null) {
                    httpError.httpError = error.networkResponse.statusCode;
                    DMLog.d(DMLog.REQUEST, new String(error.networkResponse.data));
                } else if (error.getCause() != null) {
                    error.printStackTrace();
                }

                if (requestListener != null) {
                    requestListener.onRequestCompleted(httpRequest, null, httpError);
                }
                if (future != null) {
                    future.onErrorResponse(error);
                }
            }

            public String getBodyContentType() {
                return httpRequest.getBodyContentType();
            }

            public byte[] getBody() throws AuthFailureError {
                return httpRequest.getBody();
            }
        };

        volleyRequest.setShouldCache(false);
        volleyRequest.setTag(tag);

        return volleyRequest;
    }

    public static <T> HttpRequest<T> add(HttpRequest<T> httpRequest, HttpRequest.RequestListener requestListener) {
        return add(httpRequest, requestListener, "DEFAULT_TAG");
    }

    private static <T> void addAndLog(Request<T> volleyRequest) {
        sRequestQueue.add(volleyRequest);
        DMLog.d(DMLog.REQUEST, "queue " + volleyRequest.getUrl());

    }
    public static <T> HttpRequest<T> add(HttpRequest<T> httpRequest, HttpRequest.RequestListener requestListener, String tag) {
        Request<T> volleyRequest = getVolleyRequest(httpRequest, tag, requestListener, null);

        addAndLog(volleyRequest);

        return httpRequest;
    }

    public static <T> T addAndWait(HttpRequest<T> httpRequest, String tag) {
        RequestFuture<T> future = RequestFuture.newFuture();
        Request<T> volleyRequest = getVolleyRequest(httpRequest, tag, null, future);

        addAndLog(volleyRequest);

        T result = null;
        try {
            result = future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static void cancelAll(String tag) {
        if (sRequestQueue != null) {
            sRequestQueue.cancelAll(tag);
        }
    }
}
