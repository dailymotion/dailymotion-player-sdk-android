package com.dailymotion.sdk.broadcast.amf;

import com.dailymotion.sdk.broadcast.Util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class AmfObject extends AmfSerializable {
    final byte[] END_OF_OBJECT = {0, 0, 9};

    public Map<String, AmfSerializable> mMap = new HashMap<>();

    public AmfObject() {

    }

    public void setProperty(String name, AmfSerializable value) {
        mMap.put(name, value);
    }

    @Override
    public void write(OutputStream outputStream) throws IOException {
        outputStream.write(TYPE_OBJECT);
        for (String key: mMap.keySet()) {
            Util.writeInt16(outputStream, key.length());
            outputStream.write(key.getBytes("ASCII"));
            mMap.get(key).write(outputStream);
        }
        outputStream.write(END_OF_OBJECT);
    }

    @Override
    public void read(InputStream inputStream) throws IOException {
        byte b[] = new byte[3];

        while (true) {
            Util.readFull(inputStream, b);
            if (Arrays.equals(b, END_OF_OBJECT)) {
                break;
            }

            int length = ((int)b[0] << 8) | ((int)b[1]);
            byte string[] = new byte[length];

            if (length < 1) {
                throw new IOException("bad key");
            }
            string[0] = b[2];
            Util.readFull(inputStream, string, 1, string.length);

            String key = new String(string, "ASCII");

            AmfSerializable amfs = AmfReader.read(inputStream);
            mMap.put(key, amfs);
        }
    }

    @Override
    public String toString() {
        String s = "Object:\n";
        for (String key : mMap.keySet()) {
            s += key + " -> " + mMap.get(key).toString() + "\n";
        }

        return s;
    }

}
