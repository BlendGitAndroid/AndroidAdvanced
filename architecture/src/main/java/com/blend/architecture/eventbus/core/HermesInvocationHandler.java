package com.blend.architecture.eventbus.core;

import android.text.TextUtils;
import android.util.Log;

import com.blend.architecture.eventbus.Responce;
import com.blend.architecture.eventbus.response.ResponceBean;
import com.blend.architecture.eventbus.service.HermesService;
import com.google.gson.Gson;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

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
    三个参数：
    Object proxy：就是代理对象，newProxyInstance方法的返回对象
    Method method：调用的方法
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
