//
// 音视频解码基类
// Author: Chen Xiaoping
// Create Date: 2019-08-02
//

#include "base_decoder.h"
#include "../../utils/timer.c"
#include "../../jsoncpp/value.h"

class Value;

BaseDecoder::BaseDecoder(JNIEnv *env, jobject obj, jstring path, bool for_synthesizer)
        : m_for_synthesizer(for_synthesizer) {
    m_obj = env->NewGlobalRef(obj);
    javaCallback = new Callback(env, obj);
    Init(env, path);
    CreateDecodeThread(m_obj);
}

void BaseDecoder::Init(JNIEnv *env, jstring path) {
    m_path_ref = env->NewGlobalRef(path);
    m_path = env->GetStringUTFChars(path, NULL);
    //获取JVM虚拟机，为创建线程作准备
    env->GetJavaVM(&m_jvm_for_thread);
}

BaseDecoder::~BaseDecoder() {
    if (m_format_ctx != NULL) delete m_format_ctx;
    if (m_codec_ctx != NULL) delete m_codec_ctx;
    if (m_frame != NULL) delete m_frame;
    if (m_packet != NULL) delete m_packet;
}

void BaseDecoder::CreateDecodeThread(jobject obj) {
    // 使用智能指针，线程结束时，自动删除本类指针
    std::shared_ptr<BaseDecoder> that(this);
    std::thread t(Decode, that, obj);
    t.detach();
}

void BaseDecoder::Decode(std::shared_ptr<BaseDecoder> that, jobject obj) {
    JNIEnv *env;
    //将线程附加到虚拟机，并获取env
    const char *decoderTag = that->LogSpec();
    if (that->m_jvm_for_thread->AttachCurrentThread(&env, NULL) != JNI_OK) {
        LOG_ERROR(that->TAG, decoderTag, "Fail to Init decode thread");
        return;
    }
    Callback *pCallback = new Callback(env, obj);
    int x = 123;
    pCallback->callbackS(playerInfoCallback, decoderTag, "DecodeThread create Successful");

//    if (env == NULL) {
//        LOGE("Decode", "env is null")
//        return;
//    }
//    if (obj == NULL) {
//        LOGE("Decode", "obj is null")
//        return;
//    }
//
//    //http://blog.sina.com.cn/s/blog_439abfdd0101iql1.html
//    jobject gJavaObj = env->NewGlobalRef(obj);
//    jclass thiz = env->GetObjectClass(gJavaObj);
//    jmethodID nativeCallback = env->GetMethodID(thiz, "nativeCallback", "(I)V");
//    if (nativeCallback == NULL) {
//        LOGE("Decode", "nativeCallback is null")
//    }
//    env->CallVoidMethod(obj, nativeCallback, 10000);

    that->CallbackState(PREPARE);

    that->InitFFMpegDecoder(env, pCallback);

    pCallback->callbackS(playerInfoCallback, decoderTag, "InitFFMpegDecoder successful");

    LOG_ERROR(that->TAG, decoderTag, "InitFFMpegDecoder successful");

    that->AllocFrameBuffer();
    av_usleep(1000);
    pCallback->callbackS(playerInfoCallback, decoderTag, "AllocFrameBuffer successful");
    LOG_ERROR(that->TAG, decoderTag, "AllocFrameBuffer successful");

    that->Prepare(env);
    pCallback->callbackS(playerInfoCallback, decoderTag, "Prepare successful");
    LOG_ERROR(that->TAG, decoderTag, "Prepare successful");

    that->LoopDecode(env, obj);
    pCallback->callbackS(playerInfoCallback, decoderTag, "LoopDecode successful");
    LOG_ERROR(that->TAG, decoderTag, "LoopDecode successful");

    that->DoneDecode(env);
    pCallback->callbackS(playerInfoCallback, decoderTag, "DoneDecode successful");
    LOG_ERROR(that->TAG, decoderTag, "DoneDecode successful");

    that->CallbackState(STOP);
    pCallback->callbackS(playerInfoCallback, decoderTag, "STOP successful");

    //解除线程和jvm关联
    that->m_jvm_for_thread->DetachCurrentThread();

}

