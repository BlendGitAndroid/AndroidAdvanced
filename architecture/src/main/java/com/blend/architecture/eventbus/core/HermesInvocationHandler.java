package com.blend.architecture.eventbus.core;

import android.text.TextUtils;

import com.blend.architecture.eventbus.Responce;
import com.blend.architecture.eventbus.response.ResponceBean;
import com.blend.architecture.eventbus.service.HermesService;
import com.google.gson.Gson;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 将每一步请求都封装成Request对象通过aidl传递到A进程处理，
 * 再将处理结果进行返回。
 */
public class HermesInvocationHandler implements InvocationHandler {
    private static final String TAG = "blend";
    private Class clazz;
    private static final Gson GSON = new Gson();
    private Class hermeService;

    public HermesInvocationHandler(Class<? extends HermesService> service, Class clazz) {
        this.hermeService = service;
        this.clazz = clazz;
    }

    /*
    创建一个类，实现这个方法，利用反射在invoke()方法里实现需求

    三个参数：
    Object proxy：指代我们所代理的那个真实对象
    Method method：指代的是我们所要调用真实对象的某个方法的Method对象
    Object[] args：指代的是调用真实对象某个方法时接受的参数
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //生成单例，返回客户端
        Responce responce = Hermes.getDefault().sendObjectRequest(hermeService, clazz, method, args);
        if (!TextUtils.isEmpty(responce.getData())) {
            ResponceBean responceBean = GSON.fromJson(responce.getData(), ResponceBean.class);
            if (responceBean.getData() != null) {
                Object getUserReslut = responceBean.getData();

                String data = GSON.toJson(getUserReslut);

                Class stringgetUser = method.getReturnType();   //getReturnType():该对象表示的方法的返回类型

                Object o = GSON.fromJson(data, stringgetUser);
                return o;

            }
        }
        return null;
    }
}
