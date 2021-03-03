package com.sn.plugin_videoplayer.decoder;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;


import com.sn.plugin_videoplayer.extractor.IExtractor;

import java.io.File;
import java.nio.ByteBuffer;


/**
 * 定义解码器
 */
@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
public abstract class BaseDecoder implements IDecoder {


    BaseDecoder(String mFilePath) {
        this.mFilePath = mFilePath;
    }


    public String TAG = getName();

    //-------------线程相关------------------------
    /**
     * 解码器是否在运行
     */
    private boolean mIsRunning = true;

    /**
     * 线程等待锁
     */
    private Object mLock = new Object();

    /**
     * 是否可以进入解码
     */
    private boolean mReadyForDecode = false;

    //---------------解码相关-----------------------
    /**
     * 音视频解码器
     */
    protected MediaCodec mCodec;

    /**
     * 音视频数据读取器
     */
    protected IExtractor mExtractor;

    /**
     * 解码输入缓存区
     */
    protected ByteBuffer[] mInputBuffers;

    /**
     * 解码输出缓存区
     */
    protected ByteBuffer[] mOutputBuffers;

    /**
     * 解码数据信息
     */
    private MediaCodec.BufferInfo mBufferInfo = new MediaCodec.BufferInfo();

    /**
     * 开始解码时间，用于音视频同步
     */
    private long mStartTimeForSync = -1L;

    private DecodeState mState = DecodeState.STOP;

    public IDecoderStateListener mStateListener;

    /**
     * 流数据是否结束
     */
    private boolean mIsEOS = false;

    protected int mVideoWidth = 0;

    protected int mVideoHeight = 0;

    private long mEndPos;

    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    public void setStateListener(IDecoderStateListener l) {
        mStateListener = l;
    }

    @Override
    public void pause() {
        mState = DecodeState.PAUSE;
        mIsRunning = true;
    }

    @Override
    public void goOn() {
        mState = DecodeState.DECODING;
        notifyDecode();
    }

    @Override
    public void stop() {
        mState = DecodeState.PAUSE;
    }


    @Override
    public boolean isDecoding() {
        return false;
    }

    @Override
    public boolean isSeeking() {
        return false;
    }

    @Override
    public boolean isStop() {
        return false;
    }


    @Override
    public int getWidth() {
        return 0;
    }

    @Override
    public int getHeight() {
        return 0;
    }

    @Override
    public long getDuration() {
        return 0;
    }

    @Override
    public int getRotationAngle() {
        return 0;
    }

    @Override
    public MediaFormat getMediaFormat() {
        return null;
    }

    @Override
    public int getTrack() {
        return 0;
    }

    @Override
    public String getFilePath() {
        return null;
    }


    @Override
    public void run() {
        Log.e(TAG, "Thread Name " + Thread.currentThread().getName());
        if (mState == DecodeState.STOP) {
            mState = DecodeState.START;
        }
        if (mStateListener != null)
            mStateListener.decoderPrepare(this);
        //【解码步骤：1. 初始化，并启动解码器】
        if (!init())
            return;
        Log.e(TAG, "  mIsRunning  " + mIsRunning);
        while (mIsRunning) {
            // 暂停解码
            if (mState != DecodeState.START &&
                    mState != DecodeState.DECODING &&
                    mState != DecodeState.SEEKING) {
                Log.e(TAG, " waitDecode " + mState);
                // ---------【同步时间矫正】-------------
                //恢复同步的起始时间，即去除等待流失的时间
                mStartTimeForSync = System.currentTimeMillis() - getCurTimeStamp();
                waitDecode();
            }

            if (!mIsRunning ||
                    mState == DecodeState.STOP) {
                Log.e(TAG, " STOPDecode  mIsRunning : " + mIsRunning + " , decodeState : " + mState);
                mIsRunning = false;
                break;
            }

            if (mStartTimeForSync == -1L) {
                mStartTimeForSync = System.currentTimeMillis();
            }

            //如果数据没有解码完毕，将数据推入解码器解码
            if (!mIsEOS) {
                //【解码步骤：2. 将数据压入解码器输入缓冲】
                mIsEOS = pushBufferToDecoder();
            }

            //【解码步骤：3. 将解码好的数据从缓冲区拉取出来】
            int index = pullBufferFromDecoder();
            if (index >= 0) {
                // ---------【音视频同步】-------------
                if (mState == DecodeState.DECODING) {
                    sleepRender();
                }

                //【解码步骤：4. 渲染】
                render(mOutputBuffers[index], mBufferInfo);
                //【解码步骤：5. 释放输出缓冲】
                mCodec.releaseOutputBuffer(index, true);
                if (mState == DecodeState.START) {
                    mState = DecodeState.PAUSE;
                }
            }
            //【解码步骤：6. 判断解码是否完成】
            if (mBufferInfo.flags == MediaCodec.BUFFER_FLAG_END_OF_STREAM) {
                mState = DecodeState.FINISH;
                mStateListener.decoderFinish(this);
            }
        }
        doneDecode();
        //【解码步骤：7. 释放解码器】
        release();
    }

    private long getCurTimeStamp() {
        return mBufferInfo.presentationTimeUs / 1000;
    }

