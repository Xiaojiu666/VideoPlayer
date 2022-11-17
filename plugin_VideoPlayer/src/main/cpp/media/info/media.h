//
// Created by edz on 2022/11/17.
//

#ifndef VIDEOPLAYER_MEDIA_H
#define VIDEOPLAYER_MEDIA_H

#include <jni.h>
#include <string>

extern "C" {
#include <libavcodec/avcodec.h>
#include <libavformat/avformat.h>
#include <libavutil/frame.h>
#include <libavutil/time.h>
#include <libpng/png.h>
#include <libavutil/imgutils.h>
#include <libswscale/swscale.h>
};

class Media {

public:
    Media(const char *filePath);

    ~Media();

    char *getMediaInfo();

    void openCodec();

    void generatePng(const char *filePath);


private:
    AVFormatContext *ac = NULL;

    AVStream *pVStream = NULL;

    AVCodecContext *pCodecCtx = NULL;

    int openCode;

    int videoIndex = -1;

    AVFrame *pFrame = av_frame_alloc();

    AVFrame *pFrameRGB = av_frame_alloc();

    AVPacket packet;

    int frameFinished;

    int i = 0;

};


#endif //VIDEOPLAYER_MEDIA_H
