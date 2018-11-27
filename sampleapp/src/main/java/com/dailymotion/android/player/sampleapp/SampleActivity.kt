package com.dailymotion.android.player.sampleapp

import android.content.pm.ActivityInfo
import android.content.pm.ApplicationInfo
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout

import com.dailymotion.android.player.sdk.PlayerWebView
import com.dailymotion.android.player.sdk.events.*
import com.dailymotion.websdksample.R
import kotlinx.android.synthetic.main.new_screen_sample.*

import java.util.HashMap

class SampleActivity : AppCompatActivity(), View.OnClickListener {

    private var mFullscreen = false

    fun onFullScreenToggleRequested() {
        setFullScreenInternal(!mFullscreen)
        val params: LinearLayout.LayoutParams

        if (mFullscreen) {
            toolbar!!.visibility = View.GONE
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        } else {
            toolbar!!.visibility = View.VISIBLE
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
            params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (215 * resources.displayMetrics.density).toInt())
        }
        dm_player_web_view.layoutParams = params
    }

    private fun setFullScreenInternal(fullScreen: Boolean) {
        mFullscreen = fullScreen
        if (mFullscreen) {
            action_layout.visibility = View.GONE
        } else {
            action_layout.visibility = View.VISIBLE
        }

        dm_player_web_view.setFullscreenButton(mFullscreen)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.new_screen_sample)

        setSupportActionBar(toolbar)

        if (toolbar != null) {
            toolbar!!.visibility = View.VISIBLE
            toolbar!!.setBackgroundColor(resources.getColor(android.R.color.background_dark))
            toolbar!!.setTitleTextColor(resources.getColor(android.R.color.white))

            val actionBar = supportActionBar
            actionBar?.setTitle(getString(R.string.app_name))
        }

        if (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0) {
            dm_player_web_view.setIsWebContentsDebuggingEnabled(true)
        }

        val playerParams = HashMap<String, String>()
        dm_player_web_view.load("x26hv6c", playerParams as Map<String, Any>?)

        dm_player_web_view.setPlayerEventListener { playerEvent ->
            playerEvent?.let {
                when (it) {
                    is ApiReadyEvent -> log(it.name)
                    is StartEvent -> log(it.name)
                    is LoadedMetaDataEvent -> log(it.name)
                    is ProgressEvent -> log(it.name + " (bufferedTime: " + dm_player_web_view.bufferedTime + ")")
                    is DurationChangeEvent -> log(it.name + " (duration: " + dm_player_web_view.duration + ")")
                    is TimeUpdateEvent, is AdTimeUpdateEvent, is SeekingEvent, is SeekedEvent -> log(it.name + " (currentTime: " + dm_player_web_view.position + ")")
                    is VideoStartEvent, is AdStartEvent, is AdPlayEvent, is PlayingEvent, is EndEvent -> log(it.name + " (ended: " + dm_player_web_view.isEnded + ")")
                    is AdPauseEvent, is AdEndEvent, is VideoEndEvent, is PlayEvent, is PauseEvent -> log(it.name + " (paused: " + dm_player_web_view.videoPaused + ")")
                    is QualityChangeEvent -> log(it.name + " (quality: " + dm_player_web_view.quality + ")")
                    is VolumeChangeEvent -> log(it.name + " (volume: " + dm_player_web_view.volume + ")")
                    is FullScreenToggleRequestedEvent -> onFullScreenToggleRequested()
                    else -> {
                    }
                }
            }
        }

        val playButton = findViewById<View>(R.id.btnTogglePlay) as Button
        playButton.setOnClickListener(this@SampleActivity)
        val togglePlayButton = findViewById<View>(R.id.btnPlay) as Button
        togglePlayButton.setOnClickListener(this@SampleActivity)
        val pauseButton = findViewById<View>(R.id.btnPause) as Button
        pauseButton.setOnClickListener(this@SampleActivity)

        val seekButton = findViewById<View>(R.id.btnSeek) as Button
        seekButton.setOnClickListener(this@SampleActivity)
        val loadVideoButton = findViewById<View>(R.id.btnLoadVideo) as Button
        loadVideoButton.setOnClickListener(this@SampleActivity)
        val setQualityButton = findViewById<View>(R.id.btnSetQuality) as Button
        setQualityButton.setOnClickListener(this@SampleActivity)
        val setSubtitleButton = findViewById<View>(R.id.btnSetSubtitle) as Button
        setSubtitleButton.setOnClickListener(this@SampleActivity)

        val toggleControlsButton = findViewById<View>(R.id.btnToggleControls) as Button
        toggleControlsButton.setOnClickListener(this@SampleActivity)
        val showControlsButton = findViewById<View>(R.id.btnShowControls) as Button
        showControlsButton.setOnClickListener(this@SampleActivity)
        val hideControlsButton = findViewById<View>(R.id.btnHideControls) as Button
        hideControlsButton.setOnClickListener(this@SampleActivity)
        val setVolumeButton = findViewById<Button>(R.id.btnSetVolume)
        setVolumeButton.setOnClickListener(this@SampleActivity)
    }

    override fun onClick(v: View) {
        if (v.id == R.id.btnPlay) {
            dm_player_web_view.play()
        } else if (v.id == R.id.btnTogglePlay) {
            dm_player_web_view.togglePlay()
        } else if (v.id == R.id.btnPause) {
            dm_player_web_view.pause()
        } else if (v.id == R.id.btnSeek) {
            dm_player_web_view.seek(30.0)
        } else if (v.id == R.id.btnLoadVideo) {
            dm_player_web_view.load("x19b6ui")
        } else if (v.id == R.id.btnSetQuality) {
            dm_player_web_view.quality = "240"
        } else if (v.id == R.id.btnSetSubtitle) {
            dm_player_web_view.setSubtitle("en")
        } else if (v.id == R.id.btnToggleControls) {
            dm_player_web_view.toggleControls()
        } else if (v.id == R.id.btnShowControls) {
            dm_player_web_view.showControls(true)
        } else if (v.id == R.id.btnHideControls) {
            dm_player_web_view.showControls(false)
        } else if (v.id == R.id.btnSetVolume) {
            val text = (findViewById<View>(R.id.editTextVolume) as EditText).text.toString()
            val volume = java.lang.Float.parseFloat(text)
            dm_player_web_view.volume = volume
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
        dm_player_web_view.goBack()
    }

    override fun onPause() {
        super.onPause()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            dm_player_web_view.onPause()
        }
    }

    override fun onResume() {
        super.onResume()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            dm_player_web_view.onResume()
        }
    }

    private fun log(text: String) {
        if (action_layout.visibility == View.GONE) {
            return
        }

        logText!!.append("\n" + text)
        val scroll = logText!!.layout.getLineTop(logText!!.lineCount) - logText!!.height
        if (scroll > 0) {
            logText!!.scrollTo(0, scroll)
        } else {
            logText!!.scrollTo(0, 0)
        }
    }
}
