package com.dailymotion.android.player.sdk

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.net.http.SslError
import android.os.Build
import android.os.Handler
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.webkit.*
import android.widget.ProgressBar
import androidx.annotation.RequiresApi
import com.dailymotion.android.BuildConfig
import com.dailymotion.android.player.sdk.AdIdTask.AdIdTaskListener
import com.dailymotion.android.player.sdk.events.PlayerEvent
import com.dailymotion.android.player.sdk.events.PlayerEventFactory
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.google.gson.Gson
import timber.log.Timber
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.net.URLDecoder
import java.net.URLEncoder
import java.util.*

/**
 * Created by hugo
 * on 6/13/17.
 */
class PlayerWebView : WebView {

    private val mCommandList = ArrayList<Command>()
    private val mExtraUA = ";dailymotion-player-sdk-android " + BuildConfig.SDK_VERSION

    private var mHandler: Handler? = null
    private var mGson: Gson? = null
    private var mDisallowIntercept = false
    private var mPosition = 0f
    private var mPlayWhenReady = true
    private var mVisible = false

    private var mIsWebContentsDebuggingEnabled = false
    private var mControlsCommandRunnable: Runnable? = null
    private var mMuteCommandRunnable: Runnable? = null
    private var mLoadCommandRunnable: Runnable? = null

    private var mIsInitialized = false
    private var mIsFullScreen = false
    private var mVolume = 1f
    private var mControlsLastTime: Long = 0
    private var mMuteLastTime: Long = 0
    private var mLoadLastTime: Long = 0
    private var eventFactory: PlayerEventFactory? = null
    private var mApiReady = false
    private var mHasMetadata = false
    private var mHasPlaybackReady = false
    private var mQuality: String? = ""
    private var webViewErrorListener: WebViewErrorListener? = null
    private var playerEventListener: EventListener? = null

    var mJavascriptBridge: Any = JavascriptBridge()

    var videoId: String? = null
        private set

    var videoPaused = false
        private set
    var bufferedTime = 0.0
        private set
    var duration = 0.0
        private set
    var isSeeking = false
        private set
    var isEnded = false
        private set

    var quality: String?
        get() = mQuality
        set(quality) {
            queueCommand(COMMAND_QUALITY, quality!!)
        }

    val position: Long
        get() = (mPosition * 1000).toLong()

    var volume: Float
        get() = mVolume
        set(volume) {
            if (volume in 0f..1f) {
                queueCommand(COMMAND_VOLUME, volume)
            }
        }

    var playWhenReady: Boolean
        get() = mPlayWhenReady
        set(playWhenReady) {
            mPlayWhenReady = playWhenReady
            updatePlayState()
        }

