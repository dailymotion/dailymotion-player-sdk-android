package com.dailymotion.websdksample;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
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

public class ListActivity extends Activity {

    HttpRequest.RequestListener<PagedList<Video>> mListener = new HttpRequest.RequestListener<PagedList<Video>>() {
        @Override
        public void onRequestCompleted(HttpRequest<PagedList<Video>> request, PagedList<Video> response, HttpRequest.Error error) {
            ListView listView = (ListView)findViewById(R.id.listView);
            ProgressBar progressBar = (ProgressBar)findViewById(R.id.progressBar);
            progressBar.setVisibility(View.GONE);

            listView.setAdapter(new VideoListAdapter(ListActivity.this, response));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.list_activity);

        Api.queue(new VideoListRequest(), mListener);
    }

    static class VideoListRequest extends ApiRequest<PagedList<Video>> {

        public VideoListRequest() {
            super(GET, "videos", new TypeToken<PagedList<Video>>(){}.getType());
        }

        @Override
        protected Map<String, String> getGetParams() {
            Map<String, String> map = super.getGetParams();
            String fields = "";
            for (Field f :Video.class.getDeclaredFields()) {
                if (!fields.equals("")) {
                    fields += ",";
                }
                fields += JsonRequest.javaToJson(f.getName());
            }

            map.put("fields", fields);
            return map;
        }
    }
}
