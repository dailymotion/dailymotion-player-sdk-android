package com.dailymotion.sdk.player;

import android.R;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.VideoView;

import com.dailymotion.sdk.util.DMLog;

public class DMWebVideoView extends WebView {

    private WebSettings mWebSettings;
    private WebChromeClient mChromeClient;
    private VideoView mCustomVideoView;
    private WebChromeClient.CustomViewCallback mViewCallback;
    public static String DEFAULT_PLAYER_URL = "http://www.dailymotion.com/embed/video/";
    private String mBaseUrl = DEFAULT_PLAYER_URL;
    private final String mExtraUA = "; DailymotionEmbedSDK 1.0";
    private FrameLayout mVideoLayout;
    private boolean mIsFullscreen = false;
    private ViewGroup mRootLayout;
    private boolean mAutoPlay = true;
    private String mExtraParameters;
    private String mVideoId;
    private long mStartNanos;

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

    public void setBaseUrl(String baseUrl) {
        mBaseUrl = baseUrl;
    }

    public void setExtraParameters(String extraParameters) {
        mExtraParameters = extraParameters;
    }


    private void init() {

        mWebSettings = getSettings();
        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setPluginState(WebSettings.PluginState.ON);
        mWebSettings.setUserAgentString(mWebSettings.getUserAgentString() + mExtraUA);
        if (Build.VERSION.SDK_INT >= 17) {
            mWebSettings.setMediaPlaybackRequiresUserGesture(false);
        }

        mChromeClient = new WebChromeClient() {

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
                if (view instanceof FrameLayout) {
                    FrameLayout frame = (FrameLayout) view;
                    if (frame.getFocusedChild() instanceof VideoView) {//We are in 2.3
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
            public Bitmap getDefaultVideoPoster() {
                int colors[] = new int[1];
                colors[0] = Color.TRANSPARENT;
                Bitmap bm = Bitmap.createBitmap(colors, 0, 1, 1, 1, Bitmap.Config.ARGB_8888);
                return bm;
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
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Uri uri = Uri.parse(url);
                if (uri.getScheme().equals("dmevent")) {
                    String event = uri.getQueryParameter("event");
                    if (event.equals("apiready")) {
                        if (mAutoPlay) {
                            callPlayerMethod("play");
                        }
                        DMLog.d(DMLog.STUFF, "apiready took " + ((double)(System.nanoTime() - mStartNanos))/1000000000.0);
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

    public void setVideoId(String videoId) {
        mVideoId = videoId;
    }

    public void load() {

        if (mRootLayout == null) {
            //The topmost layout of the window where the actual VideoView will be added to
            mRootLayout = (FrameLayout) ((Activity) getContext()).getWindow().getDecorView();
        }

        String url = mBaseUrl + mVideoId + "?app=" + getContext().getPackageName() + "&api=location";
        if (mExtraParameters != null && !mExtraParameters.equals("")) {
            url += "&" + mExtraParameters;
        }

        DMLog.d(DMLog.STUFF, "loading " + url);

        mStartNanos = System.nanoTime();
        loadUrl(url);
    }

    public void hideVideoView() {
        if (isFullscreen()) {
            if (mCustomVideoView != null) {
                mCustomVideoView.stopPlayback();
            }
            mRootLayout.removeView(mVideoLayout);
            mViewCallback.onCustomViewHidden();
            ((Activity) getContext()).setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
            mIsFullscreen = false;
        }


    }

    private void setupVideoLayout(View video) {

        /**
         * As we don't want the touch events to be processed by the underlying WebView, we do not set the WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE flag
         * But then we have to handle directly back press in our View to exit fullscreen.
         * Otherwise the back button will be handled by the topmost Window, id-est the player controller
         */
        mVideoLayout = new FrameLayout(getContext()) {

            @Override
            public boolean dispatchKeyEvent(KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                    hideVideoView();
                    return true;
                }

                return super.dispatchKeyEvent(event);
            }
        };

        mVideoLayout.setBackgroundResource(R.color.black);
        mVideoLayout.addView(video);
        ViewGroup.LayoutParams lp = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mRootLayout.addView(mVideoLayout, lp);
        mIsFullscreen = true;
    }

    public boolean isFullscreen() {
        return mIsFullscreen;
    }

    public void handleBackPress(Activity activity) {
        if (isFullscreen()) {
            hideVideoView();
        } else {
            loadUrl("");//Hack to stop video
            activity.finish();
        }
    }

    public boolean isAutoPlaying() {
        return mAutoPlay;
    }

    public void setAutoPlay(boolean autoPlay) {
        mAutoPlay = autoPlay;
    }

    public void seek(double time) {
        loadUrl("javascript:player.api(\"seek\"," + time + ")");
    }

    public void setRootViewGroup(ViewGroup rootView) {
        mRootLayout = rootView;
    }
}