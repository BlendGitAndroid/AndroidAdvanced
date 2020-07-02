package com.blend.architecture.eventbus.core;

import java.lang.reflect.Method;

class CoreSubscribeMethod {

    //注册方法
    private Method mMethod;

    //线程类型,定义的注解
    private CoreThreadMode mThreadMode;

    //参数类型
    private Class<?> mEventType;

    public CoreSubscribeMethod(Method method, CoreThreadMode threadMode, Class<?> eventType) {
        mMethod = method;
        mThreadMode = threadMode;
        mEventType = eventType;
    }

    public Method getMethod() {
        return mMethod;
    }

    public void setMethod(Method method) {
        mMethod = method;
    }

    public CoreThreadMode getThreadMode() {
        return mThreadMode;
    }

    public void setThreadMode(CoreThreadMode threadMode) {
        mThreadMode = threadMode;
    }

    public Class<?> getEventType() {
        return mEventType;
    }

    public void setEventType(Class<?> eventType) {
        mEventType = eventType;
    }
}
