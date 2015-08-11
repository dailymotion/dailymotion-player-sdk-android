package com.dailymotion.sdk.broadcast.amf;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class AmfSerializable {
    static final byte TYPE_NUMBER = 0;
    static final byte TYPE_BOOLEAN = 1;
    static final byte TYPE_STRING = 2;
    static final byte TYPE_OBJECT = 3;
    static final byte TYPE_NULL = 5;
    static final byte TYPE_ECMA_ARRAY = 8;
    abstract public void write(OutputStream outputStream) throws IOException;
    abstract public void read(InputStream inputStream) throws IOException;

    public String leftPad(String in, int level) {
        String padding = "";
        for (int i = 0; i < level; i++) {
            padding += "  ";
        }

        return padding + in;
    }
}
