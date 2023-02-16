# This is the Android makefile for libyuv for NDK.
LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)

LOCAL_CPP_EXTENSION := .cc		\
					.cpp

LOCAL_SRC_FILES := \
    source/compare.cc           \
    source/compare_common.cc    \
    source/compare_gcc.cc       \
    source/compare_mmi.cc       \
    source/compare_msa.cc       \
    source/compare_win.cc       \
    source/convert.cc           \
    source/convert_argb.cc      \
    source/convert_from.cc      \
    source/convert_from_argb.cc \
    source/convert_to_argb.cc   \
    source/convert_to_i420.cc   \
    source/cpu_id.cc            \
    source/planar_functions.cc  \
    source/rotate.cc            \
    source/rotate_any.cc        \
    source/rotate_argb.cc       \
    source/rotate_common.cc     \
    source/rotate_gcc.cc        \
    source/rotate_mmi.cc        \
    source/rotate_msa.cc        \
    source/rotate_win.cc        \
    source/row_any.cc           \
    source/row_common.cc        \
    source/row_gcc.cc           \
    source/row_mmi.cc           \
    source/row_msa.cc           \
    source/row_win.cc           \
    source/scale.cc             \
    source/scale_any.cc         \
    source/scale_argb.cc        \
    source/scale_common.cc      \
    source/scale_gcc.cc         \
    source/scale_mmi.cc         \
    source/scale_msa.cc         \
    source/scale_uv.cc          \
    source/scale_win.cc         \
    source/video_common.cc

ifeq ($(TARGET_ARCH_ABI),armeabi-v7a)
    LOCAL_ARM_NEON := true
    LOCAL_CFLAGS += -DLIBYUV_NEON
    LOCAL_SRC_FILES += \
        source/compare_neon.cc.neon    \
        source/rotate_neon.cc.neon     \
        source/row_neon.cc.neon        \
        source/scale_neon.cc.neon
endif

ifeq ($(TARGET_ARCH_ABI),arm64-v8a)
   LOCAL_ARM_NEON := true
   LOCAL_CFLAGS += -DLIBYUV_NEON
   LOCAL_SRC_FILES += \
       source/compare_neon64.cc.neon    \
       source/rotate_neon64.cc.neon     \
       source/row_neon64.cc.neon        \
       source/scale_neon64.cc.neon
endif

LOCAL_SRC_FILES += \
				yuvutils.c 			\
				bitmap2i420.cpp

LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/include
LOCAL_C_INCLUDES += $(LOCAL_PATH)/include
LOCAL_LDLIBS := -llog -ljnigraphics
LOCAL_LDLIBS += -L$(SYSROOT)/usr/lib
LOCAL_MODULE := libyuv
LOCAL_MODULE_TAGS := optional

include $(BUILD_SHARED_LIBRARY)