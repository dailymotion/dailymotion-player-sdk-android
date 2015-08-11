package com.dailymotion.sdk.broadcast;

import com.dailymotion.sdk.util.DMLog;

import java.util.ArrayList;

public class H264 {
    static public final int NAL_SPS = 7;
    static public final int NAL_PPS = 8;

    public static class ExtraData {
        public ArrayList<byte[]> sps;
        public ArrayList<byte[]> pps;

        public ExtraData() {
            sps = new ArrayList<>();
            pps = new ArrayList<>();
        }
    }

    static ExtraData parseExtraData(byte extraData[]) {
        ExtraData e = new ExtraData();

        if (getNextStartCode(extraData, 0) != 0) {
            return e;
        }

        int start = 4;
        int end = 0;
        int nextStartCode;
        while (true) {
            nextStartCode = getNextStartCode(extraData, start);
            if (nextStartCode == -1) {
                end = extraData.length;
            } else {
                end = nextStartCode;
            }

            if (start == end) {
                break;
            }

            byte[] nal = new byte[end - start];
            System.arraycopy(extraData, start, nal, 0, end - start);

            int nal_unit = (int)nal[0] & 0x1f;
            switch (nal_unit) {
                case NAL_SPS:
                    e.sps.add(nal);
                    break;
                case NAL_PPS:
                    e.pps.add(nal);
                    break;
                default:
                    DMLog.d(DMLog.STUFF, "Unknown NAL " + nal_unit);
                    break;
            }

            if (nextStartCode == -1) {
                break;
            } else {
                start = nextStartCode + 4;
            }
        }

        return e;
    }

    static public int getNextStartCode(byte []data, int offset) {
        int i = offset;
        while (i <= data.length - 4) {
            if (data[i] == 0 && data[i+1] == 0 && data[i+2] == 0 && data[i+3] == 1) {
                break;
            }
            i++;
        }
        if (i > data.length - 4) {
            return -1;
        } else {
            return i;
        }
    }
}
