//
// Created by edz on 2021/4/6.
//
#include <jni.h>
#include <string>
#include <unistd.h>
//
//extern "C" {
//    #include <libavcodec/avcodec.h>
//    #include <libavformat/avformat.h>
//    #include <libavfilter/avfilter.h>
//    #include <libavcodec/jni.h>
//
//    JNIEXPORT jstring JNICALL
//    Java_com_sn_videoplayer_ffmpeg_demo_DemoNativeInterface_ffmpegInfo(JNIEnv *env,
//                                                                       jobject  /* this */) {
//
//        char info[40000] = {0};
//        AVCodec *c_temp = av_codec_next(NULL);
//        while (c_temp != NULL) {
//            if (c_temp->decode != NULL) {
//                sprintf(info, "%sdecode:", info);
//                switch (c_temp->type) {
//                    case AVMEDIA_TYPE_VIDEO:
//                        sprintf(info, "%s(video):", info);
//                        break;
//                    case AVMEDIA_TYPE_AUDIO:
//                        sprintf(info, "%s(audio):", info);
//                        break;
//                    default:
//                        sprintf(info, "%s(other):", info);
//                        break;
//                }
//                sprintf(info, "%s[%10s]\n", info, c_temp->name);
//            } else {
//                sprintf(info, "%sencode:", info);
//            }
//            c_temp = c_temp->next;
//        }
//        return env->NewStringUTF(info);
//    }
//}


extern "C" {
    #include <libavcodec/avcodec.h>
        #include <libavformat/avformat.h>
        #include <libavfilter/avfilter.h>
        #include <libavcodec/jni.h>
    JNIEXPORT jstring JNICALL
    Java_com_sn_videoplayer_ffmpeg_demo_DemoNativeInterface_stringFromJNI(JNIEnv *env,
                                                                          jobject /* this */) {
//        std::string hello = "(*^__^*) 嘻嘻……~Hello from C++ 隔壁老李头";
//        return env->NewStringUTF(hello.c_str());
        char info[40000] = {0};
        AVCodec *c_temp = av_codec_next(NULL);
        while (c_temp != NULL) {
            if (c_temp->decode != NULL) {
                sprintf(info, "%sdecode:", info);
                switch (c_temp->type) {
                    case AVMEDIA_TYPE_VIDEO:
                        sprintf(info, "%s(video):", info);
                        break;
                    case AVMEDIA_TYPE_AUDIO:
                        sprintf(info, "%s(audio):", info);
                        break;
                    default:
                        sprintf(info, "%s(other):", info);
                        break;
                }
                sprintf(info, "%s[%10s]\n", info, c_temp->name);
            } else {
                sprintf(info, "%sencode:", info);
            }
            c_temp = c_temp->next;
        }

        return env->NewStringUTF(info);
    }
}