void BaseDecoder::InitFFMpegDecoder(JNIEnv *env, Callback *callback) {
    LOG_ERROR(TAG, LogSpec(), "InitFFMpegDecoder start");
    //1，初始化上下文
    m_format_ctx = avformat_alloc_context();
    LOG_ERROR(TAG, LogSpec(), "InitFFMpegDecoder m_format_ctx");
    //2，打开文件
    if (avformat_open_input(&m_format_ctx, m_path, NULL, NULL) != 0) {
        LOG_ERROR(TAG, LogSpec(), "Fail to open file [%s]", m_path);
        DoneDecode(env);
        return;
    }
    //3，获取音视频流信息
    int info = avformat_find_stream_info(m_format_ctx, NULL);
    LOG_ERROR(TAG, LogSpec(), "avformat_find_stream_info info ： %d", info);
    if (info < 0) {
        LOG_ERROR(TAG, LogSpec(), "Fail to find stream info");
        DoneDecode(env);
        return;
    }
    //4，查找编解码器
    //4.1 获取视频流的索引
    int vIdx = -1;//存放视频流的索引
    unsigned int streams = m_format_ctx->nb_streams;
    LOG_ERROR(TAG, LogSpec(), "streams size %u", streams);
    for (int i = 0; i < streams; ++i) {
        if (m_format_ctx->streams[i]->codecpar->codec_type == GetMediaType()) {
            vIdx = i;
            LOG_ERROR(TAG, LogSpec(), "InitFFMpegDecoder %d", vIdx);
            break;
        }
    }
    LOG_ERROR(TAG, LogSpec(), "vIdx %d", vIdx)
    if (vIdx == -1) {
        LOG_ERROR(TAG, LogSpec(), "Fail to find stream index")
        DoneDecode(env);
        return;
    }
    m_stream_index = vIdx;

    //4.2 获取解码器参数
    AVStream *pStream = m_format_ctx->streams[vIdx];
    AVCodecParameters *codecPar = pStream->codecpar;

    //4.3 获取解码器
//    m_codec = avcodec_find_decoder_by_name("h264_mediacodec");//硬解码
    m_codec = avcodec_find_decoder(codecPar->codec_id);

    //4.4 获取解码器上下文
    m_codec_ctx = avcodec_alloc_context3(m_codec);
    if (avcodec_parameters_to_context(m_codec_ctx, codecPar) != 0) {
        LOG_ERROR(TAG, LogSpec(), "Fail to obtain av codec context");
        DoneDecode(env);
        return;
    }

    //5，打开解码器
    if (avcodec_open2(m_codec_ctx, m_codec, NULL) < 0) {
        LOG_ERROR(TAG, LogSpec(), "Fail to open av codec");
        DoneDecode(env);
        return;
    }

    if (codecPar->codec_type == AVMEDIA_TYPE_VIDEO) {
        m_duration = (int) (m_format_ctx->duration / AV_TIME_BASE);
        int64_t frames = pStream->nb_frames;
        m_bit_rate = (int) (m_format_ctx->bit_rate / 1000);
        int m_width = pStream->codecpar->width;
        int m_height = pStream->codecpar->height;
        user["duration"] = m_duration;
        user["bitRate"] = m_bit_rate;
        user["frames"] = frames;
        user["width"] = m_width;
        user["height"] = m_height;
        const char *string = user.toStyledString().c_str();
        callback->callbackS(videoInfoCallback, string);
    }

    if (codecPar->codec_type == AVMEDIA_TYPE_AUDIO){
       int m_channels = codecPar->channels; //采样率
       int sample_rate = codecPar->sample_rate;  //通道数
        user["channels"] = m_channels;
        user["sample_rate"] = sample_rate;
        const char *string = user.toStyledString().c_str();
        callback->callbackS(audioInfoCallback, string);
    }


    LOG_INFO(TAG, LogSpec(), "Decoder init success ")
}

