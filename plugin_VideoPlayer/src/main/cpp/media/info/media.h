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
};

class Media {

public:
    Media(const char *filePath);

    ~Media();

    char*  getMediaInfo();

private:
    AVFormatContext *ac = NULL;

    AVStream *pVStream = NULL;

    int openCode;

    int videoIndex = -1;

};



#endif //VIDEOPLAYER_MEDIA_H
