package com.dailymotion.sdk.broadcast.packet;

import com.dailymotion.sdk.broadcast.Util;

import java.io.IOException;
import java.io.OutputStream;

public class VideoPacket extends RtmpPacket {
    byte[] mData;
    boolean mIsHeader;
    boolean mIsKey;

    public VideoPacket(byte[] data, boolean isHeader, long pts, boolean isKey) {
        mHeader.chunkStreamId = RtmpHeader.CHUNK_STREAM_DATA;
        mHeader.messageTypeId = RtmpHeader.MESSAGE_TYPE_ID_VIDEO;
        mHeader.messageStreamId = RtmpHeader.MESSAGE_STREAM_ID;
        mHeader.timestamp = pts;
        mData = data;
        mIsHeader = isHeader;
        mIsKey = isKey;
    }

    @Override
    public void writePayload(OutputStream outputStream) throws IOException {
        int firstByte = mIsKey ? 0x17:0x27;
        outputStream.write((byte)firstByte);
        outputStream.write(mIsHeader ? 0 : 1);
        // composition time offset
        Util.writeInt24(outputStream, 0);
        outputStream.write(mData);
    }
}
