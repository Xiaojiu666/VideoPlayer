package com.sn.plugin_videoplayer.muxer;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.util.Log;


import com.sn.plugin_videoplayer.extractor.AudioExtractor;
import com.sn.plugin_videoplayer.extractor.VideoExtractor;

import java.nio.ByteBuffer;

public class MP4Repack {

    private static final String TAG = " MP4Repack ";

    private String mFilePath;
    //初始化音视频分离器
    private AudioExtractor mAExtractor;
    private VideoExtractor mVExtractor;
    //初始化封装器
    private MMuxer mMuxer = new MMuxer();

    public MP4Repack(String filePath) {
        mFilePath = filePath;
        mAExtractor = new AudioExtractor(filePath);
        mVExtractor = new VideoExtractor(filePath);
    }


    public void start() {
        final MediaFormat audioFormat = mAExtractor.getFormat();
        final MediaFormat videoFormat = mVExtractor.getFormat();

        //判断是否有音频数据，没有音频数据则告诉封装器，忽略音频轨道
        if (audioFormat != null) {
            mMuxer.addAudioTrack(audioFormat);
        } else {
            mMuxer.setNoAudio();
        }
        //判断是否有视频数据，没有音频数据则告诉封装器，忽略视频轨道
        if (videoFormat != null) {
            mMuxer.addVideoTrack(videoFormat);
        } else {
            mMuxer.setNoVideo();
        }

        //启动线程
        new Thread(new Runnable() {
            @Override
            public void run() {
                ByteBuffer buffer = ByteBuffer.allocate(500 * 1024);
                MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
                //音频数据分离和写入
                if (audioFormat != null) {
                    int size = mAExtractor.readBuffer(buffer);
                    while (size > 0) {
                        bufferInfo.set(0, size, mAExtractor.getCurrentTimestamp(),
                                mAExtractor.getSampleFlag());

                        mMuxer.writeAudioData(buffer, bufferInfo);

                        size = mAExtractor.readBuffer(buffer);
                    }
                }

                //视频数据分离和写入
                if (videoFormat != null) {
                    int size = mVExtractor.readBuffer(buffer);
                    while (size > 0) {
                        bufferInfo.set(0, size, mVExtractor.getCurrentTimestamp(),
                                mVExtractor.getSampleFlag());

                        mMuxer.writeVideoData(buffer, bufferInfo);

                        size = mVExtractor.readBuffer(buffer);
                    }
                }

                mAExtractor.stop();
                mVExtractor.stop();
                mMuxer.release();
                Log.i(TAG, "MP4 重打包完成");

            }
        }).start();

    }

}
