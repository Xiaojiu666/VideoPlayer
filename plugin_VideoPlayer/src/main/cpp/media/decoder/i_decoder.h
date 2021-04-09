//
// Created by edz on 2021/4/9.
//

#ifndef VIDEOPLAYER_I_DECODER_H
#define VIDEOPLAYER_I_DECODER_H

//这是一个纯虚类，类似 Java 的 interface
class IDecoder{
public:
    virtual void GoOn() = 0;
    virtual void Pause() = 0;
    virtual void Stop() = 0;
    virtual bool IsRunning() = 0;
    virtual long GetDuration() = 0;
    virtual long GetCurPos() = 0;
};

#endif //VIDEOPLAYER_I_DECODER_H
