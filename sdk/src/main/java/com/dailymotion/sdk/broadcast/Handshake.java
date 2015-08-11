package com.dailymotion.sdk.broadcast;

import com.dailymotion.sdk.util.DMLog;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

public class Handshake {
    static void doHandshake(InputStream inputStream, OutputStream outputStream) throws IOException {
        // write C0
        outputStream.write(3);
        // write C1
        Util.writeInt32(outputStream, 0);
        Util.writeInt32(outputStream, 0);

        byte[] C1 = new byte[1528];
        new Random().nextBytes(C1);
        outputStream.write(C1);

        // read S0
        int S0 = inputStream.read();
        if (S0 != 3) {
            DMLog.d(DMLog.BROADCAST, "unrecognized server version " + S0);
        }

        // read S1
        byte[] S = new byte[1536];
        Util.readFull(inputStream, S);

        // write C2, it is an echo of S1
        outputStream.write(S);

        // read S2, it should be the same as C1
        Util.readFull(inputStream, S);

        for (int i = 0; i < C1.length; i++) {
            if (C1[i] != S[8 + i]) {
                DMLog.d(DMLog.BROADCAST, "handshake mismatch @" + i);
            }
        }
    }
}
