package com.dailymotion.android.player.sdk;

import android.content.Context;
import android.os.AsyncTask;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;

import timber.log.Timber;

/**
 * Created by hugo
 * on 05/02/2018.
 */

public class AdIdTask extends AsyncTask<Void, Void, String> {

    public interface AdIdTaskListener {
        void onResult(String result);
    }

    private Context mContext;
    private AdIdTaskListener mAdIdTaskListener;

    AdIdTask(Context context, AdIdTaskListener adIdTaskListener) {
        super();
        mContext = context;
        mAdIdTaskListener = adIdTaskListener;
    }

    @Override
    protected String doInBackground(Void... voids) {
        String result = null;

        try {
            result = AdvertisingIdClient.getAdvertisingIdInfo(mContext).getId();
        } catch (Exception e) {
            Timber.e(e);
        }

        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        mAdIdTaskListener.onResult(result);
    }
}
