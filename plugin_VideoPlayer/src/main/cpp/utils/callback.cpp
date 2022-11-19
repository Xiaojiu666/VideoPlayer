//
// Created by edz on 2022/11/18.
//

#include "callback.h"
#include "logger.h"

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

void Callback::callbackS(const char *name, const char *tag, const char *value) {
    char info[4096] = {0};
    sprintf(info, "[%s] %s", tag, value);
    callbackS(name, info);
}