void BaseDecoder::AllocFrameBuffer() {
    // 初始化待解码和解码数据结构
    // 1）初始化AVPacket，存放解码前的数据
    m_packet = av_packet_alloc();
    // 2）初始化AVFrame，存放解码后的数据
    m_frame = av_frame_alloc();
}

void BaseDecoder::LoopDecode(JNIEnv *env, jobject obj) {
    if (STOP == m_state) { // 如果已被外部改变状态，维持外部配置
        m_state = START;
    }
    CallbackState(START);


    while (1) {
        LOG_INFO(TAG, LogSpec(), " -- LoopDecode start --")
        if (m_state != DECODING &&
            m_state != START &&
            m_state != STOP) {
            CallbackState(m_state);
            Wait();
            LOG_INFO(TAG, LogSpec(), "Decoder run into Wait, state：%s", GetStateStr())
            CallbackState(m_state);
            // 恢复同步起始时间，去除等待流失的时间
            m_started_t = GetCurMsTime() - m_cur_t_s;
        }

        if (m_state == STOP) {
            LOG_INFO(TAG, LogSpec(), "m_state == STOP")
            break;
        }
        if (-1 == m_started_t) {
            m_started_t = GetCurMsTime();
        }


        if (DecodeOneFrame() != NULL) {
            LOG_INFO(TAG, LogSpec(), "DecodeOneFrame= %s", "successful")
            SyncRender();
            Render(m_frame, env, obj);
            if (m_state == START) {
                m_state = PAUSE;
            }
        } else {
            LOG_INFO(TAG, LogSpec(), "m_state = %d", m_state)
            if (ForSynthesizer()) {
                m_state = STOP;
            } else {
                m_state = FINISH;
            }
            CallbackState(FINISH);
        }
        LOG_INFO(TAG, LogSpec(), "-- LoopDecode end --")
    }
}

