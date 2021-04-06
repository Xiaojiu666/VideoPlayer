package com.sn.videoplayer.media_codec.decoder;

import com.sn.videoplayer.media_codec.Frame;

public interface IDecoderStateListener {

    void decoderPrepare(BaseDecoder decodeJob);

    void decoderReady(BaseDecoder decodeJob);

    void decoderRunning(BaseDecoder decodeJob);

    void decoderPause(BaseDecoder decodeJob);

    void decodeOneFrame(BaseDecoder decodeJob, Frame frame);

    void decoderFinish(BaseDecoder decodeJob);

    void decoderDestroy(BaseDecoder decodeJob);

    void decoderError(BaseDecoder decodeJob, String msg);
}
