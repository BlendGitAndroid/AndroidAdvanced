package com.blend.architecture.eventbus.response;

import com.blend.architecture.eventbus.bean.RequestBean;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ObjectResponceMake  extends ResponceMake {
    private Method mMethod;

    private Object mObject;

    @Override
    protected Object invokeMethod() {

        Exception exception;
        try {
            return mMethod.invoke(mObject,mParameters);
        } catch (IllegalAccessException e) {
            exception = e;
        } catch (InvocationTargetException e) {
            exception = e;
        }
        return null;
    }
    //  1
    @Override
    protected void setMethod(RequestBean requestBean) {
        mObject = OBJECT_CENTER.getObject(reslutClass.getName());
//        getUser()    ---->method
        Method method = typeCenter.getMethod(mObject.getClass(), requestBean);
        mMethod = method;
    }
}
