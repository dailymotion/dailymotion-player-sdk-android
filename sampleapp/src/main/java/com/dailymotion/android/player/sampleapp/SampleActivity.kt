package com.dailymotion.android.player.sampleapp

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.pm.ActivityInfo
import android.content.pm.ApplicationInfo
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.webkit.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.dailymotion.android.player.sdk.PlayerWebView

import com.dailymotion.android.player.sdk.events.*
import com.dailymotion.websdksample.R
import kotlinx.android.synthetic.main.new_screen_sample.*
import java.util.concurrent.TimeUnit

class SampleActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        const val DEFAULT_VIDEO_ID = "x70val9"
    }

    private var isInFullScreen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initializeContentView()
        initializePlayer(DEFAULT_VIDEO_ID, emptyMap())
    }

    override fun onClick(v: View) {
        when (v) {

            toggleControlsButton -> playerWebView.toggleControls()
            showControlsButton -> playerWebView.showControls(true)
            hideControlsButton -> playerWebView.showControls(false)

            playButton -> playerWebView.play()
            togglePlayPauseButton -> playerWebView.togglePlay()
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

            loadVideoButton -> playerWebView.load("x19b6ui")
            subtitleButton -> playerWebView.setSubtitle("en")
            switchQualityButton -> playerWebView.quality = "240"
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
        playerWebView.goBack()
    }

    override fun onPause() {
        super.onPause()
        playerWebView.onPause()
    }

    override fun onResume() {
        super.onResume()
        playerWebView.onResume()
    }

    private fun initializeContentView() {
        setContentView(R.layout.new_screen_sample)
        setSupportActionBar(toolbar)

        logText.movementMethod = ScrollingMovementMethod()

        toolbar?.let {
            it.visibility = View.VISIBLE
            it.setBackgroundColor(ContextCompat.getColor(this, android.R.color.background_dark))
            it.setTitleTextColor(ContextCompat.getColor(this, android.R.color.white))

            val actionBar = supportActionBar
            actionBar?.title = getString(R.string.app_name)
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
        volumeEditText.setOnClickListener(this@SampleActivity)

        volumeButton.setOnClickListener(this@SampleActivity)

        loadVideoButton.setOnClickListener(this@SampleActivity)
        switchQualityButton.setOnClickListener(this@SampleActivity)
        subtitleButton.setOnClickListener(this@SampleActivity)
    }

    private fun initializePlayer(videoId: String, params: Map<String, String>) {

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

        if (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0) {
            playerWebView.setIsWebContentsDebuggingEnabled(true)
        }

        playerWebView.load(videoId = videoId, loadParams = params)

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

                is QualityChangeEvent -> log(event.name + " (quality: " + playerWebView.quality + ")")
                is VolumeChangeEvent -> log(event.name + " (volume: " + playerWebView.volume + ")")
                is FullScreenToggleRequestedEvent -> onFullScreenToggleRequested()
            }
        }
    }

    private fun setFullScreenInternal(fullScreen: Boolean) {
        isInFullScreen = fullScreen
        controlsContainerLayout.visibility = if (isInFullScreen) View.GONE else View.VISIBLE
        playerWebView.setFullscreenButton(isInFullScreen)
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private fun onFullScreenToggleRequested() {
        setFullScreenInternal(!isInFullScreen)
        val params: ConstraintLayout.LayoutParams
        if (isInFullScreen) {
            toolbar?.visibility = View.GONE
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            params = ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        } else {
            toolbar?.visibility = View.VISIBLE
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
            params = ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (215 * resources.displayMetrics.density).toInt())
        }
        playerWebView.layoutParams = params
    }

    private fun log(text: String) {
        logText.append("\n" + text)
        val scroll = logText.layout.getLineTop(logText.lineCount) - logText.height
        if (scroll > 0) {
            logText.scrollTo(0, scroll)
        } else {
            logText.scrollTo(0, 0)
        }
    }
}