AVFrame *BaseDecoder::DecodeOneFrame() {
    //从媒体流中读取帧填充到填充到Packet的数据缓存空间。

    if (m_SeekPosition > 0) {
        int64_t timestamp_in_stream_time_base = av_rescale_q(m_SeekPosition * AV_TIME_BASE,
                                                             AV_TIME_BASE_Q,
                                                             time_base());
        int64_t pts = m_SeekPosition / av_q2d(time_base());
        LOG_INFO(TAG, LogSpec(),
                 " -- timestamp_in_stream_time_base -- %lli --   %lli  -- %i , -- %i",
                 timestamp_in_stream_time_base, pts, time_base().den, time_base().num);
        int64_t seek_target = static_cast<int64_t>(m_SeekPosition * 1000000);//微秒
        int64_t seek_min = INT64_MIN;
        int64_t seek_max = INT64_MAX;
        int seek_ret = avformat_seek_file(m_format_ctx, m_stream_index, seek_min, pts, seek_max, 0);
//        int i = av_seek_frame(m_format_ctx, m_stream_index, pts,
//                              AVSEEK_FLAG_BACKWARD);
        if (seek_ret >= 0) {
            m_SeekPosition = 0;
        }//        avcodec_flush_buffers(m_codec_ctx);
//        GoOn();
    }

    int ret = av_read_frame(m_format_ctx, m_packet);
    while (ret == 0) {
        LOG_ERROR(TAG, LogSpec(), "start stream_index = %d", m_packet->stream_index)
        // stream_index Packet所在stream的index
        if (m_packet->stream_index == m_stream_index) {
            //发送数据到ffmepg，放到解码队列中
            LOG_ERROR(TAG, LogSpec(), "Decode error: %d", -2);
            switch (avcodec_send_packet(m_codec_ctx, m_packet)) {
                case AVERROR_EOF: {
                    av_packet_unref(m_packet);
                    LOG_ERROR(TAG, LogSpec(), "Decode error: %s", av_err2str(AVERROR_EOF));
                    return NULL; //解码结束
                }
                case AVERROR(EAGAIN):
                    LOG_ERROR(TAG, LogSpec(), "Decode error: %s", av_err2str(AVERROR(EAGAIN)));
                    break;
                case AVERROR(EINVAL):
                    LOG_ERROR(TAG, LogSpec(), "Decode error: %s", av_err2str(AVERROR(EINVAL)));
                    break;
                case AVERROR(ENOMEM):
                    LOG_ERROR(TAG, LogSpec(), "Decode error: %s", av_err2str(AVERROR(ENOMEM)));
                    break;
                default:
                    LOG_ERROR(TAG, LogSpec(), "Decode error: %s", av_err2str(AVERROR(ENOMEM)));
                    break;
            }
            LOG_ERROR(TAG, LogSpec(), "m_frame = %d", m_frame->key_frame)
            LOG_INFO(TAG, LogSpec(), "m_codec_ctx = %d", m_codec_ctx->codec_type)
            //TODO 这里需要考虑一个packet有可能包含多个frame的情况
            //将成功的解码队列中取出1个frame
            int result = avcodec_receive_frame(m_codec_ctx, m_frame);
            LOG_INFO(TAG, LogSpec(), "result = %d", result)
            LOG_INFO(TAG, LogSpec(), "m_frame.pts = %d", m_frame->pts)
            if (result == 0) {
                ObtainTimeStamp();
                LOG_INFO(TAG, LogSpec(), "successful stream_index = %d", m_packet->stream_index)
                av_packet_unref(m_packet);
                LOG_INFO(TAG, LogSpec(), "m_frame= %d", m_frame->key_frame)
                return m_frame;
            } else {
                LOG_INFO(TAG, LogSpec(), "Receive frame error result: %s",
                         av_err2str(AVERROR(result)))
            }
        }
        // 释放packet
        LOG_INFO(TAG, LogSpec(), "m_packet->stream_index  = false")
        av_packet_unref(m_packet);
        ret = av_read_frame(m_format_ctx, m_packet);
    }
    LOG_INFO(TAG, LogSpec(), "end stream_index = %d", m_packet->stream_index)
    av_packet_unref(m_packet);
    LOG_INFO(TAG, LogSpec(), "ret = %s", av_err2str(AVERROR(ret)))
    return NULL;
}


void BaseDecoder::CallbackState(DecodeState status) {
    if (m_state_cb != NULL) {
        switch (status) {
            case PREPARE:
                m_state_cb->DecodePrepare(this);
                break;
            case START:
                m_state_cb->DecodeReady(this);
                break;
            case DECODING:
                m_state_cb->DecodeRunning(this);
                break;
            case PAUSE:
                m_state_cb->DecodePause(this);
                break;
            case FINISH:
                m_state_cb->DecodeFinish(this);
                break;
            case STOP:
                m_state_cb->DecodeStop(this);
                break;
        }
    }
}

