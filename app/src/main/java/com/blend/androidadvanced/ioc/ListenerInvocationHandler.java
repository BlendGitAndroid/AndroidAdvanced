package com.blend.androidadvanced.ioc;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ListenerInvocationHandler implements InvocationHandler {
    //     OnClickListener  1
//    MainActivity   2
    private Object activity;
    private Method activityMethod;

    public ListenerInvocationHandler(Object activity, Method activityMethod) {
        this.activity = activity;
        this.activityMethod = activityMethod;
    }

    /**
     * 按钮点下去就执行这个方法
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //在这里去调用被注解了的click()
        return activityMethod.invoke(activity, args);
    }
}
