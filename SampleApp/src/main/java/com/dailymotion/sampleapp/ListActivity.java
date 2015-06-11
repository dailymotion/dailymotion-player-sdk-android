package com.dailymotion.sampleapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
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
import com.dailymotion.sampleapp.R;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Field;
import java.util.Map;

public class ListActivity extends Activity {


    private AdapterView.OnItemClickListener mOnClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent();
            Video video = (Video)parent.getAdapter().getItem(position);
            intent.putExtra(PlayerActivity.EXTRA_ID, video.id);
            intent.setClass(ListActivity.this, PlayerActivity.class);
            startActivity(intent);
        }
    };

    private HttpRequest.RequestListener<PagedList<Video>> mRequestListener = new HttpRequest.RequestListener<PagedList<Video>>() {
        @Override
        public void onRequestCompleted(HttpRequest<PagedList<Video>> request, PagedList<Video> response, HttpRequest.Error error) {
            ListView listView = (ListView)findViewById(R.id.listView);
            ProgressBar progressBar = (ProgressBar)findViewById(R.id.progressBar);
            progressBar.setVisibility(View.GONE);

            listView.setAdapter(new VideoListAdapter(ListActivity.this, response));
            listView.setOnItemClickListener(mOnClickListener);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.list_activity);

        Api.queue(new VideoListRequest(), mRequestListener);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_about: {
                Intent intent = new Intent();
                intent.setClass(this, AboutActivity.class);
                startActivity(intent);
                return true;
            }
            case R.id.action_settings: {
                Intent intent = new Intent();
                intent.setClass(this, ThePreferenceActivity.class);
                startActivity(intent);
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
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
