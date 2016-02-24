package com.dailymotion.sdk.broadcast.amf;

import com.dailymotion.sdk.util.DMLog;

import java.io.IOException;
import java.io.InputStream;

public class AmfReader {
    public static AmfSerializable read(InputStream inputStream) throws IOException {
        int b = inputStream.read();
        AmfSerializable amfs = null;
        switch ((byte)b) {
            case AmfSerializable.TYPE_NUMBER:
                amfs = new AmfNumber();
                break;
            case AmfSerializable.TYPE_NULL:
                amfs = new AmfNull();
                break;
            case AmfSerializable.TYPE_STRING:
                amfs = new AmfString();
                break;
            case AmfSerializable.TYPE_OBJECT:
                amfs = new AmfObject();
                break;
            default:
                DMLog.d(DMLog.STUFF, "AmfObject " + b + " not handled");

        }
        amfs.read(inputStream);

        return amfs;
    }
}
