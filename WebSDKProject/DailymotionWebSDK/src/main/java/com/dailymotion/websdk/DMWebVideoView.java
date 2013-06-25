//
//  DMWebVideoView.java
//  Dailymotion
//
//  Created by Guillaume Lachaud on 30/05/13.
//  Copyright (c) 2013 Dailymotion. All rights reserved.
//
package com.dailymotion.websdk;

import android.R;
import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.VideoView;

public class DMWebVideoView extends WebView {

    private WebSettings                         mWebSettings;
    private WebChromeClient                     mChromeClient;
    private VideoView                           mCustomVideoView;
    private WebChromeClient.CustomViewCallback  mViewCallback;

    private final String                        mEmbedUrl = "http://www.dailymotion.com/embed/video/%s?html=1";
    private final String                        mExtraUA = "; DailymotionEmbedSDK 1.0";
    private FrameLayout                         mVideoLayout;
    private boolean                             mIsFullscreen = false;
    private AudioManager                        mAudioManager;
    private FrameLayout                         mRootLayout;

    public DMWebVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public DMWebVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DMWebVideoView(Context context) {
        super(context);
        init();
    }

    private void init(){

        //The topmost layout of the window where the actual VideoView will be added to
        mRootLayout = (FrameLayout) ((Activity) getContext()).getWindow().getDecorView();

        //We want to make sure the user gets the ability to sets the Media volume
        mAudioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        mWebSettings = getSettings();
        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setUserAgentString(mWebSettings.getUserAgentString() + mExtraUA);

        mChromeClient = new WebChromeClient(){

            /**
             * The view to be displayed while the fullscreen VideoView is buffering
             * @return
             */
            @Override
            public View getVideoLoadingProgressView() {
                ProgressBar pb = new ProgressBar(getContext());
                pb.setIndeterminate(true);
                return pb;
            }

            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                super.onShowCustomView(view, callback);
                ((Activity) getContext()).setVolumeControlStream(AudioManager.STREAM_MUSIC);
                mIsFullscreen = true;
                mViewCallback = callback;
                if (view instanceof FrameLayout){
                    FrameLayout frame = (FrameLayout) view;
                    if (frame.getFocusedChild() instanceof VideoView){//We are in 2.3
                        VideoView video = (VideoView) frame.getFocusedChild();
                        frame.removeView(video);

                        setupVideoLayout(video);

                        mCustomVideoView = video;
                        mCustomVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                            @Override
                            public void onCompletion(MediaPlayer mediaPlayer) {
                            hideVideoView();
                            }
                        });


                    } else {//Handle 4.x

                        setupVideoLayout(view);

                    }
                }
            }

            @Override
            public void onShowCustomView(View view, int requestedOrientation, CustomViewCallback callback) // Only available in API level 14+
            {
                onShowCustomView(view, callback);
            }

            @Override
            public void onHideCustomView() {
                super.onHideCustomView();
            }
        };


        setWebChromeClient(mChromeClient);
    }

    public void setVideoId(String videoId){
        loadUrl(String.format(mEmbedUrl, videoId));
    }

    public void setVideoUrl(String url){
        loadUrl(url);
    }

    public void hideVideoView(){
        if(isFullscreen()){
            if(mCustomVideoView != null){
                mCustomVideoView.stopPlayback();
            }
            mRootLayout.removeView(mVideoLayout);
            mViewCallback.onCustomViewHidden();
            mChromeClient.onHideCustomView();
            ((Activity) getContext()).setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
            mIsFullscreen = false;
        }


    }

    private void setupVideoLayout(View video){

        /**
         * As we don't want the touch events to be processed by the underlying WebView, we do not set the WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE flag
         * But then we have to handle directly back press in our View to exit fullscreen.
         * Otherwise the back button will be handled by the topmost Window, id-est the player controller
         */
        mVideoLayout = new FrameLayout(getContext()){

            @Override
            public boolean dispatchKeyEvent(KeyEvent event) {
                if(event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP){
                    hideVideoView();
                }

                return super.dispatchKeyEvent(event);
            }};

        mVideoLayout.setBackgroundResource(R.color.black);
        mVideoLayout.addView(video);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        lp.gravity = Gravity.CENTER;
        mRootLayout.addView(mVideoLayout, lp);
    }

    public boolean isFullscreen(){
        return mIsFullscreen;
    }

}