void BaseDecoder::ObtainTimeStamp() {
    if (LogSpec() == "VideoDecoder") {
        LOG_INFO(TAG, LogSpec(), "ObtainTimeStamp frame %u dts= %lli , pts = %lli ",
                 m_frame->pict_type, m_packet->dts, m_packet->pts)
    }
    if (m_frame->pkt_dts != AV_NOPTS_VALUE) {
        m_cur_t_s = m_packet->dts;
        if (LogSpec() == "VideoDecoder") {
            LOG_INFO(TAG, LogSpec(), "ObtainTimeStamp  m_packet->dts   %i ", m_cur_t_s)
        }
    } else if (m_frame->pts != AV_NOPTS_VALUE) {
        m_cur_t_s = m_frame->pts;
        if (LogSpec() == "VideoDecoder") {
            LOG_INFO(TAG, LogSpec(), "ObtainTimeStamp  m_frame->pts   %i ", m_cur_t_s)
        }
    } else {
        m_cur_t_s = 0;
        if (LogSpec() == "VideoDecoder") {
            LOG_INFO(TAG, LogSpec(), "ObtainTimeStamp m_cur_t_s  %i ", m_cur_t_s)
        }
    }
    m_cur_t_s = (int64_t) ((m_cur_t_s * av_q2d(m_format_ctx->streams[m_stream_index]->time_base)) *
                           1000);
    if (LogSpec() == "VideoDecoder") {
        LOG_INFO(TAG, LogSpec(), "ObtainTimeStamp m_cur_t_s  %i  time_base %d  %d", m_cur_t_s,
                 m_format_ctx->streams[m_stream_index]->time_base.den,
                 m_format_ctx->streams[m_stream_index]->time_base.num)
    }

}

void BaseDecoder::SyncRender() {
    LOG_INFO(TAG, LogSpec(), "SyncRender= %s", "successful")
    if (ForSynthesizer()) {
//        av_usleep(15000);
        return;
    }
    //TODO: 解决前进seek播放暂停问题 ，暂时注解
//    int64_t ct = GetCurMsTime();
//    int64_t passTime = ct - m_started_t;
//    if (m_cur_t_s > passTime) {
//        av_usleep((unsigned int) ((m_cur_t_s - passTime) * 1000));
//    }
    LOG_INFO(TAG, LogSpec(), "SyncRender= %s", "end")
}

void BaseDecoder::DoneDecode(JNIEnv *env) {
    LOG_INFO(TAG, LogSpec(), "Decode done and decoder release")
    // 释放缓存
    if (m_packet != NULL) {
        av_packet_free(&m_packet);
    }
    if (m_frame != NULL) {
        av_frame_free(&m_frame);
    }
    // 关闭解码器
    if (m_codec_ctx != NULL) {
        avcodec_close(m_codec_ctx);
        avcodec_free_context(&m_codec_ctx);
    }
    // 关闭输入流
    if (m_format_ctx != NULL) {
        avformat_close_input(&m_format_ctx);
        avformat_free_context(m_format_ctx);
    }
    // 释放转换参数
    if (m_path_ref != NULL && m_path != NULL) {
        env->ReleaseStringUTFChars((jstring) m_path_ref, m_path);
        env->DeleteGlobalRef(m_path_ref);
    }

    // 通知子类释放资源
    Release();
}

void BaseDecoder::Wait(long second, long ms) {
    LOG_INFO(TAG, LogSpec(), "Decoder run into wait, state：%s", GetStateStr())
    pthread_mutex_lock(&m_mutex);
    LOG_INFO(TAG, LogSpec(), "Decoder run into wait, state：%d", 1)
    if (second > 0 || ms > 0) {
        LOG_INFO(TAG, LogSpec(), "Decoder run into wait, state：%d", 2)
        timeval now;
        timespec outtime;
        gettimeofday(&now, NULL);
        int64_t destNSec = now.tv_usec * 1000 + ms * 1000000;
        outtime.tv_sec = static_cast<__kernel_time_t>(now.tv_sec + second + destNSec / 1000000000);
        outtime.tv_nsec = static_cast<long>(destNSec % 1000000000);
        pthread_cond_timedwait(&m_cond, &m_mutex, &outtime);
    } else {
        LOG_INFO(TAG, LogSpec(), "Decoder run into wait, state：%f", 2.1)
        //阻塞当前线程，等待其他唤醒
        pthread_cond_wait(&m_cond, &m_mutex);
    }
    pthread_mutex_unlock(&m_mutex);
}

