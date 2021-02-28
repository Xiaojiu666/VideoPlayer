package com.sn.plugin_videoplayer.muxer;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.os.Environment;
import android.util.Log;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MMuxer {

    private String TAG = "MMuxer";

    private String mPath;

    private MediaMuxer mMediaMuxer;

    private int mVideoTrackIndex = -1;
    private int mAudioTrackIndex = -1;

    private boolean mIsAudioTrackAdd = false;
    private boolean mIsVideoTrackAdd = false;

    private boolean mIsStart = false;

    public MMuxer() {
        init();
    }

    /**
     * 第一步，初始化
     */
    private void init() {
        String format = new SimpleDateFormat("yyyyMM_dd-HHmmss").format(new Date());
        String fileName = "LVideo_" + format + ".mp4";
        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
        mPath = filePath + fileName;
        try {
            // 原生封装器MediaMuxer，用于将已经编码好的音视频流数据封装到指定格式的文件中，
            // MediaMuxer支持MP4、Webm、3GP三种封装格式。一般使用MP4格式。
            mMediaMuxer = new MediaMuxer(mPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 第二步，添加音视频轨道，设置音视频数据流格式，并启动封装器
     */
    public void addVideoTrack(MediaFormat mediaFormat) {
        if (mMediaMuxer != null) {
            try {
                mVideoTrackIndex = mMediaMuxer.addTrack(mediaFormat);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
            mIsVideoTrackAdd = true;
            startMuxer();
        }
    }

    public void addAudioTrack(MediaFormat mediaFormat) {
        if (mMediaMuxer != null) {
            try {
                mAudioTrackIndex = mMediaMuxer.addTrack(mediaFormat);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
            mIsAudioTrackAdd = true;
            startMuxer();
        }
    }

    private void startMuxer() {
        if (mIsAudioTrackAdd && mIsVideoTrackAdd) {
            mMediaMuxer.start();
            mIsStart = true;
            Log.i(TAG, "启动混合器，等待数据输入...");
        }
    }

    /**
     * 忽略音频轨道
     */
    public void setNoAudio() {
        if (mIsAudioTrackAdd) return;
        mIsAudioTrackAdd = true;
        startMuxer();
    }

    /**
     * 忽略视频轨道
     */
    public void setNoVideo() {
        if (mIsVideoTrackAdd) return;
        mIsVideoTrackAdd = true;
        startMuxer();
    }


    /**
     * 第三步，写入数据，也很简单，将解封得到的数据写入即可。
     *
     * @param byteBuffer
     * @param bufferInfo
     */
    public void writeVideoData(ByteBuffer byteBuffer, MediaCodec.BufferInfo bufferInfo) {
        if (mIsStart) {
            mMediaMuxer.writeSampleData(mVideoTrackIndex, byteBuffer, bufferInfo);
        }
    }

    public void writeAudioData(ByteBuffer byteBuffer, MediaCodec.BufferInfo bufferInfo) {
        if (mIsStart) {
            mMediaMuxer.writeSampleData(mAudioTrackIndex, byteBuffer, bufferInfo);
        }
    }


    /**
     * 第四步，释放封装器，完成封装过程
     * 这一步非常重要，必须要释放之后，才能生成可用的完整的MP4文件==
     */

    public void release() {
        mIsAudioTrackAdd = false;
        mIsVideoTrackAdd = false;
        try {
            mMediaMuxer.stop();
            mMediaMuxer.release();
            mMediaMuxer = null;
            Log.i(TAG, "混合器退出...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
