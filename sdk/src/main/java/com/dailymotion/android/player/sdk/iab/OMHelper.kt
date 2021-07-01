package com.dailymotion.android.player.sdk.iab

import android.content.Context
import com.dailymotion.android.BuildConfig
import com.dailymotion.android.player.sdk.PlayerWebView
import com.dailymotion.android.player.sdk.events.*
import com.iab.omid.library.dailymotion.Omid
import com.iab.omid.library.dailymotion.adsession.*
import com.iab.omid.library.dailymotion.adsession.media.*
import timber.log.Timber
import java.net.URL
import java.util.regex.Pattern


object OMHelper {

    private const val PARTNER_NAME = "dailymotion"

    private var omidSession: AdSession? = null
    private var omidAdEvents: AdEvents? = null
    private var omidMediaEvents: MediaEvents? = null
    private var omidCurrentPosition: Quartile? = null
    private var adDuration = 1f

    enum class Quartile(
        val progress: Float,
        val nextStep: Quartile? = null,
        val action: ((MediaEvents) -> Unit)? = null
    ) {
        Q3(0.75f, null, { it.thirdQuartile(); logOmidAction("thirdQuartile") }),
        Q2(0.50f, Q3, { it.midpoint(); logOmidAction("midpoint") }),
        Q1(0.25f, Q2, { it.firstQuartile(); logOmidAction("firstQuartile") }),
        START(0f, Q1, { it.start(adDuration, 1f); logOmidAction("start duration=$adDuration") }),
        INIT(0f, START)
    }

    /**
     * Ensure Omid SDK is initialized
     */
    internal fun ensureInitialized(context: Context) {
        if (!Omid.isActive()) {
            Omid.activate(context.applicationContext)
        }
    }

    internal fun onPlayerEvent(playerWebView: PlayerWebView, playerEvent: PlayerEvent) {
        when (playerEvent) {
            is AdLoadedEvent -> {
                if(omidSession != null){
                    endOmidSession()
                }
                createOmidSession(playerWebView, playerEvent.payload)

                try {
                    omidAdEvents?.apply {
                        impressionOccurred()
                        logOmidAction("Impression occured")
                    }
                } catch (e: Exception) {
                    omidSession?.error(ErrorType.GENERIC, e.localizedMessage)
                    logOmidAction("Error ${e.localizedMessage}")
                }

                val position = try {
                    Position.valueOf(playerEvent.position?.uppercase().orEmpty())
                } catch (e: Exception) {
                    logError("Incorrect Position")
                    return
                }

                val properties = if(playerEvent.skipOffset > 0) VastProperties.createVastPropertiesForSkippableMedia(
                    playerEvent.skipOffset,
                    playerEvent.autoPlay,
                    position
                )else{
                    VastProperties.createVastPropertiesForNonSkippableMedia(playerEvent.autoPlay, position)
                }

                try {
                    omidAdEvents?.apply {
                        loaded(properties)
                        logOmidAction("Loaded ${properties.isAutoPlay}/${properties.isSkippable}/${properties.position}/${properties.skipOffset}")
                    }
                } catch (e: Exception) {
                    omidSession?.error(ErrorType.GENERIC, e.localizedMessage)
                    logOmidAction("Error ${e.localizedMessage}")
                }
            }
            is AdStartEvent -> {
                adDuration = playerEvent.adDuration.takeIf { it != 0f } ?: 1f
                startOmidSession()
            }
            is AdEndEvent -> {
                try {
                    when (playerEvent.reason) {
                        "AD_STOPPED" -> {
                            omidMediaEvents?.apply {
                                complete()
                                logOmidAction("Complete")
                            }
                        }
                        "AD_SKIPPED" -> {
                            omidMediaEvents?.apply {
                                skipped()
                                logOmidAction("Skipped")
                            }
                        }
                        "AD_ERROR" -> {
                            omidSession?.error(ErrorType.VIDEO, playerEvent.error ?: "AD_ERROR")
                            logOmidAction("Error ${playerEvent.error ?: "AD_ERROR"}")
                        }
                    }
                    endOmidSession()
                } catch (e: Exception) {
                    omidSession?.error(ErrorType.GENERIC, e.localizedMessage)
                    logOmidAction("Error ${e.localizedMessage}")
                }
            }
            is AdPauseEvent -> {
                try {
                    omidMediaEvents?.apply {
                        pause()
                        logOmidAction("pause")
                    }
                } catch (e: Exception) {
                    omidSession?.error(ErrorType.GENERIC, e.localizedMessage)
                    logOmidAction("Error ${e.localizedMessage}")
                }
            }
            is AdResumeEvent -> {
                try {
                    omidMediaEvents?.apply {
                        resume()
                        logOmidAction("resume")
                    }
                } catch (e: Exception) {
                    omidSession?.error(ErrorType.GENERIC, e.localizedMessage)
                    logOmidAction("Error ${e.localizedMessage}")
                }
            }
            is AdBufferStartEvent -> {
                try {
                    omidMediaEvents?.apply {
                        bufferStart()
                        logOmidAction("bufferStart")
                    }
                } catch (e: Exception) {
                    omidSession?.error(ErrorType.GENERIC, e.localizedMessage)
                    logOmidAction("Error ${e.localizedMessage}")
                }
            }
            is AdBufferEndEvent -> {
                try {
                    omidMediaEvents?.apply {
                        bufferFinish()
                        logOmidAction("bufferEnd")
                    }
                } catch (e: Exception) {
                    omidSession?.error(ErrorType.GENERIC, e.localizedMessage)
                }
            }
            is AdClickEvent -> {
                try {
                    omidMediaEvents?.apply {
                        adUserInteraction(InteractionType.CLICK)
                        logOmidAction("adUserInteraction Click")
                    }
                } catch (e: Exception) {
                    omidSession?.error(ErrorType.GENERIC, e.localizedMessage)
                    logOmidAction("Error ${e.localizedMessage}")
                }
            }
            is VolumeChangeEvent -> {
                try {
                    omidMediaEvents?.apply {
                        volumeChange(if (playerEvent.isMuted) 0f else 1f)
                        logOmidAction("volumeChange ${if (playerEvent.isMuted) 0f else 1f}")
                    }
                } catch (e: Exception) {
                    omidSession?.error(ErrorType.GENERIC, e.localizedMessage)
                    logOmidAction("Error ${e.localizedMessage}")
                }
            }
            is FullScreenChangeEvent -> {
                /** We might remove this step if we force the partner to implement our API
                 * to set up the player state on his own **/
                try {
                    omidMediaEvents?.apply {
                        playerStateChange(if (playerEvent.fullscreen) PlayerState.FULLSCREEN else PlayerState.NORMAL)
                        logOmidAction("playerStateChange ${if (playerEvent.fullscreen) PlayerState.FULLSCREEN else PlayerState.NORMAL}")
                    }
                } catch (e: Exception) {
                    omidSession?.error(ErrorType.GENERIC, e.localizedMessage)
                    logOmidAction("Error ${e.localizedMessage}")
                }
            }
            is AdTimeUpdateEvent -> {
                val progress = (playerEvent.time?.toDoubleOrNull() ?: 0.0) / adDuration
                val nextPosition = omidCurrentPosition?.nextStep ?: return
                if (progress > nextPosition.progress) {
                    omidCurrentPosition = nextPosition
                    try {
                        omidMediaEvents?.let { omidCurrentPosition?.action?.invoke(it) }
                    } catch (e: Exception) {
                        omidSession?.error(ErrorType.GENERIC, e.localizedMessage)
                        logOmidAction("Error ${e.localizedMessage}")
                    }
                }
            }
        }
    }

