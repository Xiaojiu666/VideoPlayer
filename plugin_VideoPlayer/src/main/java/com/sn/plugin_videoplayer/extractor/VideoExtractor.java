package com.sn.plugin_videoplayer.extractor;

import android.media.MediaFormat;

import java.nio.ByteBuffer;

public class VideoExtractor implements IExtractor {
    private MMExtractor mMediaExtractor;

    public VideoExtractor(String path) {
        mMediaExtractor = new MMExtractor(path);
    }


    @Override
    public MediaFormat getFormat() {
        return mMediaExtractor.getVideoFormat();
    }

    @Override
    public int readBuffer(ByteBuffer byteBuffer) {
        return mMediaExtractor.readBuffer(byteBuffer);
    }

    @Override
    public Long getCurrentTimestamp() {
        return mMediaExtractor.getCurrentTimestamp();
    }

    @Override
    public int getSampleFlag() {
        return mMediaExtractor.getSampleFlag();
    }

    @Override
    public Long seek(long pos) {
        return mMediaExtractor.seek(pos);
    }

    @Override
    public void setStartPos(Long pos) {
        mMediaExtractor.setStartPos(pos);
    }

    @Override
    public void stop() {
        mMediaExtractor.stop();
    }
}
