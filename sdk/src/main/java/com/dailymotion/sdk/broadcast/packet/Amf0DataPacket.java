package com.dailymotion.sdk.broadcast.packet;

import com.dailymotion.sdk.broadcast.amf.AmfReader;
import com.dailymotion.sdk.broadcast.amf.AmfSerializable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class Amf0DataPacket extends RtmpPacket {
    public ArrayList<AmfSerializable> objects = new ArrayList<>();

    public Amf0DataPacket() {
        mHeader.chunkStreamId = RtmpHeader.CHUNK_STREAM_DATA;
        mHeader.messageTypeId = RtmpHeader.MESSAGE_TYPE_ID_AMF0_DATA;
    }

    @Override
    public void writePayload(OutputStream outputStream) throws IOException {
        for (AmfSerializable amf: objects) {
            amf.write(outputStream);
        }
    }

    @Override
    public void decodePayload(InputStream inputStream) throws IOException {
        while (inputStream.available() > 0) {
            objects.add(AmfReader.read(inputStream));
        }
    }
}
