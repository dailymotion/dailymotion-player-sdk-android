package com.dailymotion.sampleapp;

import android.content.pm.ActivityInfo;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.dailymotion.sdk.api.Api;
import com.dailymotion.sdk.api.ApiRequest;
import com.dailymotion.sdk.api.model.PagedList;
import com.dailymotion.sdk.api.model.User;
import com.dailymotion.sdk.api.model.Video;
import com.dailymotion.sdk.broadcast.BroadcastView;
import com.dailymotion.sdk.httprequest.HttpRequest;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class ScreenBroadcast extends Screen {
    private View mView;
    private BroadcastView mBroadcastView;
    private TextView mText;
    private HttpRequest.RequestListener<Video> mRequestListener = new HttpRequest.RequestListener<Video>() {
        @Override
        public void onRequestCompleted(HttpRequest<Video> request, Video response, HttpRequest.Error error) {
            if (response != null && error == null) {
                mBroadcastView.startPublishing(response.live_publish_url);
                mText.setText(String.format(mActivity.getString(R.string.explanation), response.title, response.id));
            } else {
                Snackbar.make(mView, mActivity.getString(R.string.cannot_create_video), Snackbar.LENGTH_LONG);
            }
        }
    };

    public ScreenBroadcast(MainActivity activity) {
        super(activity);
    }


    static class CreateVideoRequest extends ApiRequest<PagedList<Video>> {

        public CreateVideoRequest() {
            super(POST, "me/videos", new TypeToken<Video>(){}.getType());
            requiresOAuth = true;
        }

        @Override
        protected Map<String, String> getGetParams() {
            Map<String, String> map = super.getGetParams();

            map.put("fields", Api.computeFlagsFor(Video.class));

            return map;
        }

        @Override
        protected Map<String, String> getPostParams() {
            Map<String, String> map = super.getPostParams();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.US);

            map.put("title", "live_" + sdf.format(new Date()));
            map.put("mode", "live");
            map.put("published", "true");
            map.put("channel", "Tech");

            return map;
        }
    }
    @Override
    protected View onCreateView() {
        mView = LayoutInflater.from(mActivity).inflate(R.layout.screen_broadcast, null);
        mBroadcastView = (BroadcastView)mView.findViewById(R.id.broadcast);
        mText = (TextView)mView.findViewById(R.id.text);

        Api.queue(new CreateVideoRequest(), mRequestListener);
        return mView;
    }

    @Override
    public void onDestroy() {
        mBroadcastView.release();
    }

    @Override
    public void onStart() {
        super.onStart();
        mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    @Override
    public void onStop() {
        super.onStop();
        mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }
}
