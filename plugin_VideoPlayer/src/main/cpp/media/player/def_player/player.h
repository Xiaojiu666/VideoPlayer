//
// Created by cxp on 2020-03-18.
//

#ifndef LEARNINGVIDEO_PLAYER_H
#define LEARNINGVIDEO_PLAYER_H


#include "../../decoder/video/v_decoder.h"
#include "../../decoder/audio/a_decoder.h"
#include "../../utils/callback.h"
//#include "../../../opengl/drawer/video_drawer.h"
//#include "../../../opengl/drawer/proxy/drawer_proxy.h"
//#include "../../render/video/opengl_render/opengl_render.h"

class Player {
private:
    VideoDecoder *m_v_decoder;
    VideoRender *m_v_render;

    AudioDecoder *m_a_decoder;
    AudioRender *m_a_render;

    Callback *javaCallback;

    static jstring pathA;


public:
    Player(JNIEnv *jniEnv,jobject obj, jstring path, jobject surface);

    ~Player();

    void play();

    void pause();

    void setMediaSeekTime(int time);

    int videoTimeTotal();

};


#endif //LEARNINGVIDEO_PLAYER_H
