package com.dailymotion.android.player.sampleapp

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.pm.ActivityInfo
import android.content.pm.ApplicationInfo
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.webkit.*
import com.dailymotion.android.player.sdk.PlayerWebView

import com.dailymotion.android.player.sdk.events.*
import com.dailymotion.websdksample.R
import kotlinx.android.synthetic.main.new_screen_sample.*
import java.util.*

class SampleActivity : AppCompatActivity(), View.OnClickListener {

    private var mFullscreen = false

    @SuppressLint("SourceLockedOrientationActivity")
    private fun onFullScreenToggleRequested() {
        setFullScreenInternal(!mFullscreen)
        val params: LinearLayout.LayoutParams
        if (mFullscreen) {
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

    private fun setFullScreenInternal(fullScreen: Boolean) {
        mFullscreen = fullScreen
        action_layout.visibility = if (mFullscreen) View.GONE else View.VISIBLE
        playerWebview.setFullscreenButton(mFullscreen)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.new_screen_sample)

        setSupportActionBar(toolbar)

        playerWebview.setWebViewErrorListener(object: PlayerWebView.WebViewErrorListener {

            override fun onErrorReceived(webView: WebView?, errorCode: Int, description: String?, failingUrl: String?) {
                val message = "WebView [${webView.hashCode()}] received an error with code: $errorCode, description: $description from URL: $failingUrl"
                log(message)
            }

            @TargetApi(Build.VERSION_CODES.M)
            override fun onErrorReceived(webView: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                val message = "WebView [${webView.hashCode()}] received an error with code: ${error?.errorCode}, description: ${error?.description}from URL: ${request?.url?.toString()}"
                log(message)
            }

            override fun onReceivedSslError(webView: WebView?, handler: SslErrorHandler?, error: SslError?) {
                val message = "WebView [${webView.hashCode()}] received an SSL error with primaryCode: ${error?.primaryError}"
                log(message)
            }

            @TargetApi(Build.VERSION_CODES.M)
            override fun onReceivedHttpError(webView: WebView?, request: WebResourceRequest?, errorResponse: WebResourceResponse?) {
                val message = "WebView [${webView.hashCode()}] received an HTTP error with statusCode: ${errorResponse?.statusCode}"
                log(message)
            }
        })

        @Suppress("DEPRECATION")
        toolbar?.let {
            it.visibility = View.VISIBLE
            it.setBackgroundColor(resources.getColor(android.R.color.background_dark))
            it.setTitleTextColor(resources.getColor(android.R.color.white))

            val actionBar = supportActionBar
            actionBar?.title = getString(R.string.app_name)
        }

        if (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0) {
            playerWebview.setIsWebContentsDebuggingEnabled(true)
        }

        val playerParams = HashMap<String, String>()
        playerWebview.load(videoId = "x70val9", loadParams = playerParams)

        playerWebview.playerEventListener = { playerEvent ->
            when (playerEvent) {

                is ApiReadyEvent -> log(playerEvent.name)
                is StartEvent -> log(playerEvent.name)
                is LoadedMetaDataEvent -> log(playerEvent.name)
                is ProgressEvent -> log(playerEvent.name + " (bufferedTime: " + playerWebview.bufferedTime + ")")
                is DurationChangeEvent -> log(playerEvent.name + " (duration: " + playerWebview.duration + ")")

                is TimeUpdateEvent,
                is AdTimeUpdateEvent,
                is SeekingEvent,
                is SeekedEvent -> log(playerEvent.name + " (currentTime: " + playerWebview.position + ")")

                is VideoStartEvent,
                is AdStartEvent,
                is AdPlayEvent,
                is PlayingEvent,
                is EndEvent -> log(playerEvent.name + " (ended: " + playerWebview.isEnded + ")")

                is AdPauseEvent,
                is AdEndEvent,
                is VideoEndEvent,
                is PlayEvent,
                is PauseEvent -> log(playerEvent.name + " (paused: " + playerWebview.videoPaused + ")")

                is QualityChangeEvent -> log(playerEvent.name + " (quality: " + playerWebview.quality + ")")
                is VolumeChangeEvent -> log(playerEvent.name + " (volume: " + playerWebview.volume + ")")
                is FullScreenToggleRequestedEvent -> onFullScreenToggleRequested()
            }
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
