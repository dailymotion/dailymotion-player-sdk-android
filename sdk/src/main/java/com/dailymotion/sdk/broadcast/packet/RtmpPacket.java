package com.dailymotion.sdk.broadcast.packet;

import android.util.SparseArray;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class RtmpPacket {
    protected RtmpHeader mHeader;

    public RtmpPacket(RtmpHeader rtmpHeader) {
        mHeader = rtmpHeader;
    }

    public RtmpPacket() {
        mHeader = new RtmpHeader();
    }

    public final void write(OutputStream outputStream, SparseArray<RtmpHeader> previousHeaders, int chunkSize) throws IOException {
        byte data[];
        data = getPayload();
        if (data == null) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            writePayload(byteArrayOutputStream);
            data = byteArrayOutputStream.toByteArray();

        }
        mHeader.dataLength = data.length;

        mHeader.write(outputStream, previousHeaders);

        int length = data.length;
        int pos = 0;
        while (length > 0) {
            int toWrite = Math.min(chunkSize, length);
            outputStream.write(data, pos, toWrite);
            pos += toWrite;
            length -= toWrite;

            if (length > 0 ) {
                outputStream.write(0xC0 | mHeader.chunkStreamId);
            }
        }
    }

    public byte[] getPayload() {
        return null;
    }
    public void writePayload(OutputStream outputStream) throws IOException {

    }
    public void decodePayload(InputStream inputStream) throws IOException {
    }

}
