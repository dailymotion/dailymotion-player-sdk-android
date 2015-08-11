package com.dailymotion.sampleapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.dailymotion.sdk.httprequest.HttpRequest;

import java.util.Map;

public class BitmapRequest extends HttpRequest<Bitmap> {
    private String mUrl;
    public BitmapRequest(String url) {
        super(GET, "");
        mUrl = url;
    }

    @Override
    protected Bitmap parseResponse(byte[] data, Map<String, String> headers) throws Exception {
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        return bitmap;
    }

    @Override
    protected String getBaseUrl() {
        return mUrl;
    }
}
