package com.sn.videoplayer.media_codec.decoder;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


import com.sn.videoplayer.media_codec.extractor.IExtractor;
import com.sn.videoplayer.media_codec.extractor.VideoExtractor;

import java.nio.ByteBuffer;

public class VideoDecoder extends BaseDecoder {

    private String TAG = "VideoDecoder";

    private SurfaceView mSurfaceView;
    private Surface mSurface;
    private VideoExtractor videoExtractor;

    public VideoDecoder(String mFilePath, SurfaceView sfv, Surface surface) {
        super(mFilePath);
        mSurfaceView = sfv;
        mSurface = surface;
    }

    public VideoExtractor getVideoExtractor() {
        return videoExtractor;
    }

    public void seek(){
        videoExtractor.seek(1000000);
    }

    @Override
    void doneDecode() {
        Log.w(TAG, "SurfaceView和Surface都为空，至少需要一个不为空");
    }

    @Override
    void render(ByteBuffer outputBuffers, MediaCodec.BufferInfo bufferInfo) {

    }

    @Override
    boolean check() {
        if (mSurfaceView == null && mSurface == null) {
            Log.w(TAG, "SurfaceView和Surface都为空，至少需要一个不为空");
            mStateListener.decoderError(this, "显示器为空");
            return false;
        }
        return true;
    }

    @Override
    IExtractor initExtractor(String path) {
         videoExtractor = new VideoExtractor(path);
        return videoExtractor;
    }

    @Override
    void initSpecParams(MediaFormat format) {

    }

    @Override
    boolean initRender() {
        return true;
    }

    @Override
    boolean configCodec(final MediaCodec codec, final MediaFormat format) {
        Log.e(TAG, "configCodec  configure ");
        if (mSurface != null) {
            Log.e(TAG, "configCodec  mSurface.isValid " + mSurface.isValid());
            codec.configure(format, mSurface, null, 0);
            notifyDecode();
        } else {
            mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback2() {
                @Override
                public void surfaceRedrawNeeded(SurfaceHolder holder) {

                }

                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    codec.configure(format, holder.getSurface(), null, 0);
                    notifyDecode();
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {

                }
            });
            return false;
        }
        return true;
    }
}
