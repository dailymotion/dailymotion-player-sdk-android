package com.dailymotion.android.player.sampleapp

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.sampleapp.dailymotion.SampleActivity
import kotlinx.android.synthetic.main.new_screen_sample.*
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoadPauseTest {

    @get:Rule
    var testRule: ActivityTestRule<SampleActivity> = ActivityTestRule(SampleActivity::class.java)

    @Test
    fun test() {
        val activity = testRule.activity

        activity.runOnUiThread {
            activity.playerWebView.load("x26hv6c")
            activity.playerWebView.pause()
        }
        Thread.sleep(10000)
        activity.runOnUiThread {
            if (activity.playerWebView.position >= 1000) {
                Assert.fail("Pause() failed: ${activity.playerWebView.position}ms >= 1000ms")
            }
        }
    }
}
