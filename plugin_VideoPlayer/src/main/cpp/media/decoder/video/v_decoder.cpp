//
// 视频解码器
// Author: Chen Xiaoping
// Create Date: 2019-08-02
//

#include "v_decoder.h"
#include "../../one_frame.h"

VideoDecoder::VideoDecoder(JNIEnv *env, jobject obj, jstring path, bool for_synthesizer)
        : BaseDecoder(env, obj, path, for_synthesizer) {
//    jobject gJavaObj = env->NewGlobalRef(obj);
//    jclass thiz = env->GetObjectClass(gJavaObj);
//    jmethodID nativeCallback = env->GetMethodID(thiz,"nativeCallback","(I)V");
//    if (nativeCallback == NULL){
//        LOGE(TAG, "nativeCallback is null")
//    }
//    env->CallVoidMethod(obj,nativeCallback,100);
}

VideoDecoder::~VideoDecoder() {
    delete m_video_render;
}

void VideoDecoder::SetRender(VideoRender *render) {
    this->m_video_render = render;
}

void VideoDecoder::Prepare(JNIEnv *env) {
    InitRender(env);
    InitBuffer();
    InitSws();
}

void VideoDecoder::InitRender(JNIEnv *env) {
    if (m_video_render != NULL) {
        int dst_size[2] = {-1, -1};
        m_video_render->InitRender(env, width(), height(), dst_size);
        m_dst_w = dst_size[0];
        m_dst_h = dst_size[1];
        if (m_dst_w == -1) {
            m_dst_w = width();
        }
        if (m_dst_h == -1) {
            m_dst_w = height();
        }
        LOGI(TAG, "dst %d, %d", m_dst_w, m_dst_h)
    } else {
        LOGE(TAG, "Init render error, you should call SetRender first!")
    }
}

void VideoDecoder::InitBuffer() {
    m_rgb_frame = av_frame_alloc();
    // 获取缓存大小
    // 函数的作用是通过指定像素格式、图像宽、图像高来计算所需的内存大小
    int numBytes = av_image_get_buffer_size(DST_FORMAT, m_dst_w, m_dst_h, 1);
    // 分配内存
    m_buf_for_rgb_frame = (uint8_t *) av_malloc(numBytes * sizeof(uint8_t));
    // 将内存分配给RgbFrame，并将内存格式化为三个通道后，分别保存其地址
    av_image_fill_arrays(m_rgb_frame->data, m_rgb_frame->linesize,
                         m_buf_for_rgb_frame, DST_FORMAT, m_dst_w, m_dst_h, 1);
}

void VideoDecoder::InitSws() {
    // 初始化格式转换工具
    LOG_INFO(TAG, LogSpec(), "InitSws= %d", video_pixel_format())
    LOG_INFO(TAG, LogSpec(), "InitSws= %d", DST_FORMAT)
    //https://blog.csdn.net/u011686167/article/details/121875305
    m_sws_ctx = sws_getContext(width(), height(), video_pixel_format(),
                               m_dst_w, m_dst_h, DST_FORMAT,
                               SWS_FAST_BILINEAR, NULL, NULL, NULL);
}

void VideoDecoder::Render(AVFrame *frame, JNIEnv *env, jobject obj) {
    LOG_INFO(TAG, LogSpec(), "Render start")
    LOG_INFO(TAG, LogSpec(), "Render= %d", frame->key_frame)
    double d = frame->best_effort_timestamp * av_q2d(time_base());
    LOG_INFO(TAG, LogSpec(), "Render best_effort_timestamp = %f",
             d)

    if (env == nullptr) {
        LOG_ERROR(TAG, LogSpec(), "m_env is NULL")
        return;
    }
    if (obj == nullptr) {
        LOG_ERROR(TAG, LogSpec(), "m_Obj is NULL")
        return;
    }
    jobject gJavaObj = env->NewGlobalRef(obj);
    jclass thiz = env->GetObjectClass(gJavaObj);
    jmethodID nativeCallback = env->GetMethodID(thiz, "nativeCallback", "(D)V");
    if (nativeCallback == NULL) {
        LOGE(TAG, "nativeCallback is null")
    }
    env->CallVoidMethod(obj, nativeCallback, d);

    sws_scale(m_sws_ctx, frame->data, frame->linesize, 0,
              height(), m_rgb_frame->data, m_rgb_frame->linesize);

    OneFrame *one_frame = new OneFrame(m_rgb_frame->data[0], m_rgb_frame->linesize[0], frame->pts,
                                       time_base(), NULL, false);
    m_video_render->Render(one_frame);

    if (m_state_cb != NULL) {
        if (m_state_cb->DecodeOneFrame(this, one_frame)) {
            LOGE(TAG, "[VIDEO] release")
            Wait(0, 200);
        }
    }
    LOG_INFO(TAG, LogSpec(), "Render end")
}

bool VideoDecoder::NeedLoopDecode() {
    return true;
}

void VideoDecoder::Release() {
    LOGE(TAG, "[VIDEO] release")
    if (m_rgb_frame != NULL) {
        av_frame_free(&m_rgb_frame);
        m_rgb_frame = NULL;
    }
    if (m_buf_for_rgb_frame != NULL) {
        free(m_buf_for_rgb_frame);
        m_buf_for_rgb_frame = NULL;
    }
    if (m_sws_ctx != NULL) {
        sws_freeContext(m_sws_ctx);
        m_sws_ctx = NULL;
    }
    if (m_video_render != NULL) {
        m_video_render->ReleaseRender();
        m_video_render = NULL;
    }
}


