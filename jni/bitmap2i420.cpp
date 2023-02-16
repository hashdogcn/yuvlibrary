//
// Created by 张桂华 on 2022/5/13.
//
#include <android/bitmap.h>
#include "include/libyuv.h"
#include <jni.h>
#include<android/log.h>
#include <exception>
#include <iostream>
#include <sstream>
using namespace std;

extern "C"
JNIEXPORT jstring JNICALL
Java_cn_hashdog_yuvlibrary_YuvUtils_bitmap2i420WithC(JNIEnv *env, jclass type,
                                                 jbyteArray dst_argb_,
                                                 jobject bitmap,
                                                 jint width, jint height) {
    jbyte *dst = env->GetByteArrayElements(dst_argb_, JNI_FALSE);

    // 获取 bitmap 的信息
    AndroidBitmapInfo info;
    std::string status = "";
    ostringstream oss;
    int infoStatusInt = AndroidBitmap_getInfo(env, bitmap, &info);
    if (0 == infoStatusInt){//获取bitmap信息成功
        int src_width = info.width;
        int src_height = info.height;

        int src_stride_argb = src_width * 4;//info.stride
        int dst_plane1_size = src_width * src_height;

        uint8_t *dst_buffer_y = (uint8_t *)dst;
        uint8_t *dst_buffer_u = dst_buffer_y + dst_plane1_size;
        //四舍五入
        uint8_t *dst_buffer_v = dst_buffer_u + dst_plane1_size / 4;
        //uint8* dst_buffer_v = dst_buffer_u + (src_width + 1) / 2 * (src_height + 1) / 2;
        int dst_stride_y = src_width;
        //uv分量四舍五入
        int dst_stride_u = (src_width + 1) / 2;
        int dst_stride_v = (src_width + 1) / 2;

        uint8_t* dst_argb = NULL;
        int lockPixelsStatusInt = AndroidBitmap_lockPixels(env, bitmap, (void**)&dst_argb);
        if (0 == lockPixelsStatusInt) {//锁定画布
            if (NULL != dst_argb){
                try{
                   libyuv::ABGRToI420(dst_argb, src_stride_argb,
                                    dst_buffer_y, dst_stride_y,
                                    dst_buffer_u, dst_stride_u,
                                    dst_buffer_v, dst_stride_v,
                                    width, height);
                } catch(std::exception e) {
                    oss << "ABGRToI420 catch:" << e.what();
                }
            } else {
                oss << "dst_argb == NULL";
            }
        } else {
            oss << "AndroidBitmap_lockPixels:code=" << lockPixelsStatusInt << ",width=" << info.width << ",height=" << info.height << ",format=" << info.format;
        }
        AndroidBitmap_unlockPixels(env, bitmap);// 解锁画布
    }else{
        oss << "AndroidBitmap_getInfo:code=" << infoStatusInt;
    }
    env->ReleaseByteArrayElements(dst_argb_, dst, 0);
    status = oss.str();
    return env->NewStringUTF(status.c_str());
}