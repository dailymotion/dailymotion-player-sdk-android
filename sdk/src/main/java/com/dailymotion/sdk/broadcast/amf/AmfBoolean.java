package com.dailymotion.sdk.broadcast.amf;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class AmfBoolean extends AmfSerializable {
    public boolean bool;

    public AmfBoolean(boolean bool) {
        this.bool = bool;
    }

    @Override
    public void write(OutputStream outputStream) throws IOException {
        outputStream.write(TYPE_BOOLEAN);
        outputStream.write(bool ? 1:0);
    }

    @Override
    public void read(InputStream inputStream) throws IOException {
        int b = inputStream.read();
        if (b != 0) {
            bool = true;
        } else {
            bool = false;
        }
    }

    @Override
    public String toString() {
        return "Boolean: " + bool;
    }
}
