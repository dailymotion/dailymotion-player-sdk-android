package com.dailymotion.sdk.broadcast.packet;

import java.io.IOException;
import java.io.OutputStream;

public class AudioPacket extends RtmpPacket {
    byte[] mData;
    boolean mIsAudioSpecificConfic;
    private byte mFirstByte;

    public AudioPacket(byte[] data, boolean isAudioSpecificConfic, long pts, byte firstByte) {
        mHeader.chunkStreamId = RtmpHeader.CHUNK_STREAM_DATA;
        mHeader.messageTypeId = RtmpHeader.MESSAGE_TYPE_ID_AUDIO;
        mHeader.messageStreamId = RtmpHeader.MESSAGE_STREAM_ID;
        mHeader.timestamp = pts;
        mData = data;
        mIsAudioSpecificConfic = isAudioSpecificConfic;
        mFirstByte = firstByte;
    }

    @Override
    public void writePayload(OutputStream outputStream) throws IOException {
        outputStream.write(mFirstByte);
        outputStream.write(mIsAudioSpecificConfic ? 0 : 1);
        outputStream.write(mData);
    }}
