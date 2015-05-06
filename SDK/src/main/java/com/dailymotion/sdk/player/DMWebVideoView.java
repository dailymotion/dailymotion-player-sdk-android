package com.dailymotion.sdk.player;

import android.R;
import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.VideoView;

public class DMWebVideoView extends WebView {

    private WebSettings                         mWebSettings;
    private WebChromeClient                     mChromeClient;
    private VideoView                           mCustomVideoView;
    private WebChromeClient.CustomViewCallback  mViewCallback;

    private final String                        mEmbedUrl = "http://www.dailymotion.com/embed/video/%s?html=1&fullscreen=%s&app=%s&api=location&GK_PV5=1";
    private final String                        mExtraUA = "; DailymotionEmbedSDK 1.0";
    private FrameLayout                         mVideoLayout;
    private boolean                             mIsFullscreen = false;
    private FrameLayout                         mRootLayout;
    private boolean                             mAllowAutomaticNativeFullscreen = false;
    private boolean mAutoPlay = false;

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

        mWebSettings = getSettings();
        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setPluginState(WebSettings.PluginState.ON);
        mWebSettings.setUserAgentString(mWebSettings.getUserAgentString() + mExtraUA);

        mChromeClient = new WebChromeClient(){

            /**
             * The view to be displayed while the fullscreen VideoView is buffering
             * @return the progress view
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
                hideVideoView();
            }

        };


        setWebChromeClient(mChromeClient);
        setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading (WebView view, String url) {
                Uri uri= Uri.parse(url);
                if (uri.getScheme().equals("dmevent")) {
                    String event = uri.getQueryParameter("event");
                    if (event.equals("apiready")) {
                        if (mAutoPlay) {
                            callPlayerMethod("play");
                        }
                    }
                    return true;
                } else {
                    return super.shouldOverrideUrlLoading(view, url);
                }
            }
        });
    }

    private void callPlayerMethod(String method) {
        loadUrl("javascript:player.api(\"" + method + "\")");
    }
    public void setVideoId(String videoId){
        loadUrl(String.format(mEmbedUrl, videoId, mAllowAutomaticNativeFullscreen, getContext().getPackageName()));
    }

    public void setVideoId(String videoId, boolean autoPlay){
        mAutoPlay = autoPlay;
        loadUrl(String.format(mEmbedUrl, videoId, mAllowAutomaticNativeFullscreen, getContext().getPackageName()));
    }

    public void hideVideoView(){
        if(isFullscreen()){
            if(mCustomVideoView != null){
                mCustomVideoView.stopPlayback();
            }
            mRootLayout.removeView(mVideoLayout);
            mViewCallback.onCustomViewHidden();
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
                    return true;
                }

                return super.dispatchKeyEvent(event);
            }};

        mVideoLayout.setBackgroundResource(R.color.black);
        mVideoLayout.addView(video);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        lp.gravity = Gravity.CENTER;
        mRootLayout.addView(mVideoLayout, lp);
        mIsFullscreen = true;
    }

    public boolean isFullscreen(){
        return mIsFullscreen;
    }

    public void handleBackPress(Activity activity) {
        if(isFullscreen()){
            hideVideoView();
        } else {
            loadUrl("");//Hack to stop video
            activity.finish();
        }
    }

    public void setAllowAutomaticNativeFullscreen(boolean allowAutomaticNativeFullscreen){
        mAllowAutomaticNativeFullscreen = allowAutomaticNativeFullscreen;
    }

    public boolean isAutoPlaying(){
        return mAutoPlay;
    }

    public void setAutoPlay(boolean autoPlay){
        mAutoPlay = autoPlay;
    }
}