package com.dailymotion.android.player.sampleapp

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.DialogInterface
import android.content.pm.ActivityInfo
import android.content.pm.ApplicationInfo
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import com.dailymotion.android.player.sdk.PlayerWebView
import com.dailymotion.android.player.sdk.events.*
import com.dailymotion.websdksample.BuildConfig
import com.dailymotion.websdksample.R
import kotlinx.android.synthetic.main.new_screen_sample.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class SampleActivity : AppCompatActivity(), View.OnClickListener {

    companion object {

        const val DEFAULT_VERTICAL_VIDEO_ID = "x7h2j4q"
        const val DEFAULT_HORIZONTAL_VIDEO_ID = "x70val9"
        const val DEFAULT_PLAYLIST_ID = "x5zhzj"
        const val DEFAULT_SCALE_MODE = "fit"
        const val DEFAULT_QUALITY = "240"
        const val DEFAULT_VOLUME_VALUE = "1"
        const val DEFAULT_SEEK_VALUE_SEC = "30"
    }

    private var scaleModeList = listOf(
            "fit",
            "fill"
    )
    private var selectedScaleMode = DEFAULT_SCALE_MODE

    private var isPlayerFullscreen = false
    private var videoAvailableQuality = emptyList<String>()
    private var selectedQuality = DEFAULT_QUALITY

    private var isLogFullScreen = false
    private var keepScrollBottom = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initializeContentView()
        initializePlayer(mapOf("video" to videoIdEditText.text.toString(),
                "playlist" to playlistIdEditText.text.toString(),
                /* Set it to true because the default value is false */
                "queue-enable" to "true"))
    }

    override fun onClick(v: View) {
        when (v) {

            toggleControlsButton -> playerWebView.toggleControls()
            showControlsButton -> playerWebView.showControls(true)
            hideControlsButton -> playerWebView.showControls(false)

            togglePlayPauseButton -> playerWebView.togglePlay()
            playButton -> playerWebView.play()
            pauseButton -> playerWebView.pause()

            seekForwardButton -> {
                val seekValueSec = seekEditText.text.toString().toDouble()
                val positionInMs = playerWebView.position
                val finalPositionSec = TimeUnit.MILLISECONDS.toSeconds(positionInMs) + seekValueSec
                playerWebView.seek(finalPositionSec)
            }
            seekBackwardButton -> {
                val seekValueSec = seekEditText.text.toString().toDouble()
                val positionInMs = playerWebView.position
                var finalPositionSec = TimeUnit.MILLISECONDS.toSeconds(positionInMs) - seekValueSec
                if (finalPositionSec < 0) {
                    finalPositionSec = 0.0
                }
                playerWebView.seek(finalPositionSec)
            }

            muteButton -> playerWebView.mute()
            unMuteButton -> playerWebView.unmute()
            volumeButton -> {
                val value = volumeEditText.text.toString().toFloat()
                playerWebView.volume = value
            }

            loadVideoButton -> {
                val params = mutableMapOf("video" to videoIdEditText.text.toString())
                /* queue-enable must be set to TRUE at the FIRST load call otherwise you won't be able to see the playlist queue */
                val playlistId = playlistIdEditText.text.toString()
                if (playlistId.isNotEmpty()) {
                    params["playlist"] = playlistId
                }
                playerWebView.load(params)
            }

            scaleModeEditText -> {
                /* Be sure to load a vertical video to see correctly the scale mode effect. For instance, load DEFAULT_VERTICAL_VIDEO_ID */
                AlertDialog.Builder(this@SampleActivity)
                        .setTitle(getString(R.string.apply_scale_mode))
                        .setItems(scaleModeList.toTypedArray()) { _: DialogInterface, pos: Int ->
                            selectedScaleMode = scaleModeList[pos]
                            scaleModeEditText.setText(selectedScaleMode)
                            playerWebView.scaleMode(selectedScaleMode)
                        }
                        .create().show()
            }

            qualityEditText -> {
                val availableQualities = videoAvailableQuality.toTypedArray()
                AlertDialog.Builder(this@SampleActivity)
                        .setTitle(getString(R.string.select_video_quality))
                        .setItems(availableQualities) { _: DialogInterface, pos: Int ->
                            selectedQuality = availableQualities[pos]
                            qualityEditText.setText(selectedQuality)
                            playerWebView.quality = selectedQuality
                        }
                        .create().show()
            }

            logFullScreenButton -> toggleLogFullScreen()
            logScrollBottom -> toggleLogScrollBottom()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.sample, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return true
    }

    override fun onBackPressed() {
        when {
            isPlayerFullscreen -> onFullScreenToggleRequested()
            playerWebView.canGoBack() -> playerWebView.goBack()
            else -> finish()
        }
    }

    private fun initializeContentView() {
        setContentView(R.layout.new_screen_sample)
        setSupportActionBar(toolbar)

        seekEditText.setText(DEFAULT_SEEK_VALUE_SEC)
        videoIdEditText.setText(DEFAULT_HORIZONTAL_VIDEO_ID)
        playlistIdEditText.setText(DEFAULT_PLAYLIST_ID)
        volumeEditText.setText(DEFAULT_VOLUME_VALUE)
        qualityEditText.setText(DEFAULT_QUALITY)
        scaleModeEditText.setText(DEFAULT_SCALE_MODE)

        logText.movementMethod = ScrollingMovementMethod()

        toolbar?.let {
            it.visibility = View.VISIBLE
            it.setBackgroundColor(ContextCompat.getColor(this, android.R.color.background_dark))
            it.setTitleTextColor(ContextCompat.getColor(this, android.R.color.white))

            val actionBar = supportActionBar
            actionBar?.title = "${getString(R.string.app_name)} v${BuildConfig.PLAYER_SDK_VERSION}"
        }

        toggleControlsButton.setOnClickListener(this@SampleActivity)
        showControlsButton.setOnClickListener(this@SampleActivity)
        hideControlsButton.setOnClickListener(this@SampleActivity)

        toggleControlsButton.setOnClickListener(this@SampleActivity)
        showControlsButton.setOnClickListener(this@SampleActivity)
        hideControlsButton.setOnClickListener(this@SampleActivity)

        togglePlayPauseButton.setOnClickListener(this@SampleActivity)
        playButton.setOnClickListener(this@SampleActivity)
        pauseButton.setOnClickListener(this@SampleActivity)

        seekForwardButton.setOnClickListener(this@SampleActivity)
        seekBackwardButton.setOnClickListener(this@SampleActivity)

        muteButton.setOnClickListener(this@SampleActivity)
        unMuteButton.setOnClickListener(this@SampleActivity)
        volumeButton.setOnClickListener(this@SampleActivity)

        loadVideoButton.setOnClickListener(this@SampleActivity)

        scaleModeEditText.setOnClickListener(this@SampleActivity)

        qualityEditText.setOnClickListener(this@SampleActivity)

        logFullScreenButton.setOnClickListener(this@SampleActivity)
        logScrollBottom.setOnClickListener(this@SampleActivity)
    }

    private fun initializePlayer(params: Map<String, String>) {

        if (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0) {
            playerWebView.setIsWebContentsDebuggingEnabled(true)
        }

        /* Plug our listener so we can listen to WebView errors */
        playerWebView.setWebViewErrorListener(object : PlayerWebView.WebViewErrorListener {
            override fun onErrorReceived(webView: WebView?, errorCode: Int, description: String?, failingUrl: String?) {
                log("WebView [${webView.hashCode()}] received an error with code: $errorCode, description: $description from URL: $failingUrl")
            }

            @TargetApi(Build.VERSION_CODES.M)
            override fun onErrorReceived(webView: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                log("WebView [${webView.hashCode()}] received an error with code: ${error?.errorCode}, description: ${error?.description}from URL: ${request?.url?.toString()}")
            }

            override fun onReceivedSslError(webView: WebView?, handler: SslErrorHandler?, error: SslError?) {
                log("WebView [${webView.hashCode()}] received an SSL error with primaryCode: ${error?.primaryError}")
            }

            @TargetApi(Build.VERSION_CODES.M)
            override fun onReceivedHttpError(webView: WebView?, request: WebResourceRequest?, errorResponse: WebResourceResponse?) {
                log("WebView [${webView.hashCode()}] received an HTTP error with statusCode: ${errorResponse?.statusCode}")
            }
        })

        playerWebView.setEventListener { event ->
            when (event) {
                is ApiReadyEvent -> log(event.name)
                is StartEvent -> log(event.name)
                is LoadedMetaDataEvent -> log(event.name)
                is ProgressEvent -> log(event.name + " (bufferedTime: " + playerWebView.bufferedTime + ")")
                is DurationChangeEvent -> log(event.name + " (duration: " + playerWebView.duration + ")")

                is TimeUpdateEvent,
                is AdTimeUpdateEvent,
                is SeekingEvent,
                is SeekedEvent -> log(event.name + " (currentTime: " + playerWebView.position + ")")

                is VideoStartEvent,
                is AdStartEvent,
                is AdPlayEvent,
                is PlayingEvent,
                is EndEvent -> log(event.name + " (ended: " + playerWebView.isEnded + ")")

                is AdPauseEvent,
                is AdEndEvent,
                is VideoEndEvent,
                is PlayEvent,
                is PauseEvent -> log(event.name + " (paused: " + playerWebView.videoPaused + ")")

                is QualitiesAvailableEvent -> videoAvailableQuality = event.qualities ?: listOf(DEFAULT_QUALITY)
                is QualityChangeEvent -> log(event.name + " (quality: " + playerWebView.quality + ")")
                is VolumeChangeEvent -> log(event.name + " (volume: " + playerWebView.volume + ")")
                is FullScreenToggleRequestedEvent -> onFullScreenToggleRequested()
            }
        }

        playerWebView.load(params = params)
    }

    private fun setFullScreenInternal(fullScreen: Boolean) {
        isPlayerFullscreen = fullScreen
        controlsContainerLayout.visibility = if (isPlayerFullscreen) View.GONE else View.VISIBLE
        playerWebView.setFullscreenButton(isPlayerFullscreen)
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private fun onFullScreenToggleRequested() {
        setFullScreenInternal(!isPlayerFullscreen)

        if (isPlayerFullscreen) {
            toolbar?.visibility = View.GONE
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            playerWebView.layoutParams = ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

        } else {
            toolbar?.visibility = View.VISIBLE
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
            playerWebView.layoutParams = ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (215 * resources.displayMetrics.density).toInt())

            val constraintSet = ConstraintSet()
            constraintSet.clone(rootConstraintLayout)
            constraintSet.connect(R.id.playerWebView, ConstraintSet.TOP, R.id.toolbar, ConstraintSet.BOTTOM, 0)
            constraintSet.applyTo(rootConstraintLayout)
        }
    }

    private fun toggleLogFullScreen() {
        isLogFullScreen = !isLogFullScreen

        if (isLogFullScreen) {
            controlsContainerLayout.visibility = View.GONE
            val height = scrollContainerLayout.height
            logControlsContainerLayout.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height)

        } else {
            controlsContainerLayout.visibility = View.VISIBLE
            logControlsContainerLayout.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, resources.getDimensionPixelSize(R.dimen.log_default_height))
        }
    }

    private fun toggleLogScrollBottom() {
        keepScrollBottom = !keepScrollBottom
        if (keepScrollBottom) {
            logScrollBottom.setImageResource(R.drawable.ic_scroll_bottom_toggled)
        } else {
            logScrollBottom.setImageResource(R.drawable.ic_scroll_bottom)
        }
    }

    private fun log(text: String) {
        val timestamp = SimpleDateFormat.getTimeInstance().format(Date())
        logText.append("\n$timestamp: $text")

        if (keepScrollBottom) {
            logText.layout?.let {
                val scroll = it.getLineTop(logText.lineCount) - logText.height
                if (scroll > 0) {
                    logText.scrollTo(0, scroll)
                } else {
                    logText.scrollTo(0, 0)
                }
            }
        }
    }
}
