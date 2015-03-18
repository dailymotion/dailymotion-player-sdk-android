package com.dailymotion.sample;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.dailymotion.sdk.api.model.PagedList;
import com.dailymotion.sdk.api.model.Video;
import com.dailymotion.sdk.httprequest.HttpRequest;
import com.dailymotion.sdk.httprequest.RequestQueue;

public class VideoListAdapter implements ListAdapter {
    private final Context mContext;
    PagedList<Video> mPage;

    public VideoListAdapter(Context context, PagedList<Video> page) {
        mPage = page;
        mContext = context;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getCount() {
        return mPage.list.size();
    }

    @Override
    public Object getItem(int position) {
        return mPage.list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.video_view, null);
        final ImageView imageView = (ImageView)view.findViewById(R.id.imageView);
        TextView textView = (TextView)view.findViewById(R.id.textView);

        Video video = mPage.list.get(position);
        textView.setText(video.title);

        final HttpRequest.RequestListener<Bitmap> listener = new HttpRequest.RequestListener<Bitmap>() {

            @Override
            public void onRequestCompleted(HttpRequest request, Bitmap response, HttpRequest.Error error) {
                imageView.setImageBitmap(response);
            }
        };

        RequestQueue.add(new BitmapRequest(video.thumbnail_720_url), listener);

        return view;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
