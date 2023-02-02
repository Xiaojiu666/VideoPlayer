//
// Created by edz on 2022/11/18.
//

#include "callback.h"
#include "logger.h"
#include "../jsoncpp/value.h"

static const char *TAG = "Callback";

//https://blog.csdn.net/qq_27278957/article/details/77164353
Callback::Callback(JNIEnv *env, jobject obj) {
    jniEnv = env;
    jobJect = obj;
}

Callback::~Callback() {

}

void Callback::callbackI(const char *methodName, int value) {
    jobject gJavaObj = jniEnv->NewGlobalRef(jobJect);
    jclass thiz = jniEnv->GetObjectClass(gJavaObj);
    jmethodID nativeCallback = jniEnv->GetMethodID(thiz, methodName, "(I)V");
    if (nativeCallback == NULL) {
        LOGE(TAG, "nativeCallback is null")
    }
    jniEnv->CallVoidMethod(gJavaObj, nativeCallback, value);
}


void Callback::callbackD(const char *methodName, double value) {
    jobject gJavaObj = jniEnv->NewGlobalRef(jobJect);
    jclass thiz = jniEnv->GetObjectClass(gJavaObj);
    jmethodID nativeCallback = jniEnv->GetMethodID(thiz, methodName, "(D)V");
    if (nativeCallback == NULL) {
        LOGE(TAG, "nativeCallback is null")
    }
    jniEnv->CallVoidMethod(gJavaObj, nativeCallback, value);
}


void Callback::callbackS(const char *methodName, const char *value) {
    LOGE(TAG, "nativeCallback callbackS")
    if (jniEnv == NULL || jobJect == NULL) {
        LOGE(TAG, "nativeCallback jniEnv jobJect is null")
        return;
    }
    jobject gJavaObj = jniEnv->NewGlobalRef(jobJect);
    jclass thiz = jniEnv->GetObjectClass(gJavaObj);
    jmethodID nativeCallback = jniEnv->GetMethodID(thiz, methodName, "(Ljava/lang/String;)V");
    if (nativeCallback == NULL) {
        LOGE(TAG, "nativeCallback is null")
    }
    jstring charst = jniEnv->NewStringUTF(value);
    jniEnv->CallVoidMethod(gJavaObj, nativeCallback, charst);
}

//传递对象参数 : https://blog.csdn.net/u011905195/article/details/112470909
//解决子线程 无法find自定义对象的方法 https://blog.csdn.net/lmh_19941113/article/details/85048184
//暂时废弃了，子线程使用json 字符串解决 传递数据问题
void Callback::callbackMediaInfo(const char *methodName,int value) {
    if (jniEnv == NULL || jobJect == NULL) {
        LOGE(TAG, "nativeCallback jniEnv jobJect is null")
        return;
    }
    jobject gJavaObj = jniEnv->NewGlobalRef(jobJect);
    jclass thiz = jniEnv->GetObjectClass(gJavaObj);

    jclass user_clazz = jniEnv->FindClass("com/sn/videoplayer/ffmpeg/demo/MediaInfo");

    //步骤2:获取方法名称ID
    jmethodID method_init_id = jniEnv->GetMethodID(user_clazz, "<init>", "()V");//拿到构造方法
    jmethodID method_set_id = jniEnv->GetMethodID(user_clazz, "setMediaDuration", "(I)V");//拿到构造方法
    LOGE(TAG, "nativeCallback user_clazz")

    jmethodID method_get_id = jniEnv->GetMethodID(user_clazz, "getMediaDuration",
                                                  "()Lcom/sn/videoplayer/ffmpeg/demo/MediaInfo;");//拿到构造方法
    LOGE(TAG, "nativeCallback user_clazz 1")

    //步骤3：
    //创建了User对象
    jobject user_obj = jniEnv->NewObject(user_clazz, method_init_id);
    int duration = 1024;
//    jstring jname = jniEnv->NewStringUTF(name.c_str());
    //步骤4:调用相应的方法
    jniEnv->CallVoidMethod(user_obj, method_set_id, value);//set函数没返回值
//    //调用方法
//    jstring result = (jstring) env->CallObjectMethod(user_obj, method_get_id);
//    // LOGD("result:%s",jstring2str(env,result).c_str());
//    LOGD("JniNative返回数据:%s",jstring2string(jniEnv,result).c_str());

//    jclass jclazz = jniEnv->GetObjectClass(thiz);
    LOGE(TAG, "nativeCallback user_clazz 3")
    jmethodID jmethodID = jniEnv->GetMethodID(thiz, "mediaInfoCallbackMsg",
                                              "(Lcom/sn/videoplayer/ffmpeg/demo/MediaInfo;)V");
    LOGE(TAG, "nativeCallback user_clazz 4")
    jniEnv->CallVoidMethod(gJavaObj, jmethodID, user_obj);
}

void Callback::callbackS(const char *name, const char *tag, const char *value) {
    char info[40960] = {0};
    sprintf(info, "[%s]", tag);
    LOGE(TAG, "callbackS d %s", info)
    sprintf(info, "[%s] %s", tag, value);
    LOGE(TAG, "callbackS d %s", info)
    callbackS(name, info);
}



