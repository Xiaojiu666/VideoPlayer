//
// Created by cxp on 2020-03-18.
//

#include "player.h"
#include "../../render/video/native_render/native_render.h"
#include "../../render/audio/opensl_render.h"

//#include "../../render/audio/opensl_render.h"
//#include "../../render/video/opengl_render/opengl_render.h"
//#include "../../../opengl/drawer/proxy/def_drawer_proxy_impl.h"
//const char *TAG = "VideoDecoder";


Player::Player(JNIEnv *jniEnv, jobject obj, jstring path, jobject surface) {

    javaCallback = new Callback(jniEnv, obj);
    LOGE(TAG, "VideoDecoder Init")
    m_v_decoder = new VideoDecoder(jniEnv, obj, path);
    // 本地窗口播放
    LOGE(TAG, "NativeRender Init")
    m_v_render = new NativeRender(jniEnv, surface);
    m_v_decoder->SetRender(m_v_render);
    LOGE(TAG, "AudioDecoder Init")
    // 音频解码
    m_a_decoder = new AudioDecoder(jniEnv, obj, path, false);
    // 渲染
    m_a_render = new OpenSLRender();
    m_a_decoder->SetRender(m_a_render);
    LOGE(TAG, "AudioDecoder con")
}

Player::~Player() {
    // 此处不需要 delete 成员指针
    // 在BaseDecoder中的线程已经使用智能指针，会自动释放
}

void Player::play() {
    if (m_v_decoder != NULL) {
        m_v_decoder->GoOn();
        m_a_decoder->GoOn();
    }
}

void Player::pause() {
    if (m_v_decoder != NULL) {
        m_v_decoder->Pause();
        m_a_decoder->Pause();
    }
}

void Player::setMediaSeekTime(int time) {
    if (m_v_decoder != NULL) {
        m_v_decoder->setMediaSeekTime(time);
        m_a_decoder->setMediaSeekTime(time);
    }
}

int Player::videoTimeTotal() {
    if (m_v_decoder != NULL) {
        return m_v_decoder->VideoTotalTime();;
    }
    return 0;
}