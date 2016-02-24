package com.dailymotion.sdk.broadcast.amf;

import com.dailymotion.sdk.broadcast.Util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class AmfString extends AmfSerializable{
    public String string;
    public AmfString() {
    }

    public AmfString(String string) {
        this.string = string;
    }

    @Override
    public void write(OutputStream outputStream) throws IOException {
        outputStream.write(TYPE_STRING);
        Util.writeInt16(outputStream, string.length());
        outputStream.write(string.getBytes("ASCII"));
    }

    @Override
    public void read(InputStream inputStream) throws IOException {
        int length = Util.readInt16(inputStream);
        byte[] b = new byte[length];
        Util.readFull(inputStream, b);
        string = new String(b, "ASCII");
    }

    @Override
    public String toString() {
        String s = "String: " + string;

        return s;
    }

}
