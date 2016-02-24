package com.dailymotion.sampleapp;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.dailymotion.sdk.api.Api;
import com.dailymotion.sdk.api.ApiRequest;
import com.dailymotion.sdk.api.model.PagedList;
import com.dailymotion.sdk.api.model.Video;
import com.dailymotion.sdk.httprequest.HttpRequest;
import com.dailymotion.sdk.httprequest.JsonRequest;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Field;
import java.util.Map;

public class ScreenVideoList extends Screen {
    private AdapterView mListView;
    private ProgressBar mProgressBar;

    public ScreenVideoList(MainActivity activity) {
        super(activity);
    }

    private AdapterView.OnItemClickListener mOnClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent();
            Video video = (Video)parent.getAdapter().getItem(position);

            mActivity.pushScreen(new ScreenPlayer(mActivity, video));
        }
    };

    private HttpRequest.RequestListener<PagedList<Video>> mRequestListener = new HttpRequest.RequestListener<PagedList<Video>>() {
        @Override
        public void onRequestCompleted(HttpRequest<PagedList<Video>> request, PagedList<Video> response, HttpRequest.Error error) {
            mProgressBar.setVisibility(View.GONE);

            mListView.setAdapter(new VideoListAdapter(mActivity, response));
            mListView.setOnItemClickListener(mOnClickListener);
        }
    };

    @Override
    public View onCreateView() {
        View v = LayoutInflater.from(mActivity).inflate(R.layout.screen_videolist, null);
        mListView = (ListView)v.findViewById(R.id.listView);
        mProgressBar = (ProgressBar)v.findViewById(R.id.progressBar);

        Api.queue(new VideoListRequest(), mRequestListener);
        return v;
    }

    static class VideoListRequest extends ApiRequest<PagedList<Video>> {

        public VideoListRequest() {
            super(GET, "videos", new TypeToken<PagedList<Video>>(){}.getType());
        }

        @Override
        protected Map<String, String> getGetParams() {
            Map<String, String> map = super.getGetParams();
            map.put("fields", Api.computeFlagsFor(Video.class));
            return map;
        }
    }
}
