package com.dailymotion.android.player.sdk;

import android.content.Context;
import android.os.AsyncTask;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;

import timber.log.Timber;

/**
 * Created by hugo
 * on 05/02/2018.
 */

public class AdIdTask extends AsyncTask<Void, Void, AdvertisingIdClient.Info > {

    public interface AdIdTaskListener {
        void onResult(AdvertisingIdClient.Info  result);
    }

    private Context mContext;
    private AdIdTaskListener mAdIdTaskListener;

    AdIdTask(Context context, AdIdTaskListener adIdTaskListener) {
        super();
        mContext = context;
        mAdIdTaskListener = adIdTaskListener;
    }

    @Override
    protected AdvertisingIdClient.Info  doInBackground(Void... voids) {
        AdvertisingIdClient.Info info = null;

        try {
            info = AdvertisingIdClient.getAdvertisingIdInfo(mContext);

        } catch (Exception e) {
            Timber.e(e);
        }

        return info;
    }

    @Override
    protected void onPostExecute(AdvertisingIdClient.Info  result) {
        super.onPostExecute(result);

        mAdIdTaskListener.onResult(result);
    }
}
