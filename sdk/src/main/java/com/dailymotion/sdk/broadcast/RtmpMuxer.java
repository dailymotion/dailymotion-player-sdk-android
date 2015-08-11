package com.dailymotion.sdk.broadcast;

import android.annotation.TargetApi;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.util.SparseArray;


import com.dailymotion.sdk.broadcast.amf.AmfBoolean;
import com.dailymotion.sdk.broadcast.amf.AmfEcmaArray;
import com.dailymotion.sdk.broadcast.amf.AmfNull;
import com.dailymotion.sdk.broadcast.amf.AmfNumber;
import com.dailymotion.sdk.broadcast.amf.AmfObject;
import com.dailymotion.sdk.broadcast.amf.AmfSerializable;
import com.dailymotion.sdk.broadcast.amf.AmfString;
import com.dailymotion.sdk.broadcast.packet.Amf0CommandPacket;
import com.dailymotion.sdk.broadcast.packet.Amf0DataPacket;
import com.dailymotion.sdk.broadcast.packet.AudioPacket;
import com.dailymotion.sdk.broadcast.packet.RtmpHeader;
import com.dailymotion.sdk.broadcast.packet.RtmpPacket;
import com.dailymotion.sdk.broadcast.packet.SetChunkSizePacket;
import com.dailymotion.sdk.broadcast.packet.VideoPacket;
import com.dailymotion.sdk.util.DMLog;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import io.kickflip.sdk.av.Muxer;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class RtmpMuxer extends Muxer {
    public static final int VIDEO_INITIAL_BITRATE = 1500*1000;
    public static final int AUDIO_BITRATE = 128*1000;
    private final Socket mSocket;
    private final String mHost;
    private int mPort;
    private String mApp;
    private String mPlayPath;

    private Thread mControlThread;
    private Object mLock;
    private Queue<Object> mControlPacketsQueue = new LinkedList<>();
    private Queue<RtmpPacket> mDataPacketsQueue = new LinkedList<>();
    private boolean mPublishDone;
    private boolean mCanWrite;
    private int mVideoTrackindex;
    private int mAudioTrackIndex;
    private ArrayList<MediaFormat> mMediaFormats = new ArrayList<>();
    private Handler mHandler;
    private Listener mListener;

    /*
     * used by the read thread
     */
    private InputStream mInputStream;
    private int mReadChunkSize = 128;
    private SparseArray<RtmpHeader> mReadHeaders = new SparseArray<>();

    /*
     * used by the write thread
     */
    private NetworkOutputStream mOutputStream;
    private int mWriteChunkSize = 128;
    private SparseArray<RtmpHeader> mWriteHeaders = new SparseArray<>();
    private SparseArray<ByteArrayOutputStream> mMessagesInFlight = new SparseArray<>();
    private int mConnectTransactionId;
    private int mCreateStreamTransactionId;
    private int mTransactionIdCounter = 1;
    private byte mAudioFirstByte;
    private long mLastNanos;
    private int mBitrate = VIDEO_INITIAL_BITRATE;
    private int mLastSize;

    private static class Sentinel {
    }

    public interface Listener {
        void onPublishStarted();
        void setBitrate(int mBitrate);
    }

    void controlThread() {
        Thread writeThread = null;
        try {
            InetSocketAddress socketAddress = new InetSocketAddress(mHost, mPort);

            mSocket.connect(socketAddress, 3000);

            mInputStream = mSocket.getInputStream();
            mOutputStream = new NetworkOutputStream(mSocket.getOutputStream());

            Handshake.doHandshake(mInputStream, mOutputStream);

            writeThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    writeThread();
                }
            });

            mWriteChunkSize = 4096;

            writeThread.start();

            sendSetChunkSize();
            sendConnect();

            while (true) {
                RtmpHeader header = new RtmpHeader();
                header.read(mInputStream, mReadHeaders);
                ByteArrayInputStream in;
                ByteArrayOutputStream baos = mMessagesInFlight.get(header.chunkStreamId);
                if (baos == null) {
                    baos = new ByteArrayOutputStream();
                    mMessagesInFlight.put(header.chunkStreamId, baos);
                }

                int remainingBytes = header.dataLength - baos.size();
                byte[] chunk = new byte[Math.min(remainingBytes, mReadChunkSize)];
                Util.readFull(mInputStream, chunk);
                baos.write(chunk);

                if (baos.size() == header.dataLength) {
                    in = new ByteArrayInputStream(baos.toByteArray());
                    baos.reset();
                } else {
                    continue;
                }

                RtmpPacket rtmpPacket;
                switch(header.messageTypeId) {
                    case RtmpHeader.MESSAGE_TYPE_ID_SET_CHUNK_SIZE:
                        rtmpPacket = new SetChunkSizePacket(header);
                        break;
                    case RtmpHeader.MESSAGE_TYPE_ID_AMF0_COMMAND:
                        rtmpPacket = new Amf0CommandPacket(header);
                        break;
                    default:
                        rtmpPacket = new RtmpPacket();
                        break;
                }

                rtmpPacket.decodePayload(in);

                if (rtmpPacket instanceof SetChunkSizePacket) {
                    mReadChunkSize = ((SetChunkSizePacket)rtmpPacket).getChunkSize();
                } else if (rtmpPacket instanceof Amf0CommandPacket) {
                    Amf0CommandPacket amf0 = (Amf0CommandPacket)rtmpPacket;
                    if (amf0.commandName.equals("_result")) {
                        if (amf0.transactionId == mConnectTransactionId) {
                            sendRelease();
                            sendFCPublish();
                            sendCreateStream();
                        } else if (amf0.transactionId == mCreateStreamTransactionId) {
                            sendPublish();
                        }
                    } else if (amf0.commandName.equals("onStatus")) {
                        if (amf0.objects.size() > 1 && amf0.objects.get(1) instanceof AmfObject) {
                            AmfObject o = (AmfObject)amf0.objects.get(1);
                            AmfString amfs = (AmfString)o.mMap.get("code");
                            if (amfs.string.equals("NetStream.Publish.Start")) {
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (mListener != null) {
                                            mListener.onPublishStarted();
                                        }
                                    }
                                });
                                DMLog.d(DMLog.BROADCAST, "start Publishing");
                            }
                        }
                    }

                    DMLog.d(DMLog.BROADCAST, "AMFO command: " + amf0.commandName + "(" + amf0.transactionId + ")");
                    for (AmfSerializable amfs: amf0.objects) {
                        DMLog.d(DMLog.BROADCAST, amfs.toString());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //
        // cleanup...
        //
        try {
            if (writeThread != null) {
                writeThread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            if (mOutputStream != null) {
                mOutputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            if (mInputStream != null) {
                mInputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            mSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addControlPacket(Object o) {
        synchronized (mLock) {
            mControlPacketsQueue.add(o);
            mLock.notify();
        }
    }

    private void addDataPacket(RtmpPacket rtmpPacket) {
        synchronized (mLock) {
            mDataPacketsQueue.add(rtmpPacket);
            mLock.notify();
        }
    }


    private void sendConnect() {
        Amf0CommandPacket amf0CommandPacket = new Amf0CommandPacket(mTransactionIdCounter++);
        amf0CommandPacket.commandName = "connect";
        AmfObject amfo = new AmfObject();
        amfo.setProperty("app", new AmfString(mApp));
        amfo.setProperty("type", new AmfString("nonprivate"));
        amfo.setProperty("flashVer", new AmfString("FMLE/3.0 (compatible; FMSc/1.0)"));
        amfo.setProperty("swfUrl", new AmfString("rtmp://publish.dailymotion.com/publish-dm"));
        amfo.setProperty("tcUrl", new AmfString("rtmp://publish.dailymotion.com/publish-dm"));
        amf0CommandPacket.objects.add(amfo);
        mConnectTransactionId = amf0CommandPacket.transactionId;
        addControlPacket(amf0CommandPacket);
    }

    private void sendRelease() {
        Amf0CommandPacket amf0CommandPacket = new Amf0CommandPacket(mTransactionIdCounter++);
        amf0CommandPacket.commandName = "releaseStream";
        amf0CommandPacket.objects.add(new AmfNull());
        amf0CommandPacket.objects.add(new AmfString(mPlayPath));
        addControlPacket(amf0CommandPacket);
    }

    private void sendFCPublish() {
        Amf0CommandPacket amf0CommandPacket = new Amf0CommandPacket(mTransactionIdCounter++);
        amf0CommandPacket.commandName = "FCPublish";
        amf0CommandPacket.objects.add(new AmfNull());
        amf0CommandPacket.objects.add(new AmfString(mPlayPath));
        addControlPacket(amf0CommandPacket);
    }

    private void sendCreateStream() {
        Amf0CommandPacket amf0CommandPacket = new Amf0CommandPacket(mTransactionIdCounter++);
        amf0CommandPacket.commandName = "createStream";
        amf0CommandPacket.objects.add(new AmfNull());
        mCreateStreamTransactionId = amf0CommandPacket.transactionId;
        addControlPacket(amf0CommandPacket);
    }

    private void sendPublish() {
        Amf0CommandPacket amf0CommandPacket = new Amf0CommandPacket(mTransactionIdCounter++);
        amf0CommandPacket.commandName = "publish";
        amf0CommandPacket.objects.add(new AmfNull());
        amf0CommandPacket.objects.add(new AmfString(mPlayPath));
        amf0CommandPacket.objects.add(new AmfString("live"));
        addControlPacket(amf0CommandPacket);
    }

    private void sendSetChunkSize() {
        SetChunkSizePacket sendChunkSizePacket = new SetChunkSizePacket();
        sendChunkSizePacket.setChunkSize(mWriteChunkSize);
        addControlPacket(sendChunkSizePacket);
    }

    private void writeThread() {
        long startNanos;
        long idleNanos = 0;

        while (true) {
            Object o = null;
            startNanos = System.nanoTime();

            synchronized (mLock) {
                if (!mControlPacketsQueue.isEmpty()) {
                    o = mControlPacketsQueue.remove();
                }

                if (o == null) {
                    if (!mDataPacketsQueue.isEmpty()) {
                        o = mDataPacketsQueue.remove();
                    }
                }

                if (o == null) {
                    try {
                        mLock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            idleNanos += System.nanoTime() - startNanos;

            if (o == null) {
                continue;
            } else if (o instanceof Sentinel) {
                break;
            }

            RtmpPacket packet = (RtmpPacket)o;
            try {
                packet.write(mOutputStream, mWriteHeaders, mWriteChunkSize);
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }

            synchronized (mLock) {
                long nowNanos = System.nanoTime();
                if (nowNanos - mLastNanos > 1000L*1000*1000) {
                    double idle = (double)idleNanos / (double)(nowNanos - mLastNanos);
                    int actualBitrate = mOutputStream.getBitrate();
                    int size = mDataPacketsQueue.size();
                    String s = "Bitrate: " + actualBitrate / 1000
                            + " kbps In-Flight:" + size
                            + " idle: " + idle;
                    if (size > 5 && size > mLastSize) {
                        mBitrate = (int)((double)actualBitrate * 0.8);
                        if (mBitrate < 100*1000) {
                            mBitrate = 100*1000;
                        }
                        s += "   late => " + mBitrate;
                        mListener.setBitrate(mBitrate);
                    } else if (size == 0 && idle > 0.8) {
                        mBitrate = (int)((double)mBitrate * 1.03);
                        if (mBitrate > 5000*1000) {
                            mBitrate = 5000*1000;
                        }
                        s += "   idle => " + mBitrate;
                        mListener.setBitrate(mBitrate);
                    }

                    DMLog.d(DMLog.BROADCAST, s);
                    mLastNanos = System.nanoTime();
                    mLastSize = size;
                    idleNanos = 0;
                }
            }
        }

        try {
            mOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected RtmpMuxer(String rmtpUrl, Listener listener) {
        super(rmtpUrl, FORMAT.RTMP);

        mListener = listener;

        mHandler = new Handler();

        mLock = new Object();

        Uri uri = Uri.parse(rmtpUrl);
        mHost = uri.getHost();
        mPort = uri.getPort();
        if (mPort == -1) {
            mPort = 1935;
        }
        String p[] = uri.getPath().split("/");
        if (p.length > 2) {
            mApp = p[1];
            mPlayPath = p[2] + "?" + uri.getQuery();
        }

        DMLog.d(DMLog.BROADCAST, "RtmpMuxer: app=" + mApp + " playpath=" + mPlayPath);

        mSocket = new Socket();

        mControlThread = new Thread(new Runnable() {
            @Override
            public void run() {
                controlThread();
            }
        });
        mControlThread.start();
    }

    @Override
    public void forceStop() {

    }

    @Override
    public int addTrack(MediaFormat trackFormat) {
        int trackIndex = super.addTrack(trackFormat);
        mMediaFormats.add(trackFormat);
        String s = "addTrack " + trackIndex;

        String mimeType = trackFormat.getString(trackFormat.KEY_MIME);
        if (mimeType.equals(trackFormat.MIMETYPE_VIDEO_AVC)) {
            mVideoTrackindex = trackIndex;
            s += "(video)";
        } else if (mimeType.equals(trackFormat.MIMETYPE_AUDIO_AAC)) {
            mAudioTrackIndex = trackIndex;
            s += "(audio)";
        } else {
            s = "Unknown mime type " + mimeType;
        }

        DMLog.d(DMLog.BROADCAST, s);
        return trackIndex;
    }

    public void writeSampleData(MediaCodec encoder, int trackIndex, int bufferIndex, ByteBuffer encodedData, MediaCodec.BufferInfo bufferInfo){
        if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
            signalEndOfTrack();
        }

        synchronized (mLock) {
            if (!mCanWrite) {
                if (mMediaFormats.size() == 2) {
                    mCanWrite = true;

                    DMLog.d(DMLog.BROADCAST, "sending codec config");

                    Amf0DataPacket amf0DataPacket = new Amf0DataPacket();
                    amf0DataPacket.objects.add(new AmfString("@setDataFrame"));
                    amf0DataPacket.objects.add(new AmfString("onMetaData"));
                    AmfEcmaArray array = new AmfEcmaArray();
                    array.setProperty("duration", new AmfNumber(0));
                    array.setProperty("fileSize", new AmfNumber(0));

                    MediaFormat trackFormat = mMediaFormats.get(mVideoTrackindex);
                    array.setProperty("width", new AmfNumber(trackFormat.getInteger(trackFormat.KEY_WIDTH)));
                    array.setProperty("height", new AmfNumber(trackFormat.getInteger(trackFormat.KEY_HEIGHT)));
                    array.setProperty("videocodecid", new AmfString("avc1"));
                    array.setProperty("videodatarate", new AmfNumber(mBitrate /1000));
                    // how can I detect that ?
                    array.setProperty("framerate", new AmfNumber(30));

                    trackFormat = mMediaFormats.get(mAudioTrackIndex);
                    array.setProperty("audiocodecid", new AmfString("mp4a"));
                    array.setProperty("audiodatarate", new AmfNumber(AUDIO_BITRATE/1000));
                    int sampleRate = trackFormat.getInteger(trackFormat.KEY_SAMPLE_RATE);
                    array.setProperty("audiosamplerate", new AmfNumber(sampleRate));
                    int sampleSize = 16;
                    array.setProperty("audiosamplesize", new AmfNumber(sampleSize));
                    int channelCount = trackFormat.getInteger(trackFormat.KEY_CHANNEL_COUNT);
                    array.setProperty("audiochannels", new AmfNumber(channelCount));
                    array.setProperty("stereo", new AmfBoolean((channelCount >= 2 )? true : false));
                    array.setProperty("encoder", new AmfString("dmandroidapp"));

                    amf0DataPacket.objects.add(array);

                    addDataPacket(amf0DataPacket);

                    int firstByte = 0xa2;
                    switch (sampleRate / 1000) {
                        case 44:
                            firstByte |= 3 << 2;
                            break;
                        case 22:
                            firstByte |= 2 << 2;
                            break;
                        case 11:
                            firstByte |= 1 << 2;
                            break;
                        case 5:
                            firstByte |= 0 << 2;
                            break;
                    }
                    if (channelCount >= 2) {
                        firstByte |= 1;
                    }

                    mAudioFirstByte = (byte)firstByte;
                }
            }
        }

        if (!mCanWrite) {
            encoder.releaseOutputBuffer(bufferIndex, false);
            return;
        }

        byte data[] = new byte[bufferInfo.size];
        encodedData.position(bufferInfo.offset);
        encodedData.limit(bufferInfo.offset + bufferInfo.size);
        encodedData.get(data);

        long pts = 0;
        if (trackIndex == mVideoTrackindex) {
            boolean isKey = (bufferInfo.flags & MediaCodec.BUFFER_FLAG_KEY_FRAME) != 0;
            boolean isHeader = false;
            if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                isHeader = true;
                try {
                    H264.ExtraData e = H264.parseExtraData(data);
                    byte sps[] = e.sps.get(0);
                    byte pps[] = e.pps.get(0);

                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    out.write(0x01);
                    out.write(sps, 1, 3);
                    out.write(0xff);
                    out.write(0xe1);

                    Util.writeInt16(out, sps.length);
                    out.write(sps, 0, sps.length);
                    out.write(0x01);
                    Util.writeInt16(out, pps.length);
                    out.write(pps, 0, pps.length);

                    data = out.toByteArray();
                } catch (Exception e) {
                    DMLog.e(DMLog.BROADCAST, "bad extradata");
                    e.printStackTrace();
                }
            } else {

                if (mFirstPts == -1 && bufferInfo.presentationTimeUs > 0) {
                    mFirstPts = bufferInfo.presentationTimeUs;
                    DMLog.d(DMLog.BROADCAST, "mFirstPts=" + mFirstPts);
                }

                // replace annex B start codes with NAL sizes;
                int start = 4;
                if (H264.getNextStartCode(data, 0) != 0) {
                    DMLog.e(DMLog.BROADCAST, "encoded data does not start with a start code ?");
                }

                int end;

                while (true) {
                    int nextStartCode = H264.getNextStartCode(data, start);
                    if (nextStartCode == -1) {
                        end = data.length;
                    } else {
                        end = nextStartCode;
                    }

                    Util.putInt32(data, start - 4, end -start);

                    if (nextStartCode == -1) {
                        break;
                    } else {
                        start = nextStartCode + 4;
                    }
                }

                if (mFirstPts == -1) {
                    pts = 0;
                } else {
                    pts = bufferInfo.presentationTimeUs - mFirstPts;
                }
            }

            pts /= 1000;
            //DMLog.d(DMLog.BROADCAST, "writeSampleData " + trackIndex + " pts=" + pts);
            addDataPacket(new VideoPacket(data, isHeader, pts, isKey));
        } else {
            boolean isHeader = false;
            if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                isHeader = true;
            } else {
                pts = bufferInfo.presentationTimeUs - mFirstPts;
            }

            if (mFirstPts != -1 && pts >= 0) {
                pts /= 1000;
                addDataPacket(new AudioPacket(data, isHeader, pts, mAudioFirstByte));
                //DMLog.d(DMLog.BROADCAST, "writeSampleData " + trackIndex + "                          pts=" + pts);
            }
        }

        encoder.releaseOutputBuffer(bufferIndex, false);
    }

    public void release() {
        try {
            mSocket.shutdownInput();
            mSocket.shutdownOutput();
        } catch (IOException e) {
            e.printStackTrace();
        }
        addControlPacket(new Sentinel());
    }
}