    /**
     * Create and setup an Omid AdSession
     * @param playerWebView the host
     * @param payload coming from the event which allow to setup the Omid session
     */
    private fun createOmidSession(playerWebView: PlayerWebView, payload: String?) {

        val verificationScriptsList = parseVerificationScriptData(payload)
        val verificationScriptResourceList = try {
            verificationScriptsList.map {
                VerificationScriptResource.createVerificationScriptResourceWithParameters(
                    it.vendorKey,
                    URL(it.url),
                    it.parameters
                )
            }
        }catch (e: IllegalArgumentException){
            logError("Error while creating verificationScriptResourceList", e)
            return
        }

        val partner = try {
            Partner.createPartner(PARTNER_NAME, BuildConfig.SDK_VERSION)
        } catch (e: java.lang.IllegalArgumentException) {
            logError("Error while creating partner", e)
            return
        }

        val adSessionContext = AdSessionContext.createNativeAdSessionContext(
            partner,
            OmidJsLoader.getOmidJs(playerWebView.context),
            verificationScriptResourceList,
            null,
            null
        )

        val adSessionConfiguration = try {
            AdSessionConfiguration.createAdSessionConfiguration(
                CreativeType.VIDEO, ImpressionType.ONE_PIXEL,
                Owner.NATIVE, Owner.NATIVE, true
            )
        } catch (e: IllegalArgumentException) {
            logError("Error while creating adSessionConfiguration", e)
            return
        }

        omidSession = AdSession.createAdSession(adSessionConfiguration, adSessionContext)
        omidSession?.registerAdView(playerWebView)

        omidAdEvents = AdEvents.createAdEvents(omidSession)
        omidMediaEvents = MediaEvents.createMediaEvents(omidSession)
        omidCurrentPosition = Quartile.INIT
    }

    /**
     * End Omid session
     */
    internal fun endOmidSession() {
        omidSession?.apply {
            finish()
            logOmidAction("Session End")
        }
        omidSession = null
        omidAdEvents = null
        omidMediaEvents = null
        omidCurrentPosition = null
        adDuration = 1f
    }

    /**
     * Start Omid session
     */
    private fun startOmidSession() {
        omidSession?.apply {
            start()
            logOmidAction("Session Start")
        }
    }

    /**
     * Parse the verificationScriptsList from the payload of AdLoadedEvent
     */
    private fun parseVerificationScriptData(payload: String?): List<VerificationScriptData> {
        return payload
            ?.split("&")
            ?.groupBy {
                val m = Pattern.compile("verificationScripts\\[(.*)]\\[(.*)]=(.*)").matcher(it)
                if (m.matches()) m.group(1) else null
            }
            ?.filterKeys { it != null }
            ?.map { group ->
                VerificationScriptData(
                    vendorKey = group.value.find { it.contains("verificationScripts[${group.key}][vendor]") }
                        ?.split("=")?.get(1) ?: "",
                    url = group.value.find { it.contains("verificationScripts[${group.key}][resource]") }
                        ?.split("=")?.get(1) ?: "",
                    parameters = group.value.find { it.contains("verificationScripts[${group.key}][parameters]") }
                        ?.split("=")?.get(1) ?: ""
                )
            }
            ?: emptyList()
    }

    private fun logError(error: String, exception: Exception? = null) {
        Timber.e(exception, "OMSDK: ERROR : $error")
    }

    private fun logOmidAction(message: String) {
        Timber.d("OMSDK: $message")
    }
}