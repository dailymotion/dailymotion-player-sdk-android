package com.dailymotion.android.player.sdk

import android.content.Context
import android.os.AsyncTask
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import timber.log.Timber

/**
 * Created by hugo
 * on 05/02/2018.
 */
class AdIdTask(private val mContext: Context, private val mAdIdTaskListener: AdIdTaskListener) : AsyncTask<Void?, Void?, AdvertisingIdClient.Info?>() {

    interface AdIdTaskListener {
        fun onResult(result: AdvertisingIdClient.Info?)
    }

    override fun doInBackground(vararg voids: Void?): AdvertisingIdClient.Info? {
        var info: AdvertisingIdClient.Info? = null
        try {
            info = AdvertisingIdClient.getAdvertisingIdInfo(mContext)
        } catch (e: Exception) {
            Timber.e(e)
        }
        return info
    }

    override fun onPostExecute(result: AdvertisingIdClient.Info?) {
        super.onPostExecute(result)
        mAdIdTaskListener.onResult(result)
    }

}