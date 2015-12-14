LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE := mz11-native
LOCAL_LDFLAGS := -Wl,--build-id
LOCAL_SRC_FILES := \
	/home/choco/AndroidStudioProjects/PaintMyRoom/app/src/main/jni/Android.mk \
	/home/choco/AndroidStudioProjects/PaintMyRoom/app/src/main/jni/native.c \

LOCAL_C_INCLUDES += /home/choco/AndroidStudioProjects/PaintMyRoom/app/src/main/jni
LOCAL_C_INCLUDES += /home/choco/AndroidStudioProjects/PaintMyRoom/app/src/debug/jni

include $(BUILD_SHARED_LIBRARY)
