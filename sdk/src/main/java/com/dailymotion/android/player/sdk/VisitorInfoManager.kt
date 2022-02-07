package com.dailymotion.android.player.sdk

import android.content.Context
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import timber.log.Timber

class VisitorInfoManager {

    private var advertisingInfo: AdvertisingIdClient.Info? = null
    private val mutex = Mutex()

    suspend fun getAdvertisingInfo(context: Context): AdvertisingIdClient.Info? = mutex.withLock {
        if (advertisingInfo?.id.isNullOrBlank()) {
            advertisingInfo = withContext(Dispatchers.IO) {
                try {
                    AdvertisingIdClient.getAdvertisingIdInfo(context)
                } catch (e: Exception) {
                    Timber.e(e)
                    null
                }
            }
        }
        return advertisingInfo
    }
}