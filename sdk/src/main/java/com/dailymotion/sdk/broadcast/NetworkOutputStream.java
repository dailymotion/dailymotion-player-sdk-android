package com.dailymotion.sdk.broadcast;

import java.io.IOException;
import java.io.OutputStream;

public class NetworkOutputStream extends OutputStream {

    private final OutputStream mOutputStream;
    private BitrateMonitor mMonitor;

    NetworkOutputStream(OutputStream out) {
        mOutputStream = out;
        mMonitor = new BitrateMonitor();
    }

    @Override
    public void write(int oneByte) throws IOException {
        mMonitor.registerBytes(1);
        mOutputStream.write(oneByte);
    }

    @Override
    public void write(byte[] buffer, int offset, int count) throws IOException {
        mMonitor.registerBytes(count);
        mOutputStream.write(buffer, offset, count);
    }

    public void close() throws IOException {
        mOutputStream.close();
    }

    public void flush() throws IOException {
        mOutputStream.flush();
    }

    public int getBitrate() {
        return mMonitor.getBitrate();
    }

}
