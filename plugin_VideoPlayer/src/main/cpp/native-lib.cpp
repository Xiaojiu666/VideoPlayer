//
// Created by edz on 2021/4/6.
//
#include <jni.h>
#include <string>
#include <unistd.h>
#include <random>
#include <__locale>
#include "media/player/def_player/player.h"
#include "utils/logger.h"

const char *TAG = "AVCodec info";


extern "C" {
#include <libavcodec/avcodec.h>
#include <libavformat/avformat.h>
#include <libavfilter/avfilter.h>
#include <libavcodec/jni.h>
int volatile gIsThreadStop = 0;

JNIEXPORT jstring JNICALL
Java_com_sn_videoplayer_ffmpeg_demo_DemoNativeInterface_ffmpegInfo(JNIEnv *env,
                                                                   jobject /* this */) {

    char info[40000] = {0};
    AVCodec *c_temp = av_codec_next(NULL);
//    while (c_temp != NULL) {
//        if (c_temp->decode != NULL) {
//            //数据写入某个字符串中
////                sprintf(info, "decode: %s", info);
////                LOGE(TAG, "decode before info %s",info)
//            switch (c_temp->type) {
//                case AVMEDIA_TYPE_VIDEO:
//                    sprintf(info, "%s(video):", info);
////                        LOGE(TAG, "%s(video):", info)
//                    break;
//                case AVMEDIA_TYPE_AUDIO:
//                    sprintf(info, "%s(audio):", info);
//                    break;
//                default:
//                    sprintf(info, "%s(other):", info);
//                    break;
//            }
//            LOGE(TAG, "decode after %s", info)
//            sprintf(info, "%s[%10s]\n", info, c_temp->name);
//            LOGE(TAG, "c_temp->name %s", c_temp->name)
//        } else {
////                sprintf(info, "%sencode:", info);
//        }
//        c_temp = c_temp->next; // ->等于取值
//    }

    return env->NewStringUTF(info);
}

JNIEXPORT jint JNICALL
Java_com_sn_videoplayer_ffmpeg_demo_DemoNativeInterface_createPlayer(JNIEnv *env,
                                                                     jobject obj/* this */,
                                                                     jstring path,
                                                                     jobject surface) {
    Player *player = new Player(env, obj, path, surface);
    return (jint) player;
}

JNIEXPORT void JNICALL
Java_com_sn_videoplayer_ffmpeg_demo_DemoNativeInterface_play(JNIEnv *env,
                                                             jobject  /* this */,
                                                             jint player) {
    Player *p = (Player *) player;
    p->play();
}

JNIEXPORT void JNICALL
Java_com_sn_videoplayer_ffmpeg_demo_DemoNativeInterface_pause(JNIEnv *env,
                                                              jobject  /* this */,
                                                              jint player) {
    Player *p = (Player *) player;
    p->pause();
}
//    https://www.cnblogs.com/seven-sky/p/7205932.html
JNIEXPORT void JNICALL
Java_com_sn_videoplayer_ffmpeg_demo_DemoNativeInterface_threadStart(JNIEnv *env,
                                                                    jobject  /* this */) {

}

JNIEXPORT void JNICALL
Java_com_sn_videoplayer_ffmpeg_demo_DemoNativeInterface_threadStop(JNIEnv *env,
                                                                   jobject  /* this */) {

}

JNIEXPORT jstring JNICALL
Java_com_sn_videoplayer_ffmpeg_demo_DemoNativeInterface_videoTime(JNIEnv *env, jobject thiz,
                                                                  jint player) {
    Player *p = (Player *) player;
    return env->NewStringUTF(p->viedeotime());
}

JNIEXPORT jint JNICALL
Java_com_sn_videoplayer_ffmpeg_demo_DemoNativeInterface_videoTotalTime(JNIEnv *env, jobject thiz,
                                                                       jint player) {
    Player *p = (Player *) player;
    return (jint) p->videoTimeTotal();
}

jstring StringToJString(JNIEnv *env, const std::string &nativeString) {
    return env->NewStringUTF(nativeString.c_str());
}


JNIEXPORT jstring JNICALL
Java_com_sn_videoplayer_ffmpeg_demo_DemoNativeInterface_videoInfo(JNIEnv *env, jobject thiz,
                                                                  jstring path) {
    AVFormatContext *ac = NULL;
    std::string videoInfo = "";
    const char *filepaht = env->GetStringUTFChars(path, NULL);
    int ret = avformat_open_input(&ac, filepaht, NULL, NULL);
    //https://www.php.cn/csharp-article-414735.html
    if (ret < 0) {
        videoInfo.append("open video error \n");
    } else {
        videoInfo.append("open video success  \n");
    }
    int videoindex = -1;
    for (int i = 0; i < ac->nb_streams; i++) {
        if (ac->streams[i]->codecpar->codec_type == AVMEDIA_TYPE_VIDEO) {
            videoindex = i;
            break;
        }
    }
    videoInfo += "streams size  = " + std::to_string(videoindex) + " \n";

    if (videoindex == -1) {
        printf("Didn't find a video stream.\n");
    }


    AVStream * pVStream = ac->streams[videoindex];

    //https://blog.csdn.net/qq_41824928/article/details/103631719
    videoInfo += "duration = " + std::to_string(ac->duration / 1000000) + "s\n";
    videoInfo += "bitrate = " + std::to_string(ac->bit_rate / 1000) + "kb/s\n";
    videoInfo += "totalFrames = " + std::to_string(pVStream-> nb_frames) + " 帧\n";
    videoInfo += "width  = " + std::to_string( pVStream->codecpar->width) +  "，height  = " + std::to_string( pVStream->codecpar->height) +"  \n";
    LOGE(TAG, "videoInfo %s", videoInfo.c_str())

    AVCodecParameters *pCodecParam=pVStream->codecpar;
    AVCodec * pCodec = avcodec_find_decoder(pCodecParam->codec_id);
    if (!pCodec) {
        fprintf(stderr, "Codec not found\n");
        exit(1);
    }
    fprintf(stderr, "codecName = %s\n", pCodec->long_name);
    // 打开视频编码器
    //https://blog.csdn.net/qq_44857505/article/details/127305880
    AVCodecContext *pCodecCtx = avcodec_alloc_context3(pCodec); // avcodec_alloc_context3的作用是分配一个AVCodecContext并设置默认值
    if (avcodec_parameters_to_context(pCodecCtx, pCodecParam) < 0)
    {
        printf("Couldn't copy codec context.\r\n");
    }
    if (avcodec_open2(pCodecCtx, pCodec, NULL) < 0)
    {
        printf("Couldn't open codec.\r\n");
    }

    AVFrame *pFrame = av_frame_alloc();
    AVFrame *pFrameRGB = av_frame_alloc();

// 给视频解码
    AVPacket packet;
    int frameFinished;
    int i = 0;
    while (av_read_frame(ac, &packet) >= 0)
    {
        if (packet.stream_index == videoindex)
        {
            avcodec_send_packet(pCodecCtx, &packet);
            frameFinished = avcodec_receive_frame(pCodecCtx, pFrame);
            // 保存packet
            if (frameFinished == 0)
            {
                if(i > 10){
                    char filename[1024];
                    //把yuv数据保存为png图片
                    sprintf(filename, "frame%d.png", i);
                    FILE *fp = fopen(filename, "wb");
                    if (fp == NULL)
                    {
                        printf("open file error\r\n");
                    }
                    png_structp png_ptr = png_create_write_struct(PNG_LIBPNG_VER_STRING, NULL, NULL, NULL);
                    if (png_ptr == NULL)
                    {
                        printf("png_create_write_struct error\r\n");
                    }
                    png_infop info_ptr = png_create_info_struct(png_ptr);
                    if (info_ptr == NULL)
                    {
                        printf("png_create_info_struct error\r\n");
                    }
                    if (setjmp(png_jmpbuf(png_ptr)))
                    {
                        printf("setjmp error\r\n");
                    }
                    png_init_io(png_ptr, fp);
                    png_set_IHDR(png_ptr, info_ptr, pCodecCtx->width, pCodecCtx->height, 8, PNG_COLOR_TYPE_RGB, PNG_INTERLACE_NONE, PNG_COMPRESSION_TYPE_BASE, PNG_FILTER_TYPE_BASE);
                    png_write_info(png_ptr, info_ptr);
                    // pFrame->data[0]是Y分量，pFrame->data[1]是U分量，pFrame->data[2]是V分量 转换为RGB
                    uint8_t *out_buffer = (uint8_t *)av_malloc(av_image_get_buffer_size(AV_PIX_FMT_RGB24, pCodecCtx->width, pCodecCtx->height, 1));
                    if(out_buffer == NULL)
                    {
                        printf("av_malloc error\r\n");
                    }
                    av_image_fill_arrays(pFrameRGB->data, pFrameRGB->linesize, out_buffer, AV_PIX_FMT_RGB24, pCodecCtx->width, pCodecCtx->height, 1);
                    struct SwsContext *img_convert_ctx = sws_getContext(pCodecCtx->width, pCodecCtx->height, pCodecCtx->pix_fmt, pCodecCtx->width, pCodecCtx->height, AV_PIX_FMT_RGB24, SWS_BICUBIC, NULL, NULL, NULL);
                    if(img_convert_ctx == NULL)
                    {
                        printf("sws_getContext error\r\n");
                    }
                    sws_scale(img_convert_ctx, (const uint8_t* const*)pFrame->data, pFrame->linesize, 0, pCodecCtx->height, pFrameRGB->data, pFrameRGB->linesize);
                    sws_freeContext(img_convert_ctx);
                    //把yuv数据保存为png图片
                    png_bytep row_pointers[pCodecCtx->height]; // 每一行的指针 数组 用于写入 png 图片
                    for (int i = 0; i < pCodecCtx->height; i++)
                    {
                        row_pointers[i] = pFrameRGB->data[0] + i * pFrameRGB->linesize[0]; //
                    }

                    // yuv有三个通道，分别是Y,U,V
                    // Y通道的数据是连续的，U和V通道的数据是交错的
                    // YUV420P的数据格式是YYYYYYYYUUVV
                    // 现在要把YUV420P的数据可以直接存入png图片么
                    // for (int i = 0; i < pCodecCtx->height; i++)
                    // {
                    //     row_pointers[i] = pFrame->data[0] + i * pFrame->linesize[0];
                    // }
                    png_write_image(png_ptr, row_pointers);
                    png_write_end(png_ptr, NULL);
                    png_destroy_write_struct(&png_ptr, &info_ptr);
                    printf("save frame%d.png\r\n", i);
                    fclose(fp);
                    // 保存yuv数据到图片文件
                    // sprintf(filename, "../video_resources/frame%d.yuv", i);
                    // FILE *pFile = fopen(filename, "wb");
                    // if (pFile == NULL)
                    // {
                    //     printf("Couldn't open file.\r \n");
                    //     return -1;
                    // }
                    // // 打印图片宽高
                    // printf("width: %d, height: %d\r\n", pFrame->width, pFrame->height);
                    // fwrite(pFrame->data[0], 1, pFrame->linesize[0] * pCodecCtx->height, pFile);
                    // fwrite(pFrame->data[1], 1, pFrame->linesize[1] * pCodecCtx->height / 2, pFile);
                    // fwrite(pFrame->data[2], 1, pFrame->linesize[2] * pCodecCtx->height / 2, pFile);
                    // fclose(pFile);
                }
                i++;
                printf("frame%d\r\n", i);
            }

            // if (frameFinished == 0) //
            // {
            //     i++;
            //     if (i > 100)
            //     {
            //         break;
            //     }
            // }
        }
        av_packet_unref(&packet);
    }


    av_dump_format(ac, 0, filepaht, 0);
    avformat_close_input(&ac);
    return env->NewStringUTF(videoInfo.c_str());
}


}

