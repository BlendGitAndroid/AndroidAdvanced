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
        this.clazz = clazz;
        this.hermeService = service;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Log.i(TAG, "invoke:-------> " + method.getName());
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
