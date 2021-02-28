package com.sn.plugin_videoplayer.extractor;


import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.util.Log;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Android原生自带有一个MediaExtractor，用于音视频数据分离和提取，接来下就基于这个，做一个支持音视频提取的工具类MMExtracto
 */
public class MMExtractor {

    private static final String TAG = "MMExtractor";
    /**
     * 音视频分离器
     */
    private MediaExtractor mExtractor;

    /**
     * 音频通道索引
     */
    private int mAudioTrack = -1;

    /**
     * 视频通道索引
     */
    private int mVideoTrack = -1;

    /**
     * 当前帧时间戳
     */
    private Long mCurSampleTime;

    /**
     * 开始解码时间点
     */
    private Long mStartPos;

    public MMExtractor(String path) {
        init(path);
    }

    private void init(String path) {
        //【1，初始化】
        try {
            mExtractor = new MediaExtractor();
            mExtractor.setDataSource(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取视频格式参数
     */
    public MediaFormat getVideoFormat() {
        //【2.1，获取视频多媒体格式】
        Log.e(TAG, " getVideoFormat  getTrackCount " + mExtractor.getTrackCount());
        for (int i = 0; i < mExtractor.getTrackCount(); i++) {
            MediaFormat mediaFormat = mExtractor.getTrackFormat(i);
            String mime = mediaFormat.getString(MediaFormat.KEY_MIME);
            if (mime.startsWith("video/")) {
                mVideoTrack = i;
                break;
            }
        }
        if (mVideoTrack >= 0) {
            return mExtractor.getTrackFormat(mVideoTrack);
        } else {
            return null;
        }
    }

    /**
     * 获取音频格式参数
     */
    MediaFormat getAudioFormat() {
        //【2.2，获取音频频多媒体格式】
        for (int i = 0; i < mExtractor.getTrackCount(); i++) {
            MediaFormat mediaFormat = mExtractor.getTrackFormat(i);
            String mime = mediaFormat.getString(MediaFormat.KEY_MIME);
            if (mime.startsWith("audio/")) {
                mAudioTrack = i;
                break;
            }
        }
        if (mAudioTrack >= 0) {
            return mExtractor.getTrackFormat(mAudioTrack);
        } else {
            return null;
        }
    }

    /**
     * 读取视频数据
     */
    public int readBuffer(ByteBuffer byteBuffer) {
        //【3，提取数据】
        byteBuffer.clear();
        selectSourceTrack();
        int readSampleCount = mExtractor.readSampleData(byteBuffer, 0);
        if (readSampleCount < 0) {
            return -1;
        }
        mCurSampleTime = mExtractor.getSampleTime();
        mCurSampleFlag = mExtractor.getSampleFlags();
        mExtractor.advance();
        return readSampleCount;
    }

    /**
     * 选择通道
     */
    private void selectSourceTrack() {
        if (mVideoTrack >= 0) {
            mExtractor.selectTrack(mVideoTrack);
        } else if (mAudioTrack >= 0) {
            mExtractor.selectTrack(mAudioTrack);
        }
    }


    /**
     * Seek到指定位置，并返回实际帧的时间戳
     */
    public Long seek(Long pos) {
        mExtractor.seekTo(pos, MediaExtractor.SEEK_TO_PREVIOUS_SYNC);
        return mExtractor.getSampleTime();
    }


    /**
     * 停止读取数据
     */
    public void stop() {
        //【4，释放提取器】
        mExtractor.release();
        mExtractor = null;
    }

    int getVideoTrack() {
        return mVideoTrack;
    }

    int getAudioTrack() {
        return mAudioTrack;
    }

    public void setStartPos(Long pos) {
        mStartPos = pos;
    }

    /**
     * 获取当前帧时间
     */
    public long getCurrentTimestamp() {
        return mCurSampleTime;
    }

    /**
     * 当前帧标志
     */
    private int mCurSampleFlag;

    public int getSampleFlag() {
        return mCurSampleFlag;
    }

}
