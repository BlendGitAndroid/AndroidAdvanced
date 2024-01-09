package com.blend.architecture.aop.imitation_taobao;

import android.content.Context;
import android.content.Intent;

import com.blend.architecture.aop.imitation_taobao.util.SharePreferenceUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class MyHandler implements InvocationHandler {
    private Object target;
    private Context context;

    public MyHandler(Context context) {
        this.target = context;
        this.context = context;
    }

    /**
     * 这个invoke就是拦截Object对象的所有方法
     * <p>
     * 使用动态代理，就是在这里进行拦截，跳转到登陆界面
     *
     * @param proxy
     * @param method
     * @param args
     * @return
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = null;
        if (SharePreferenceUtil.getBooleanSp(SharePreferenceUtil.IS_LOGIN, context)) {
            result = method.invoke(target, args);
        } else {
            Intent intent = new Intent(context, LoginActivity.class);
            context.startActivity(intent);
        }
        return result;
    }
}
