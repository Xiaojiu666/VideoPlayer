package com.sn.plugin_videoplayer;

import android.media.MediaCodec;

import java.nio.ByteBuffer;

public class Frame {
    ByteBuffer buffer;

    MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();

    void setBufferInfo(MediaCodec.BufferInfo info) {
        bufferInfo.set(info.offset, info.size, info.presentationTimeUs, info.flags);
    }

}
