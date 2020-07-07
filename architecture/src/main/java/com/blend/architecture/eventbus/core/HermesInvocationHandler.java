package com.blend.architecture.eventbus.core;

import android.text.TextUtils;
import android.util.Log;

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
    private static final String TAG = "alan";
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
    Object proxy：就是代理对象，但可以使用getClass()方法，得到proxy的Class类从而取得实例的类信息，如方法列表，annotation等
    Method method：被动态代理类调用的方法，从中可得到方法名，参数类型，返回类型等等
    Object[] args：方法中的参数
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Log.i(TAG, "invoke:-------> " + method.getName());
        //生成单例，返回客户端
        Responce responce = Hermes.getDefault().sendObjectRequest(hermeService, clazz, method, args);
        if (!TextUtils.isEmpty(responce.getData())) {
            ResponceBean responceBean = GSON.fromJson(responce.getData(), ResponceBean.class);
            if (responceBean.getData() != null) {
                Object getUserReslut = responceBean.getData();
                String data = GSON.toJson(getUserReslut);
//
                Class stringgetUser = method.getReturnType();
                Object o = GSON.fromJson(data, stringgetUser);
                return o;

            }
        }
        return null;
    }
}
