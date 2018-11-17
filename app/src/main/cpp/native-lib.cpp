#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL Java_root_iv_protection_MainActivity_stringFromJNI(JNIEnv *env, jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

extern "C"
JNIEXPORT jstring JNICALL
Java_root_iv_protection_MainActivity_getString(JNIEnv *env, jobject instance) {

    return env->NewStringUTF("Igor");
}