package com.blend.architecture.eventbus.core;

import android.text.TextUtils;
import android.util.Log;

import com.blend.architecture.eventbus.bean.RequestBean;
import com.blend.architecture.eventbus.bean.RequestParameter;
import com.blend.architecture.eventbus.util.TypeUtils;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

public class TypeCenter {
    private static final TypeCenter ourInstance = new TypeCenter();

    public static TypeCenter getInstance() {
        return ourInstance;
    }

    //name   String
    private final ConcurrentHashMap<String, Class<?>> mAnnotatedClasses;
    //类对应的方法 app --class  --->n   method String  name
    private final ConcurrentHashMap<Class<?>, ConcurrentHashMap<String, Method>> mRawMethods;

    private TypeCenter() {
        mAnnotatedClasses = new ConcurrentHashMap<String, Class<?>>();
        mRawMethods = new ConcurrentHashMap<Class<?>, ConcurrentHashMap<String, Method>>();
    }
    public void register(Class<?> clazz) {
        //分为注册类  注册方法
        registerClass(clazz);
        registerMethod(clazz);

    }




    public Method getMethod(Class<?> clazz, RequestBean requestBean) {
        String name = requestBean.getMethodName();//
        if ( name!= null) {
            Log.i("david", "getMethod: 1======="+name);
            mRawMethods.putIfAbsent(clazz, new ConcurrentHashMap<String, Method>());
            ConcurrentHashMap<String, Method> methods = mRawMethods.get(clazz);
            Method method = methods.get(name);
            if (method != null) {
                Log.i("david", "getMethod: "+method.getName());
                return method;
            }
            int pos = name.indexOf('(');

            Class[] paramters = null;
            RequestParameter[] requestParameters = requestBean.getRequestParameter();
            if (requestParameters != null && requestParameters.length > 0) {
                paramters = new Class[requestParameters.length];
                for (int i=0;i<requestParameters.length;i++) {
                    paramters[i]=getClassType(requestParameters[i].getParameterClassName());
                }
            }
            method = TypeUtils.getMethod(clazz, name.substring(0, pos), paramters);
            methods.put(name, method);
            return method;
        }
        return null;


    }
    public Class<?> getClassType(String name)   {
        if (TextUtils.isEmpty(name)) {
            return null;
        }
        Class<?> clazz = mAnnotatedClasses.get(name);
        if (clazz == null) {
            try {
                clazz = Class.forName(name);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return clazz;
    }
    //mAnnotatedMethods 填充
    private void registerMethod(Class<?> clazz) {
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            mRawMethods.putIfAbsent(clazz, new ConcurrentHashMap<String, Method>());
//            map都不会为空
            ConcurrentHashMap<String, Method> map = mRawMethods.get(clazz);
            String key = TypeUtils.getMethodId(method);
            map.put(key, method);
        }
    }

    //    mAnnotatedClasses 填充
    private void registerClass(Class<?> clazz) {
        String className = clazz.getName();
        mAnnotatedClasses.putIfAbsent(className, clazz);    //若存在重复的key，则不会放入值
    }

}