void BaseDecoder::SendSignal() {
//    LOG_INFO(TAG, LogSpec(), "Decoder wake up, state: %s", GetStateStr())
    pthread_mutex_lock(&m_mutex);
    pthread_cond_signal(&m_cond);
    pthread_mutex_unlock(&m_mutex);
}


int BaseDecoder::CurrentTime() {

}


int BaseDecoder::VideoTotalTime() {
    char info[40960] = {0};
    //4，获取音视频流信息
    LOG_INFO(TAG, LogSpec(), "VideoTime = start")
    if (m_format_ctx->duration != AV_NOPTS_VALUE) {
        int hours, mins, secs, us;
        int64_t duration = m_format_ctx->duration + 5000;
        LOG_INFO(TAG, LogSpec(), "VideoTime = m_format_ctx%lld", m_format_ctx->duration)
//        secs = duration / AV_TIME_BASE;
//        us = duration % AV_TIME_BASE;
//        mins = secs / 60;
//        secs %= 60;
//        hours = mins / 60;
//        mins %= 60;
        return duration / AV_TIME_BASE;
    }
    LOG_INFO(TAG, LogSpec(), "VideoTotalTime = 0")
    return 0;
}

void BaseDecoder::setMediaSeekTime(int time) {
    char info[40960] = {0};
    //4，获取音视频流信息
    LOG_INFO(TAG, LogSpec(), "VideoTime = start")
    m_SeekPosition = time;
//    Stop();
//
//    int64_t timestamp_in_stream_time_base = av_rescale_q(time * AV_TIME_BASE, AV_TIME_BASE_Q,
//                                                         time_base());
//    int64_t pts = time / av_q2d(time_base());
//
//
//    LOG_INFO(TAG, LogSpec(), " -- timestamp_in_stream_time_base -- %lli --   %lli  -- %i , -- %i",
//             timestamp_in_stream_time_base, pts, time_base().den, time_base().num);
//    int64_t seek_target = static_cast<int64_t>(time * 1000000);//微秒
//    int64_t seek_min = INT64_MIN;
//    int64_t seek_max = INT64_MAX;
//    int seek_ret = avformat_seek_file(m_format_ctx, -1, seek_min, seek_target, seek_max, 0);
////    av_seek_frame(m_format_ctx, m_stream_index, pts,
////                  AVSEEK_FLAG_ANY);
//    avcodec_flush_buffers(m_codec_ctx);
//    GoOn();
//        if (m_state == START) {
//            m_state = DECODING;
//        }
//    avcodec_flush_buffers(m_codec_ctx);
//    if (m_format_ctx->duration != AV_NOPTS_VALUE) {
//        int hours, mins, secs, us;
//        int64_t duration = m_format_ctx->duration + 5000;
//        LOG_INFO(TAG, LogSpec(), "VideoTime = m_format_ctx%lld", m_format_ctx->duration)
//        secs = duration / AV_TIME_BASE;
//        us = duration % AV_TIME_BASE;
//        mins = secs / 60;
//        secs %= 60;
//        hours = mins / 60;
//        mins %= 60;
//        LOG_ERROR(TAG, LogSpec(), "%02d:%02d:%02d.%02d\n", hours, mins, secs,
//                  (100 * us) / AV_TIME_BASE)
//        snprintf(info, 40960, "%02d:%02d:%02d", hours, mins, secs);
//        return info;
//    }
//    LOG_INFO(TAG, LogSpec(), "VideoTime = NUll")
//    return NULL;
}

void BaseDecoder::GoOn() {
    m_state = DECODING;
    SendSignal();
}


void BaseDecoder::Pause() {
    m_state = PAUSE;
}

void BaseDecoder::Stop() {
    m_state = STOP;
    SendSignal();
}

bool BaseDecoder::IsRunning() {
    return DECODING == m_state;
}

long BaseDecoder::GetDuration() {
    return m_duration;
}

long BaseDecoder::GetCurPos() {
    return (long) m_cur_t_s;
}
