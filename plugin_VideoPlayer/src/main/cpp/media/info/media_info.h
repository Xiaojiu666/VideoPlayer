//
// Created by edz on 2022/11/17.
//

#ifndef VIDEOPLAYER_MEDIA_INFO_H
#define VIDEOPLAYER_MEDIA_INFO_H


class MediaInfo {
private:

public:
    MediaInfo(JNIEnv *jniEnv, jobject obj, jstring path, jobject surface);
    ~MediaInfo();

};


#endif //VIDEOPLAYER_MEDIA_INFO_H
