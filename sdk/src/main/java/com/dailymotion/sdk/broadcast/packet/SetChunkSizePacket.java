package com.dailymotion.sdk.broadcast.packet;


import com.dailymotion.sdk.broadcast.Util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SetChunkSizePacket extends RtmpPacket {
    private int mChunkSize;

    public SetChunkSizePacket() {
        mHeader.chunkStreamId = RtmpHeader.CHUNK_STREAM_ID_CONTROL;
        mHeader.messageTypeId = RtmpHeader.MESSAGE_TYPE_ID_SET_CHUNK_SIZE;
    }

    public SetChunkSizePacket(RtmpHeader header) {
        super(header);
    }

    public void setChunkSize(int chunkSize) {
        mChunkSize = chunkSize;
    }

    public int getChunkSize() {
        return mChunkSize;
    }

    public void writePayload(OutputStream outputStream) throws IOException {
        Util.writeInt32(outputStream, mChunkSize);
    }

    public void decodePayload(InputStream inputStream) throws IOException {
        mChunkSize = Util.readInt32(inputStream);
    }
}
