package com.blend.algorithm.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

class ProxyerDynamic implements InvocationHandler {

    private Object factory;

    public ProxyerDynamic(Object o) {
        factory = o;
    }

    /**
     * 其实我认为在这个方法中做什么都可以
     */
    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
        System.out.println("Start Dynamic:");
        Object invoke = method.invoke(factory, objects); //利用反射在这里进行调用
        return invoke; //这里返回上面方法调用的结果
    }
}
