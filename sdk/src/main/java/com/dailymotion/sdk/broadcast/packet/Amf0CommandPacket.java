package com.dailymotion.sdk.broadcast.packet;

import com.dailymotion.sdk.broadcast.amf.AmfNumber;
import com.dailymotion.sdk.broadcast.amf.AmfReader;
import com.dailymotion.sdk.broadcast.amf.AmfSerializable;
import com.dailymotion.sdk.broadcast.amf.AmfString;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class Amf0CommandPacket extends RtmpPacket {
    public String commandName;
    public int transactionId;
    public ArrayList<AmfSerializable> objects = new ArrayList<>();

    public Amf0CommandPacket(RtmpHeader rtmpHeader) {
        super(rtmpHeader);
    }

    public Amf0CommandPacket(int transactionId) {
        mHeader.chunkStreamId = RtmpHeader.CHUNK_STREAM_AMF0_COMMAND;
        mHeader.messageTypeId = RtmpHeader.MESSAGE_TYPE_ID_AMF0_COMMAND;

        this.transactionId = transactionId;
    }
    @Override
    public void writePayload(OutputStream outputStream) throws IOException {
        AmfString amfs = new AmfString();
        amfs.string = commandName;
        amfs.write(outputStream);

        AmfNumber amfn = new AmfNumber();
        amfn.number = transactionId;
        amfn.write(outputStream);

        for (AmfSerializable amf: objects) {
            amf.write(outputStream);
        }
    }

    @Override
    public void decodePayload(InputStream inputStream) throws IOException {
        AmfSerializable amfs = AmfReader.read(inputStream);
        if (!(amfs instanceof AmfString)) {
            throw new IOException("expected string");
        }
        commandName = ((AmfString) amfs).string;

        amfs = AmfReader.read(inputStream);
        if (!(amfs instanceof AmfNumber)) {
            throw new IOException("expected number");
        }
        transactionId = (int)((AmfNumber) amfs).number;

        while (inputStream.available() > 0) {
            objects.add(AmfReader.read(inputStream));
        }
    }
}