    private void sleepRender() {
        try {
            long passTime = System.currentTimeMillis() - mStartTimeForSync;
            long curTime = getCurTimeStamp();
            if (curTime > passTime) {
                Thread.sleep(curTime - passTime);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void release() {
        try {
            mState = DecodeState.STOP;
            mIsEOS = false;
            mExtractor.stop();
            mCodec.stop();
            mCodec.release();
            mStateListener.decoderDestroy(this);
        } catch (Exception e) {
        }
    }

    /**
     * 结束解码
     */
    abstract void doneDecode();

    /**
     * 渲染
     */
    abstract void render(ByteBuffer outputBuffers,
                         MediaCodec.BufferInfo bufferInfo);


    /**
     * 通知解码线程继续运行
     */
    public void notifyDecode() {
        synchronized (mLock) {
            mLock.notifyAll();
        }
        if (mState == DecodeState.DECODING) {
            mStateListener.decoderRunning(this);
        }
    }

    private int pullBufferFromDecoder() {
        // 查询是否有解码完成的数据，index >=0 时，表示数据有效，并且index为缓冲区索引
        int index = mCodec.dequeueOutputBuffer(mBufferInfo, 2000);
        Log.e(TAG, "pullBufferFromDecoder" + index);
        switch (index) {
            case MediaCodec.INFO_OUTPUT_FORMAT_CHANGED:
                break;
            case MediaCodec.INFO_TRY_AGAIN_LATER:
                break;
            case MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED:
                mOutputBuffers = mCodec.getOutputBuffers();
                break;
            default:
                return index;
        }
        return -1;
    }

    private boolean pushBufferToDecoder() {
        //返回要用有效数据填充的输入缓冲区的索引,分块，因为有多块缓冲区
        //使（计算机待处理数据项）出列
        int inputBufferIndex = mCodec.dequeueInputBuffer(2000);
        Log.e(TAG, "pushBufferToDecoder" + inputBufferIndex);
        boolean isEndOfStream = false;

        if (inputBufferIndex >= 0) {
            ByteBuffer inputBuffer = mInputBuffers[inputBufferIndex];
            int sampleSize = mExtractor.readBuffer(inputBuffer);
            if (sampleSize < 0) {
                //如果数据已经取完，压入数据结束标志：BUFFER_FLAG_END_OF_STREAM
                mCodec.queueInputBuffer(inputBufferIndex, 0, 0,
                        0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                isEndOfStream = true;
            } else {
                mCodec.queueInputBuffer(inputBufferIndex, 0,
                        sampleSize, mExtractor.getCurrentTimestamp(), 0);
                //Log.e(TAG, "pushBufferToDecoder getCurrentTimestamp " + mExtractor.getCurrentTimestamp());
            }
        }
        return isEndOfStream;
    }

    /**
     * 解码线程进入等待
     */
    private void waitDecode() {
        try {
            if (mState == DecodeState.PAUSE) {
                mStateListener.decoderPause(this);
            }
            synchronized (mLock) {
                mLock.wait();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String mFilePath;

    private boolean init() {
        //1.检查参数是否完整
        if (mFilePath.isEmpty() || !new File(mFilePath).exists()) {
            Log.w(TAG, "文件路径为空");
            mStateListener.decoderError(this, "文件路径为空");
            return false;
        }
        //调用虚函数，检查子类参数是否完整
        if (!check()) return false;

        //2.初始化数据提取器
        mExtractor = initExtractor(mFilePath);
        if (mExtractor == null ||
                mExtractor.getFormat() == null) return false;

        //3.初始化参数
        if (!initParams()) return false;

        //4.初始化渲染器
        if (!initRender()) return false;

        //5.初始化解码器
        if (!initCodec()) return false;

        Log.e(TAG, "decoder init sucessful...");
        return true;
    }


    private boolean initCodec() {
        try {
            //1.根据音视频编码格式初始化解码器
            String type = mExtractor.getFormat().getString(MediaFormat.KEY_MIME);
            Log.e(TAG, "initCodec " + type);
            mCodec = MediaCodec.createDecoderByType(type);
            //2.配置解码器
            if (!configCodec(mCodec, mExtractor.getFormat())) {
                waitDecode();
            }
            //3.启动解码器
            mCodec.start();
            //4.获取解码器缓冲区
            mInputBuffers = mCodec.getInputBuffers();
            mOutputBuffers = mCodec.getOutputBuffers();
            Log.e(TAG, "Codec init sucessful...");
            for (int i = 0; i < mInputBuffers.length; i++){
                Log.e(TAG, "Codec init mInputBuffers" + mInputBuffers[i].toString());
            }

            for (int i = 0; i < mOutputBuffers.length; i++){
                Log.e(TAG, "Codec init mOutputBuffers" + mOutputBuffers[i].toString());
            }

//            Log.e(TAG, "Codec init mOutputBuffers" + mOutputBuffers.length);
        } catch (Exception e) {
            Log.e(TAG, "Exception " + e.getMessage());
            e.printStackTrace();
            return false;
        }
        return true;
    }


    private boolean initParams() {
        try {
            MediaFormat format = mExtractor.getFormat();
            long mDuration = format.getLong(MediaFormat.KEY_DURATION) / 1000;
            if (mEndPos == 0L)
                mEndPos = mDuration;
            initSpecParams(mExtractor.getFormat());
        } catch (Exception e) {
            return false;
        }
        return true;
    }


    /**
     * 检查子类参数
     */
    abstract boolean check();

    /**
     * 初始化数据提取器
     */
    abstract IExtractor initExtractor(String path);

    /**
     * 初始化子类自己特有的参数
     */
    abstract void initSpecParams(MediaFormat format);

    /**
     * 初始化渲染器
     */
    abstract boolean initRender();

    /**
     * 配置解码器
     */
    abstract boolean configCodec(MediaCodec codec, MediaFormat format);
}
