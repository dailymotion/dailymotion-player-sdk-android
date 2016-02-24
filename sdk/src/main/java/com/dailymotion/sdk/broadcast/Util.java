package com.dailymotion.sdk.broadcast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class Util {
    public static byte[] toByteArray(int value) {
        return ByteBuffer.allocate(4).putInt(value).array();
    }

    public static int readInt16(InputStream inputStream) throws IOException {
        return ((inputStream.read() & 0xff) << 8)
                | (inputStream.read() & 0xff);
    }

    public static void writeInt16(OutputStream outputStream, int value) throws IOException {
        outputStream.write((byte)(value >> 8));
        outputStream.write((byte)(value));
    }

    public static int readInt24(InputStream inputStream) throws IOException {
        return ((inputStream.read() & 0xff) << 16)
                | ((inputStream.read() & 0xff) << 8)
                | (inputStream.read() & 0xff);
    }

    public static void writeInt24(OutputStream outputStream, int value) throws IOException {
        outputStream.write((byte)(value >> 16));
        outputStream.write((byte)(value >> 8));
        outputStream.write((byte)(value));
    }

    public static int readInt32(InputStream inputStream) throws IOException {
        return ((inputStream.read() & 0xff) << 24)
                | ((inputStream.read() & 0xff) << 16)
                | ((inputStream.read() & 0xff) << 8)
                | (inputStream.read() & 0xff);
    }
    public static void writeInt32(OutputStream outputStream, int value) throws IOException {
        outputStream.write((byte)(value >> 24));
        outputStream.write((byte)(value >> 16));
        outputStream.write((byte)(value >> 8));
        outputStream.write((byte)(value));
    }

    public static long readInt64(InputStream inputStream) throws IOException {
        return (((long)(inputStream.read() & 0xff)) << 56)
                | (((long)(inputStream.read() & 0xff)) << 48)
                | (((long)(inputStream.read() & 0xff)) << 40)
                | (((long)(inputStream.read() & 0xff)) << 32)
                | (((long)(inputStream.read() & 0xff)) << 24)
                | (((long)(inputStream.read() & 0xff)) << 16)
                | (((long)(inputStream.read() & 0xff)) << 8)
                | (((long)inputStream.read() & 0xff));
    }
    public static void writeInt64(OutputStream outputStream, long value) throws IOException {
        outputStream.write((byte)(value >> 56));
        outputStream.write((byte)(value >> 48));
        outputStream.write((byte)(value >> 40));
        outputStream.write((byte)(value >> 32));
        outputStream.write((byte)(value >> 24));
        outputStream.write((byte)(value >> 16));
        outputStream.write((byte)(value >> 8));
        outputStream.write((byte)(value));
    }

    public static void writeInt32LittleEndian(OutputStream outputStream, int value) throws IOException {
        outputStream.write((byte)(value));
        outputStream.write((byte)(value >> 8));
        outputStream.write((byte)(value >> 16));
        outputStream.write((byte)(value >> 24));
    }

    public static int readInt32LittleEndian(InputStream inputStream) throws IOException {
        return (inputStream.read() & 0xff)
                | ((inputStream.read() & 0xff) << 8)
                | ((inputStream.read() & 0xff) << 16)
                | ((inputStream.read() & 0xff) << 24);
    }

    public static void readFull(InputStream inputStream, byte[] data) throws IOException {
        readFull(inputStream, data, 0, data.length);
    }

    public static void readFull(InputStream inputStream, byte[] data, int offset, int max) throws IOException {
        int read;
        do {
            read = inputStream.read(data, offset, (max - offset));
            if (read != -1) {
                offset += read;
            } else {
                throw new IOException("");
            }
        } while (offset < max);

    }

    public static void putInt32(byte[] data, int srcPos, int value) {
        data[srcPos + 0] = (byte)(value >> 24);
        data[srcPos + 1] = (byte)(value >> 16);
        data[srcPos + 2] = (byte)(value >> 8);
        data[srcPos + 3] = (byte)(value >> 0);
    }
}
