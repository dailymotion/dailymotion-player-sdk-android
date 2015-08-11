package com.dailymotion.sdk.broadcast.amf;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class AmfNull extends AmfSerializable{
    @Override
    public void write(OutputStream outputStream) throws IOException {
        outputStream.write(TYPE_NULL);
    }

    @Override
    public void read(InputStream inputStream) throws IOException {

    }

    @Override
    public String toString() {
        return "Null";
    }
}
