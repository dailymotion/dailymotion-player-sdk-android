package com.dailymotion.sdk.broadcast;

public class BitrateMonitor {
    int mBitrate;
    long mLastNanos = -1;
    int mBytes;

    private static final long TO_NANOS = 1000L * 1000 * 1000;

    public void registerBytes(int bytes) {
        if (mLastNanos == -1){
            mLastNanos = System.nanoTime();
        }

        mBytes += bytes;

        long nowNanos = System.nanoTime();
        if (nowNanos - mLastNanos > 3L * TO_NANOS) {
            mBitrate = (int)((double)(mBytes * 8 * TO_NANOS) / (double)(nowNanos - mLastNanos));
            mBytes = 0;
            mLastNanos = nowNanos;
        }
    }


    public int getBitrate() {
        return mBitrate;
    }
}
