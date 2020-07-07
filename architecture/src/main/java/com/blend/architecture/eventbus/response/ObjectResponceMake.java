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
            //通过反射执行代码
            return mMethod.invoke(mObject,mParameters);
        } catch (IllegalAccessException e) {
            exception = e;
        } catch (InvocationTargetException e) {
            exception = e;
        }
        return null;
    }

    /*
    反射去加载getInstance方法并得到单例。
    这里其实可以不把方法名写成getInstance也行，但是前提是在A进程中要在一开始就将类put到objectCenter
     */
    @Override
    protected void setMethod(RequestBean requestBean) {
        mObject = OBJECT_CENTER.getObject(reslutClass.getName());
        Method method = typeCenter.getMethod(mObject.getClass(), requestBean);
        mMethod = method;
    }
}
