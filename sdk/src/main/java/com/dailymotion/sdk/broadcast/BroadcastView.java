package com.dailymotion.sdk.broadcast;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import io.kickflip.sdk.av.AVRecorder;
import io.kickflip.sdk.av.SessionConfig;
import io.kickflip.sdk.view.GLCameraEncoderView;

public class BroadcastView extends FrameLayout {
    private AVRecorder mRecorder;
    private RtmpMuxer mMuxer;
    private Listener mListener;
    private boolean mStarted;
    GLCameraEncoderView mCameraEncoderView;

    private RtmpMuxer.Listener mMuxerListener = new RtmpMuxer.Listener() {
        @Override
        public void onPublishStarted() {
            mStarted = true;
            mRecorder.startRecording();
            if (mListener != null) {
                mListener.onPublishStarted();
            }
        }

        @Override
        public void setBitrate(int mBitrate) {
            if (mRecorder != null) {
                mRecorder.adjustVideoBitrate(mBitrate);
            }
        }
    };

    public void release() {

        if (mRecorder != null) {
            if (mStarted) {
                mRecorder.stopRecording();
            }
            mRecorder.release();
        }

        if (mMuxer != null) {
            mMuxer.release();
        }
    }

    public interface Listener {
        void onPublishStarted();
    }
    public BroadcastView(Context context) {
        super(context);
        init();
    }

    public BroadcastView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setBackgroundColor(Color.BLACK);
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    public void startPublishing(String publishUrl) {
        if (mMuxer == null) {
            mCameraEncoderView = new GLCameraEncoderView(getContext());

            FrameLayout.LayoutParams surfaceViewParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            this.addView(mCameraEncoderView, 0, surfaceViewParams);

            mMuxer = new RtmpMuxer(publishUrl, mMuxerListener);
            SessionConfig config = new SessionConfig.Builder(mMuxer)
                    .withTitle("Title")
                    .withDescription("A live stream!")
                    .withAdaptiveStreaming(true)
                    .withVideoResolution(1280, 720)
                    .withVideoBitrate(RtmpMuxer.VIDEO_INITIAL_BITRATE)
                    .withAudioBitrate(RtmpMuxer.AUDIO_BITRATE)
                    .withPrivateVisibility(false)
                    .withLocation(true)
                    .build();
            mRecorder = new AVRecorder(config);
            mRecorder.setPreviewDisplay(mCameraEncoderView);
        }
    }

}
