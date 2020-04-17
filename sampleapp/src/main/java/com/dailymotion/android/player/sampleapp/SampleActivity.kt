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
import android.widget.EditText
import android.widget.LinearLayout
import android.webkit.*
import androidx.core.content.ContextCompat
import com.dailymotion.android.player.sdk.PlayerWebView

import com.dailymotion.android.player.sdk.events.*
import com.dailymotion.websdksample.R
import kotlinx.android.synthetic.main.new_screen_sample.*

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
            btnPlay -> playerWebview.play()
            btnTogglePlay -> playerWebview.togglePlay()
            btnPause -> playerWebview.pause()
            btnSeek -> playerWebview.seek(30.0)
            btnLoadVideo -> playerWebview.load("x19b6ui")
            btnSetQuality -> playerWebview.quality = "240"
            btnSetSubtitle -> playerWebview.setSubtitle("en")
            btnToggleControls -> playerWebview.toggleControls()
            btnShowControls -> playerWebview.showControls(true)
            btnHideControls -> playerWebview.showControls(false)
            btnSetVolume -> {
                val text = (findViewById<View>(R.id.editTextVolume) as EditText).text.toString()
                val volume = java.lang.Float.parseFloat(text)
                playerWebview.volume = volume
            }
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
        playerWebview.goBack()
    }

    override fun onPause() {
        super.onPause()
        playerWebview.onPause()
    }

    override fun onResume() {
        super.onResume()
        playerWebview.onResume()
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

        btnTogglePlay.setOnClickListener(this@SampleActivity)
        btnPlay.setOnClickListener(this@SampleActivity)
        btnPause.setOnClickListener(this@SampleActivity)
        btnSeek.setOnClickListener(this@SampleActivity)
        btnLoadVideo.setOnClickListener(this@SampleActivity)
        btnSetQuality.setOnClickListener(this@SampleActivity)
        btnSetSubtitle.setOnClickListener(this@SampleActivity)
        btnToggleControls.setOnClickListener(this@SampleActivity)
        btnShowControls.setOnClickListener(this@SampleActivity)
        btnHideControls.setOnClickListener(this@SampleActivity)
        btnSetVolume.setOnClickListener(this@SampleActivity)
    }

    private fun initializePlayer(videoId: String, params: Map<String, String>) {

        /* Plug our listener so we can listen to WebView errors */
        playerWebview.setWebViewErrorListener(object : PlayerWebView.WebViewErrorListener {
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
            playerWebview.setIsWebContentsDebuggingEnabled(true)
        }

        playerWebview.load(videoId = videoId, loadParams = params)

        playerWebview.setEventListener { event ->
            when (event) {
                is ApiReadyEvent -> log(event.name)
                is StartEvent -> log(event.name)
                is LoadedMetaDataEvent -> log(event.name)
                is ProgressEvent -> log(event.name + " (bufferedTime: " + playerWebview.bufferedTime + ")")
                is DurationChangeEvent -> log(event.name + " (duration: " + playerWebview.duration + ")")

                is TimeUpdateEvent,
                is AdTimeUpdateEvent,
                is SeekingEvent,
                is SeekedEvent -> log(event.name + " (currentTime: " + playerWebview.position + ")")

                is VideoStartEvent,
                is AdStartEvent,
                is AdPlayEvent,
                is PlayingEvent,
                is EndEvent -> log(event.name + " (ended: " + playerWebview.isEnded + ")")

                is AdPauseEvent,
                is AdEndEvent,
                is VideoEndEvent,
                is PlayEvent,
                is PauseEvent -> log(event.name + " (paused: " + playerWebview.videoPaused + ")")

                is QualityChangeEvent -> log(event.name + " (quality: " + playerWebview.quality + ")")
                is VolumeChangeEvent -> log(event.name + " (volume: " + playerWebview.volume + ")")
                is FullScreenToggleRequestedEvent -> onFullScreenToggleRequested()
            }
        }
    }

    private fun setFullScreenInternal(fullScreen: Boolean) {
        isInFullScreen = fullScreen
        action_layout.visibility = if (isInFullScreen) View.GONE else View.VISIBLE
        playerWebview.setFullscreenButton(isInFullScreen)
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private fun onFullScreenToggleRequested() {
        setFullScreenInternal(!isInFullScreen)
        val params: LinearLayout.LayoutParams
        if (isInFullScreen) {
            toolbar?.visibility = View.GONE
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        } else {
            toolbar?.visibility = View.VISIBLE
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
            params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (215 * resources.displayMetrics.density).toInt())
        }
        playerWebview.layoutParams = params
    }

    private fun log(text: String) {
        if (action_layout.visibility == View.GONE) {
            return
        }

        logText.append("\n" + text)
        val scroll = logText.layout.getLineTop(logText.lineCount) - logText.height
        if (scroll > 0) {
            logText.scrollTo(0, scroll)
        } else {
            logText.scrollTo(0, 0)
        }
    }
}
