package com.dailymotion.sdk.broadcast.amf;

import com.dailymotion.sdk.broadcast.Util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class AmfNumber extends AmfSerializable {
    public long number;

    public AmfNumber() {
    }

    public AmfNumber(int number) {
        this.number = number;
    }

    @Override
    public void write(OutputStream outputStream) throws IOException {
        outputStream.write(TYPE_NUMBER);
        long bits = Double.doubleToRawLongBits(number);
        Util.writeInt64(outputStream, bits);

    }

    @Override
    public void read(InputStream inputStream) throws IOException {
        long bits = Util.readInt64(inputStream);
        double value = Double.longBitsToDouble(bits);
        number = (long)value;
    }

    @Override
    public String toString() {
        String s = "Number: " + number;

        return s;
    }

}
