//
// Created by edz on 2021/4/9.
//

#ifndef VIDEOPLAYER_BASE_DECODER_H
#define VIDEOPLAYER_BASE_DECODER_H

#include <jni.h>
#include <string>
#include <thread>
#include "../../utils/logger.h"
#include "i_decoder.h"
#include "decode_state.h"

extern "C" {
#include <libavcodec/avcodec.h>
#include <libavformat/avformat.h>
#include <libavutil/frame.h>
#include <libavutil/time.h>
};
class BaseDecoder :IDecoder {
private:
    const char *TAG = "BaseDecoder";

    //-------------定义解码相关------------------------------
    // 解码信息上下文
    AVFormatContext *m_format_ctx = NULL;

    // 解码器
    AVCodec *m_codec = NULL;

    // 解码器上下文
    AVCodecContext *m_codec_ctx = NULL;

    // 待解码包
    AVPacket *m_packet = NULL;

    // 最终解码数据
    AVFrame *m_frame = NULL;

    // 当前播放时间
    int64_t m_cur_t_s = 0;

    // 总时长
    long m_duration = 0;

    // 开始播放的时间
    int64_t m_started_t = -1;

    // 解码状态
    DecodeState m_state = STOP;

    // 数据流索引
    int m_stream_index = -1;

    // -------------------定义线程相关-----------------------------
    // 线程依附的JVM环境
    JavaVM *m_jvm_for_thread = NULL;

    // 原始路径jstring引用，否则无法在线程中操作
    jobject m_path_ref = NULL;

    // 经过转换的路径
    const char *m_path = NULL;

    // 线程等待锁变量
    pthread_mutex_t m_mutex = PTHREAD_MUTEX_INITIALIZER;
    pthread_cond_t m_cond = PTHREAD_COND_INITIALIZER;

    // 为合成器提供解码
    bool m_for_synthesizer = false;


    //-----------------私有方法------------------------------------

    /**
     * 初始化
     * @param env jvm环境
     * @param path 本地文件路径
     */
    void Init(JNIEnv *env, jstring path);

    /**
     * 初始化FFMpeg相关的参数
     * @param env jvm环境
     */
    void InitFFMpegDecoder(JNIEnv * env);

    /**
     * 分配解码过程中需要的缓存
     */
    void AllocFrameBuffer();

    /**
     * 新建解码线程
     */
    void CreateDecodeThread();

    /**
     * 循环解码
     */
    void LoopDecode();

    /**
     * 获取当前帧时间戳
     */
    void ObtainTimeStamp();

    /**
     * 解码完成
     * @param env jvm环境
     */
    void DoneDecode(JNIEnv *env);

    /**
     * 静态解码方法，用于解码线程回调
     * @param that 当前解码器
     */
    static void Decode(std::shared_ptr<BaseDecoder> that);

    /**
     * 时间同步
     */
    void SyncRender();



public:

    //--------构造方法和析构方法-------------

    BaseDecoder(JNIEnv *env, jstring path);
    virtual ~BaseDecoder();

    //--------实现基础类方法-----------------

    void GoOn() override;
    void Pause() override;
    void Stop() override;
    bool IsRunning() override;
    long GetDuration() override;
    long GetCurPos() override;

    /**
    * 视频宽度
    * @return
    */
    int width() {
        return m_codec_ctx->width;
    }

    /**
     * 视频高度
     * @return
     */
    int height() {
        return m_codec_ctx->height;
    }

    long duration() {
        return m_duration;
    }
};


#endif //VIDEOPLAYER_BASE_DECODER_H
