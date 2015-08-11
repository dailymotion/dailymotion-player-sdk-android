package com.dailymotion.sdk.broadcast.amf;

import com.dailymotion.sdk.broadcast.Util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class AmfEcmaArray extends AmfSerializable {
    final byte[] END_OF_OBJECT = {0, 0, 9};

    public ArrayList<String> mKeys = new ArrayList<>();
    public ArrayList<AmfSerializable> mValues = new ArrayList<>();

    public AmfEcmaArray() {

    }

    public void setProperty(String name, AmfSerializable value) {
        mKeys.add(name);
        mValues.add(value);
    }

    @Override
    public void write(OutputStream outputStream) throws IOException {
        outputStream.write(TYPE_ECMA_ARRAY);
        Util.writeInt32(outputStream, mKeys.size());
        for (int i = 0; i < mKeys.size(); i++) {
            Util.writeInt16(outputStream, mKeys.get(i).length());
            outputStream.write(mKeys.get(i).getBytes("ASCII"));
            mValues.get(i).write(outputStream);
        }
        outputStream.write(END_OF_OBJECT);
    }

    @Override
    public void read(InputStream inputStream) throws IOException {
        byte b[] = new byte[3];
        int max = Util.readInt32(inputStream);
        int count = 0;
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
            mKeys.add(key);
            mValues.add(amfs);

            count++;
        }
    }

    @Override
    public String toString() {
        String s = "EcmaArray:\n";
        for (int i = 0; i < mKeys.size(); i++) {
            s += mKeys.get(i) + " -> " + mValues.get(i).toString() + "\n";
        }

        return s;
    }
}
