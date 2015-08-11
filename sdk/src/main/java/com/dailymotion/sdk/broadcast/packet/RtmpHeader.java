package com.dailymotion.sdk.broadcast.packet;

import android.util.SparseArray;

import com.dailymotion.sdk.broadcast.Util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class RtmpHeader {
    public static final int CHUNK_TYPE_0 = 0;
    public static final int CHUNK_TYPE_1 = 1;
    public static final int CHUNK_TYPE_2 = 2;
    public static final int CHUNK_TYPE_3 = 3;

    public static final int CHUNK_STREAM_ID_CONTROL = 2;
    public static final int CHUNK_STREAM_AMF0_COMMAND = 3;
    public static final int CHUNK_STREAM_DATA = 4;


    /* protocol control messages (message stream id= 0, chunk stream id= 2)*/
    public static final int MESSAGE_TYPE_ID_SET_CHUNK_SIZE = 1;
    public static final int MESSAGE_TYPE_ID_ABORT = 2;
    public static final int MESSAGE_TYPE_ID_ACKNOWLEDGEMENT = 3;
    public static final int MESSAGE_TYPE_ID_ACKNOWLEDGEMENT_WINDOW_SIZE = 5;
    public static final int MESSAGE_TYPE_ID_SET_PEER_BANDWIDTH = 6;

    /* user control message */
    public static final int MESSAGE_TYPE_ID_USER_CONTROL = 4;

    public static final int MESSAGE_TYPE_ID_AMF0_COMMAND = 20;

    public static final int MESSAGE_TYPE_ID_AMF0_DATA = 18;

    public static int MESSAGE_TYPE_ID_AUDIO = 8;
    public static int MESSAGE_TYPE_ID_VIDEO = 9;

    public static int MESSAGE_STREAM_ID = 1;

    public int chunkStreamId;
    public long timestamp;
    public int messageStreamId;
    public int messageTypeId;
    public int dataLength;
    public int delta;

    public RtmpHeader() {}

    public void read(InputStream inputStream, SparseArray<RtmpHeader> previousHeaders) throws IOException {
        int b = inputStream.read() & 0xff;
        int chunkTypeId = b >> 6;

        chunkStreamId = b & 0x3f;
        if (chunkStreamId == 0 || chunkStreamId == 1) {
            throw new IOException("not supported");
        }

        RtmpHeader previousHeader = previousHeaders.get(chunkStreamId);

        switch(chunkTypeId) {
            case CHUNK_TYPE_0:
                timestamp = Util.readInt24(inputStream);
                dataLength = Util.readInt24(inputStream);
                messageTypeId = inputStream.read() & 0xff;
                messageStreamId = Util.readInt32LittleEndian(inputStream);
                break;
            case CHUNK_TYPE_1:
            {
                if (previousHeader == null) {
                    throw new IOException("bad chunk received");
                }

                delta = Util.readInt24(inputStream);
                dataLength = Util.readInt24(inputStream);
                messageTypeId = inputStream.read() & 0xff;
                messageStreamId = previousHeader.messageStreamId;
                timestamp = previousHeader.timestamp + delta;
                break;
            }
            case CHUNK_TYPE_2:
            {
                if (previousHeader == null) {
                    throw new IOException("bad chunk received");
                }

                delta = Util.readInt24(inputStream);
                dataLength = previousHeader.dataLength;
                messageTypeId = previousHeader.messageTypeId;
                messageStreamId = previousHeader.messageStreamId;
                timestamp = previousHeader.timestamp + delta;
            }
            case CHUNK_TYPE_3:
            {
                if (previousHeader == null) {
                    throw new IOException("bad chunk received");
                }

                dataLength = previousHeader.dataLength;
                messageTypeId = previousHeader.messageTypeId;
                messageStreamId = previousHeader.messageStreamId;
                timestamp = previousHeader.timestamp;
            }

            if (previousHeader == null) {
                previousHeader = new RtmpHeader();
                previousHeaders.put(chunkStreamId, previousHeader);
            }

            previousHeader.chunkStreamId = chunkStreamId;
            previousHeader.timestamp = timestamp;
            previousHeader.messageTypeId = messageTypeId;
            previousHeader.messageStreamId = messageStreamId;
            previousHeader.delta = delta;
        }
    }

    public void write(OutputStream outputStream, SparseArray<RtmpHeader> previousHeaders) throws IOException {
        //
        // XXX: compress
        //
        outputStream.write(CHUNK_TYPE_0 | chunkStreamId);
        Util.writeInt24(outputStream, (int)timestamp);
        Util.writeInt24(outputStream, dataLength);
        outputStream.write(messageTypeId);
        Util.writeInt32LittleEndian(outputStream, messageStreamId);
    }
}