    constructor(context: Context?) : super(context) { /* do nothing */ }
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) { /* do nothing */ }
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) { /* do nothing */ }

    @JvmOverloads
    fun load(videoId: String?, loadParams: Map<String, Any?>? = emptyMap()) {
        if (!mIsInitialized) {
            val defaultQueryParameters: MutableMap<String?, String?> = HashMap()
            defaultQueryParameters["sharing-enable"] = "false"
            defaultQueryParameters["watchlater-enable"] = "false"
            defaultQueryParameters["like-enable"] = "false"
            defaultQueryParameters["collections-enable"] = "false"
            defaultQueryParameters["fullscreen-action"] = "trigger_event"
            defaultQueryParameters["locale"] = Locale.getDefault().language
            initialize("https://www.dailymotion.com/embed/", defaultQueryParameters, HashMap())
        }
        queueCommand(COMMAND_LOAD, videoId!!, loadParams!!)
    }

    fun initialize(baseUrl: String?, queryParameters: Map<String?, String?>?, httpHeaders: Map<String?, String?>?) {
        mIsInitialized = true
        eventFactory = PlayerEventFactory()
        AdIdTask(context, object : AdIdTaskListener {
            override fun onResult(result: AdvertisingIdClient.Info?) {
                finishInitialization(baseUrl, queryParameters, httpHeaders, result)
            }
        }).execute()
    }

    @SuppressLint("JavascriptInterface")
    fun finishInitialization(baseUrl: String?, queryParameters: Map<String?, String?>?, httpHeaders: Map<String?, String?>?, adInfo: AdvertisingIdClient.Info?) {
        mGson = Gson()
        val mWebSettings = settings
        mWebSettings.domStorageEnabled = true
        mWebSettings.javaScriptEnabled = true
        mWebSettings.userAgentString = mWebSettings.userAgentString + mExtraUA
        mWebSettings.pluginState = WebSettings.PluginState.ON
        setBackgroundColor(Color.BLACK)
        if (Build.VERSION.SDK_INT >= 17) {
            mWebSettings.mediaPlaybackRequiresUserGesture = false
        }
        mHandler = Handler()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setWebContentsDebuggingEnabled(mIsWebContentsDebuggingEnabled)
        }
        val mChromeClient: WebChromeClient = object : WebChromeClient() {
            /**
             * The view to be displayed while the fullscreen VideoView is buffering
             * @return the progress view
             */
            override fun getVideoLoadingProgressView(): View {
                val pb = ProgressBar(context)
                pb.isIndeterminate = true
                return pb
            }

            override fun onShowCustomView(view: View, callback: CustomViewCallback) {}
            override fun getDefaultVideoPoster(): Bitmap {
                val colors = IntArray(1)
                colors[0] = Color.TRANSPARENT
                return Bitmap.createBitmap(colors, 0, 1, 1, 1, Bitmap.Config.ARGB_8888)
            }

            override fun onHideCustomView() {}
        }
        addJavascriptInterface(mJavascriptBridge, "dmpNativeBridge")
        webViewClient = object : WebViewClient() {
            override fun shouldInterceptRequest(view: WebView, url: String): WebResourceResponse? {
                if (url.startsWith(ASSETS_SCHEME)) {
                    val asset = url.substring(ASSETS_SCHEME.length)
                    if (asset.endsWith(".ttf") || asset.endsWith(".otf")) {
                        try {
                            val inputStream = context.assets.open(asset)
                            var response: WebResourceResponse? = null
                            val encoding = "UTF-8"
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                val statusCode = 200
                                val reasonPhase = "OK"
                                val responseHeaders: MutableMap<String, String> = HashMap()
                                responseHeaders["Access-Control-Allow-Origin"] = "*"
                                response = WebResourceResponse("font/ttf", encoding, statusCode, reasonPhase, responseHeaders, inputStream)
                            } else {
                                response = WebResourceResponse("font/ttf", encoding, inputStream)
                            }
                            return response
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }
                return null
            }

            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                Timber.e("webview redirect to %s", url)
                val httpIntent = Intent(Intent.ACTION_VIEW)
                httpIntent.data = Uri.parse(url)
                httpIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(httpIntent)
                return true
            }

            override fun onReceivedError(view: WebView, errorCode: Int, description: String, failingUrl: String) {
                super.onReceivedError(view, errorCode, description, failingUrl)
                if (webViewErrorListener != null) {
                    webViewErrorListener!!.onErrorReceived(view, errorCode, description, failingUrl)
                }
            }

            @TargetApi(Build.VERSION_CODES.M)
            override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError) {
                super.onReceivedError(view, request, error)
                if (webViewErrorListener != null) {
                    webViewErrorListener!!.onErrorReceived(view, request, error)
                }
            }

            override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
                super.onReceivedSslError(view, handler, error)
                if (webViewErrorListener != null) {
                    webViewErrorListener!!.onReceivedSslError(view, handler, error)
                }
            }

            @TargetApi(Build.VERSION_CODES.M)
            override fun onReceivedHttpError(view: WebView, request: WebResourceRequest, errorResponse: WebResourceResponse) {
                super.onReceivedHttpError(view, request, errorResponse)
                if (webViewErrorListener != null) {
                    webViewErrorListener!!.onReceivedHttpError(view, request, errorResponse)
                }
            }
        }
        webChromeClient = mChromeClient
        val parameters: MutableMap<String?, String?> = HashMap()
        // the following parameters below are compulsory, make sure they are always defined
        parameters["app"] = context.packageName
        parameters["api"] = "nativeBridge"
        parameters["queue-enable"] = "0"
        if (Utils.hasFireTV(context)) {
            parameters["client_type"] = "firetv"
        } else if (Utils.hasLeanback(context)) {
            parameters["client_type"] = "androidtv"
        } else {
            parameters["client_type"] = "androidapp"
        }
        try {
            if (adInfo != null && adInfo.id != null && !adInfo.id.isEmpty()) {
                parameters["ads_device_id"] = adInfo.id
                parameters["ads_device_tracking"] = if (adInfo.isLimitAdTrackingEnabled) "0" else "1"
            }
        } catch (e: Exception) {
            Timber.e(e)
        }
        parameters.putAll(queryParameters!!)
        val builder = StringBuilder()
        builder.append(baseUrl)
        var isFirstParameter = true
        for ((key, value) in parameters) {
            if (isFirstParameter) {
                isFirstParameter = false
                builder.append('?')
            } else {
                builder.append('&')
            }
            var encodedParam: String?
            encodedParam = try {
                URLEncoder.encode(value, "UTF-8")
            } catch (e: UnsupportedEncodingException) {
                value
            }
            var encodedKey: String?
            encodedKey = try {
                URLEncoder.encode(key, "UTF-8")
            } catch (e: UnsupportedEncodingException) {
                key
            }
            builder.append(encodedKey)
            builder.append('=')
            builder.append(encodedParam)
        }
        loadUrl(builder.toString(), httpHeaders)
    }

    private fun sendCommand(command: Command) {
        when (command.methodName) {
            COMMAND_MUTE -> callPlayerMethod(if (command.params[0] as Boolean) "mute" else "unmute")
            COMMAND_CONTROLS -> callPlayerMethod("api", "controls", if (command.params[0] as Boolean) "true" else "false")
            COMMAND_QUALITY -> callPlayerMethod("api", "quality", command.params[0])
            COMMAND_SUBTITLE -> callPlayerMethod("api", "subtitle", command.params[0])
            COMMAND_TOGGLE_CONTROLS -> callPlayerMethod("api", "toggle-controls", command.params)
            COMMAND_TOGGLE_PLAY -> callPlayerMethod("api", "toggle-play", command.params)
            COMMAND_VOLUME -> callPlayerMethod("api", "volume", command.params)
            else -> callPlayerMethod(command.methodName, *command.params)
        }
    }

    private fun handleEvent(e: String) {

        /* The data we get from the api is a bit strange... */
        var e = e
        e = URLDecoder.decode(e)
        val p = e.split("&").toTypedArray()
        val map = HashMap<String, String?>()
        for (s in p) {
            val s2 = s.split("=").toTypedArray()
            if (s2.size == 1) {
                map[s2[0]] = null
            } else if (s2.size == 2) {
                map[s2[0]] = s2[1]
            } else {
                Timber.e("bad param: $s")
            }
        }
        val event = map["event"]
        if (event == null) {
            Timber.e("bad event 2: $e")
            return
        }
        if (event != "timeupdate") {
            Timber.d("[%d] event %s", hashCode(), e)
        }
        val playerEvent = eventFactory!!.createPlayerEvent(event, map, e)
        when (playerEvent.name) {
            EVENT_APIREADY -> {
                mApiReady = true
            }
            EVENT_START -> {
                isEnded = false
                mHandler!!.removeCallbacks(mLoadCommandRunnable)
                mLoadCommandRunnable = null
            }
            EVENT_END -> {
                isEnded = true
            }
            EVENT_PROGRESS -> {
                bufferedTime = map["time"]!!.toFloat().toDouble()
            }
            EVENT_TIMEUPDATE -> {
                mPosition = map["time"]!!.toFloat()
            }
            EVENT_DURATION_CHANGE -> {
                duration = map["duration"]!!.toFloat().toDouble()
            }
            EVENT_GESTURE_START -> {
                mDisallowIntercept = true
            }
            EVENT_MENU_DID_SHOW -> {
                mDisallowIntercept = true
            }
            EVENT_GESTURE_END -> {
                mDisallowIntercept = false
            }
            EVENT_MENU_DID_HIDE -> {
                mDisallowIntercept = false
            }
            EVENT_PLAY -> {
                videoPaused = false
                mPlayWhenReady = true
            }
            EVENT_PAUSE -> {
                videoPaused = true
                mPlayWhenReady = false
            }
            EVENT_AD_PLAY -> {
                mPlayWhenReady = true
            }
            EVENT_AD_PAUSE -> {
                mPlayWhenReady = false
            }
            EVENT_CONTROLSCHANGE -> {
                mHandler!!.removeCallbacks(mControlsCommandRunnable)
                mControlsCommandRunnable = null
            }
            EVENT_VOLUMECHANGE -> {
                mVolume = map["volume"]!!.toFloat()
                mHandler!!.removeCallbacks(mMuteCommandRunnable)
                mMuteCommandRunnable = null
            }
            EVENT_LOADEDMETADATA -> {
                mHasMetadata = true
            }
            EVENT_QUALITY_CHANGE -> {
                mQuality = map["quality"]
            }
            EVENT_SEEKED -> {
                isSeeking = false
                mPosition = map["time"]!!.toFloat()
            }
            EVENT_SEEKING -> {
                isSeeking = true
                mPosition = map["time"]!!.toFloat()
            }
            EVENT_PLAYBACK_READY -> {
                mHasPlaybackReady = true
            }
        }

        playerEventListener?.onEventReceived(playerEvent)

        tick()
    }

    private fun tick() {
        if (!mApiReady) {
            return
        }
        val iterator = mCommandList.iterator()
        loop@ while (iterator.hasNext()) {
            val command = iterator.next()
            when (command.methodName) {
                COMMAND_PAUSE, COMMAND_PLAY -> if (!mHasPlaybackReady) {
                    continue@loop
                }
                COMMAND_NOTIFY_LIKECHANGED -> if (!mHasMetadata) {
                    continue@loop
                }
                COMMAND_NOTIFY_WATCHLATERCHANGED -> if (!mHasMetadata) {
                    continue@loop
                }
                COMMAND_MUTE -> {
                    if (System.currentTimeMillis() - mMuteLastTime < 1000) {
                        continue@loop
                    }
                    mMuteLastTime = System.currentTimeMillis()
                }
                COMMAND_LOAD -> {
                    if (System.currentTimeMillis() - mLoadLastTime < 1000) {
                        continue@loop
                    }
                    mLoadLastTime = System.currentTimeMillis()
                }
                COMMAND_CONTROLS -> {
                    if (System.currentTimeMillis() - mControlsLastTime < 1000) {
                        continue@loop
                    }
                    mControlsLastTime = System.currentTimeMillis()
                }
            }
            iterator.remove()
            sendCommand(command)
        }
    }

    fun queueCommand(method: String, vararg params: Any) {
        /*
         * remove duplicate commands
         */
        var iterator = mCommandList.iterator()
        while (iterator.hasNext()) {
            if (iterator.next().methodName == method) {
                iterator.remove()
            }
        }

        /*
         * if we're loading a new video, cancel the stuff from before
         */
        if (method == COMMAND_LOAD) {
            mPosition = 0f
            mDisallowIntercept = false
            videoId = params[0] as String
            mHasMetadata = false
            mHasPlaybackReady = false
            iterator = mCommandList.iterator()
            while (iterator.hasNext()) {
                when (iterator.next().methodName) {
                    COMMAND_NOTIFY_LIKECHANGED, COMMAND_NOTIFY_WATCHLATERCHANGED, COMMAND_SEEK, COMMAND_PAUSE, COMMAND_PLAY -> iterator.remove()
                }
            }
        }
        val command = Command()
        command.methodName = method
        command.params = params.asList().toTypedArray()
        mCommandList.add(command)
        tick()
    }

    fun callPlayerMethod(method: String?, vararg params: Any) {
        val builder = StringBuilder()
        builder.append("javascript:player.")
        builder.append(method)
        builder.append('(')
        var count = 0
        for (o in params) {
            count++
            if (o is String) {
                builder.append("'$o'")
            } else if (o is Number) {
                builder.append(o.toString())
            } else if (o is Boolean) {
                builder.append(o.toString())
            } else {
                builder.append("JSON.parse('" + mGson!!.toJson(o) + "')")
            }
            if (count < params.size) {
                builder.append(",")
            }
        }
        builder.append(')')
        val js = builder.toString()
        loadUrl(js)
    }

    fun release() {
        loadUrl("about:blank")
        onPause()
    }

    /**
     * Notify PlayerWebView of the view visibility.
     *
     * @param visible TRUE, view is visible. FALSE otherwise.
     * @param shouldHandleTimers if TRUE, it will call resumeTimers() if visible param is TRUE and pauseTimers() if visible param is FALSE.
     * Otherwise, calls to resumeTimers() / pauseTimers() won't be made.
     * Beware pauseTimers() will pause timers for all your WebViews. If you're using more than 2, you might want to handle this separately.
     */
    fun setVisible(visible: Boolean, shouldHandleTimers: Boolean) {
        if (mVisible != visible) {
            mVisible = visible
            if (!mVisible) {
                playWhenReady = false
                // when we resume, we don't want video to start automatically
            }
            if (!mVisible) {
                onPause()
                if (shouldHandleTimers) {
                    pauseTimers()
                }
            } else {
                onResume()
                if (shouldHandleTimers) {
                    resumeTimers()
                }
            }
        }
    }

    private fun updatePlayState() {
        if (!mVisible) {
            pause()
        } else {
            if (mPlayWhenReady) {
                play()
            } else {
                pause()
            }
        }
    }

    fun setMinimizeProgress(p: Float) {
        showControls(p <= 0)
    }

    fun setIsLiked(isLiked: Boolean) {
        queueCommand(COMMAND_NOTIFY_LIKECHANGED, isLiked)
    }

    fun setIsInWatchLater(isInWatchLater: Boolean) {
        queueCommand(COMMAND_NOTIFY_WATCHLATERCHANGED, isInWatchLater)
    }

    private fun mute(mute: Boolean) {
        queueCommand(COMMAND_MUTE, mute)
    }

    fun mute() {
        mute(true)
    }

    fun unmute() {
        mute(false)
    }

    fun play() {
        queueCommand(COMMAND_PLAY)
    }

    fun pause() {
        queueCommand(COMMAND_PAUSE)
    }

    fun seek(time: Double) {
        queueCommand(COMMAND_SEEK, time)
    }

    fun showControls(visible: Boolean) {
        queueCommand(COMMAND_CONTROLS, visible)
    }

    fun setFullscreenButton(fullScreen: Boolean) {
        if (fullScreen != mIsFullScreen) {
            mIsFullScreen = fullScreen
            queueCommand(COMMAND_NOTIFYFULLSCREENCHANGED)
        }
    }

    fun setSubtitle(language_code: String?) {
        queueCommand(COMMAND_SUBTITLE, language_code!!)
    }

    fun toggleControls() {
        queueCommand(COMMAND_TOGGLE_CONTROLS)
    }

    fun togglePlay() {
        queueCommand(COMMAND_TOGGLE_PLAY)
    }

    fun setIsWebContentsDebuggingEnabled(isWebContentsDebuggingEnabled: Boolean) {
        mIsWebContentsDebuggingEnabled = isWebContentsDebuggingEnabled
    }

    fun setWebViewErrorListener(errorListener: WebViewErrorListener?) {
        webViewErrorListener = errorListener
    }

    fun setEventListener(listener: EventListener) {
        playerEventListener = listener
    }

    fun setEventListener(listener: (PlayerEvent) -> (Unit)) {
        playerEventListener = object : EventListener {
            override fun onEventReceived(event: PlayerEvent) {
                listener.invoke(event)
            }
        }
    }

    override fun loadUrl(url: String) {
        Timber.d("[%d] loadUrl %s", hashCode(), url)
        super.loadUrl(url)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (mDisallowIntercept) {
            requestDisallowInterceptTouchEvent(true)
        }
        return super.onTouchEvent(event)
    }

    private inner class JavascriptBridge {
        @JavascriptInterface
        fun triggerEvent(e: String) {
            mHandler!!.post { handleEvent(e) }
        }
    }

    internal data class Command(var methodName: String? = null,
                                var params: Array<Any> = emptyArray()) {

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Command

            if (methodName != other.methodName) return false
            if (!params.contentEquals(other.params)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = methodName?.hashCode() ?: 0
            result = 31 * result + params.contentHashCode()
            return result
        }
    }

    interface WebViewErrorListener {
        fun onErrorReceived(webView: WebView?, errorCode: Int, description: String?, failingUrl: String?)

        @RequiresApi(Build.VERSION_CODES.M)
        fun onErrorReceived(webView: WebView?, request: WebResourceRequest?, error: WebResourceError?)
        fun onReceivedSslError(webView: WebView?, handler: SslErrorHandler?, error: SslError?)

        @RequiresApi(Build.VERSION_CODES.M)
        fun onReceivedHttpError(webView: WebView?, request: WebResourceRequest?, errorResponse: WebResourceResponse?)
    }

    interface EventListener {
        fun onEventReceived(event: PlayerEvent)
    }

    companion object {

        const val EVENT_APIREADY = "apiready"
        const val EVENT_TIMEUPDATE = "timeupdate"
        const val EVENT_DURATION_CHANGE = "durationchange"
        const val EVENT_PROGRESS = "progress"
        const val EVENT_SEEKED = "seeked"
        const val EVENT_SEEKING = "seeking"
        const val EVENT_GESTURE_START = "gesture_start"
        const val EVENT_GESTURE_END = "gesture_end"
        const val EVENT_MENU_DID_SHOW = "menu_did_show"
        const val EVENT_MENU_DID_HIDE = "menu_did_hide"
        const val EVENT_VIDEO_START = "video_start"
        const val EVENT_VIDEO_END = "video_end"
        const val EVENT_AD_START = "ad_start"
        const val EVENT_AD_PLAY = "ad_play"
        const val EVENT_AD_PAUSE = "ad_pause"
        const val EVENT_AD_END = "ad_end"
        const val EVENT_AD_TIME_UPDATE = "ad_timeupdate"
        const val EVENT_ADD_TO_COLLECTION_REQUESTED = "add_to_collection_requested"
        const val EVENT_LIKE_REQUESTED = "like_requested"
        const val EVENT_WATCH_LATER_REQUESTED = "watch_later_requested"
        const val EVENT_SHARE_REQUESTED = "share_requested"
        const val EVENT_FULLSCREEN_TOGGLE_REQUESTED = "fullscreen_toggle_requested"
        const val EVENT_PLAY = "play"
        const val EVENT_PAUSE = "pause"
        const val EVENT_LOADEDMETADATA = "loadedmetadata"
        const val EVENT_PLAYING = "playing"
        const val EVENT_START = "start"
        const val EVENT_END = "end"
        const val EVENT_CONTROLSCHANGE = "controlschange"
        const val EVENT_VOLUMECHANGE = "volumechange"
        const val EVENT_QUALITY_CHANGE = "qualitychange"
        const val EVENT_QUALITIES_AVAILABLE = "qualitiesavailable"
        const val EVENT_PLAYBACK_READY = "playback_ready"
        const val EVENT_CHROME_CAST_REQUESTED = "chromecast_requested"
        const val EVENT_VIDEO_CHANGE = "videochange"
        const val COMMAND_NOTIFY_LIKECHANGED = "notifyLikeChanged"
        const val COMMAND_NOTIFY_WATCHLATERCHANGED = "notifyWatchLaterChanged"
        const val COMMAND_NOTIFYFULLSCREENCHANGED = "notifyFullscreenChanged"
        const val COMMAND_LOAD = "load"
        const val COMMAND_MUTE = "mute"
        const val COMMAND_CONTROLS = "controls"
        const val COMMAND_PLAY = "play"
        const val COMMAND_PAUSE = "pause"
        const val COMMAND_SEEK = "seek"
        const val COMMAND_SETPROP = "setProp"
        const val COMMAND_QUALITY = "quality"
        const val COMMAND_SUBTITLE = "subtitle"
        const val COMMAND_TOGGLE_CONTROLS = "toggle-controls"
        const val COMMAND_TOGGLE_PLAY = "toggle-play"
        const val COMMAND_VOLUME = "volume"

        private const val ASSETS_SCHEME = "asset://"
    }
}