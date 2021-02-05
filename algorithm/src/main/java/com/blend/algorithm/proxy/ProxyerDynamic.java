package com.blend.algorithm.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

class ProxyerDynamic implements InvocationHandler {

    private Object factory;

    public ProxyerDynamic(Object o) {
        factory = o;
    }

    /**
     * 动态代理，就是在运行时，当代码执行到Proxy.newProxyInstance方法时，会在内存中自动生成接口的字节码文件
     *
     * 其实我认为在这个方法中做什么都可以
     * 参数：
     * o：这个o其实就是根据接口生成的代理类对象，这个代理类继承Proxy，实现这个自定义的接口
     * method:自定义接口内部定义的方法
     * objects:这个标识参数，就是自定义接口传进来的参数
     */
    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
        System.out.println("Start Dynamic:");
        Object invoke = method.invoke(factory, objects); //利用反射在这里进行调用
        return invoke; //这里返回上面方法调用的结果
    }
}
