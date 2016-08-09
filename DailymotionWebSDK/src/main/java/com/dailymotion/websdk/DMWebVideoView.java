package com.dailymotion.websdk;

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
import android.util.Log;
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

import java.util.Set;

// import com.dailymotion.sdk.util.DMLog;

public class DMWebVideoView extends WebView {

    public interface Listener {

        public void onEvent(String event);

    }

    public class Error {

        public String code;
        public String title;
        public String message;

        public Error(String c, String t, String m) {
            code = c;
            title = t;
            message = m;
        }

    }

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
    private Listener mListener = null;

    public boolean apiReady = false;
    public boolean autoplay = false;
    public double currentTime = 0;
    public double bufferedTime = 0;
    public double duration = 0;
    public boolean seeking = false;
    public Object error = null;
    public boolean ended = false;
    public boolean paused = true;
    public boolean fullscreen = false;
    public boolean rebuffering = false;
    public String qualities = "";
    public String quality = "";
    public String subtitles = "";
    public String subtitle = "";

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

    public void setEventListener(Listener listener) {
        mListener = listener;
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
                        // DMLog.d(DMLog.STUFF, "apiready took " + ((double) (System.nanoTime() - mStartNanos)) / 1000000000.0);
                    }

                    switch (event)
                    {
                        case "apiready": apiReady = true; break;
                        case "start": ended = false; break;
                        case "loadedmetadata": error = null; break;
                        case "timeupdate":
                        case "ad_timeupdate": currentTime = Double.parseDouble(uri.getQueryParameter("time")); break;
                        case "progress": bufferedTime = Double.parseDouble(uri.getQueryParameter("time")); break;
                        case "durationchange": duration = Double.parseDouble(uri.getQueryParameter("duration")); break;
                        case "seeking": seeking = true; currentTime = Double.parseDouble(uri.getQueryParameter("time")); break;
                        case "seeked": seeking = false; currentTime = Double.parseDouble(uri.getQueryParameter("time")); break;
                        case "fullscreenchange": fullscreen = parseBooleanFromAPI(uri.getQueryParameter("fullscreen")); break;
                        case "video_start":
                        case "ad_start":
                        case "ad_play":
                        case "playing":
                        case "play": paused = false; break;
                        case "end": ended = true; break;
                        case "ad_pause":
                        case "ad_end":
                        case "video_end":
                        case "pause": paused = true; break;
                        case "error":
                            error = new DMWebVideoView.Error(uri.getQueryParameter("code"), uri.getQueryParameter("title"), uri.getQueryParameter("message"));
                            break;
                        case "rebuffer": rebuffering = parseBooleanFromAPI(uri.getQueryParameter("rebuffering")); break;
                        case "qualitiesavailable": qualities = uri.getQueryParameter("qualities"); break;
                        case "qualitychange": quality = uri.getQueryParameter("quality"); break;
                        case "subtitlesavailable": subtitles = uri.getQueryParameter("subtitles"); break;
                        case "subtitlechange": subtitle = uri.getQueryParameter("subtitle"); break;
                    }

                    if (mListener != null) {
                        mListener.onEvent(event);
                    }
                    return true;
                } else {
                    return super.shouldOverrideUrlLoading(view, url);
                }
            }
        });
    }

    private boolean parseBooleanFromAPI(String value) {
        if (value.equals("true") || value.equals("1")) {
            return true;
        }
        return false;
    }

    private void callPlayerMethod(String method) {
        loadUrl("javascript:player.api(\"" + method + "\")");
    }

    private void callPlayerMethod(String method, String param) {
        loadUrl("javascript:player.api(\"" + method + "\", \"" + param + "\")");
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

        // DMLog.d(DMLog.STUFF, "loading " + url);

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

    public void play() {
        callPlayerMethod("play");
    }

    public void togglePlay() {
        callPlayerMethod("toggle-play");
    }

    public void pause() {
        callPlayerMethod("pause");
    }

    public void seek(double time) {
        callPlayerMethod("seek", Double.toString(time));
    }

    public void setQuality(String quality) {
        callPlayerMethod("quality", quality);
    }

    public void setSubtitle(String language_code) {
        callPlayerMethod("subtitle", language_code);
    }

    public void setControls(boolean visible) {
        callPlayerMethod("controls", (visible ? "true" : "false"));
    }

    public void toggleControls() {
        callPlayerMethod("toggle-controls");
    }

    public void setRootViewGroup(ViewGroup rootView) {
        mRootLayout = rootView;
    }
}