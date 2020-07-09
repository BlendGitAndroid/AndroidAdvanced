package com.blend.architecture.eventbus.core;

import android.content.Context;

import com.blend.architecture.eventbus.Request;
import com.blend.architecture.eventbus.Responce;
import com.blend.architecture.eventbus.annotion.ClassId;
import com.blend.architecture.eventbus.bean.RequestBean;
import com.blend.architecture.eventbus.bean.RequestParameter;
import com.blend.architecture.eventbus.service.HermesService;
import com.blend.architecture.eventbus.util.TypeUtils;
import com.google.gson.Gson;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class Hermes {
    //得到对象
    public static final int TYPE_NEW = 0;
    //得到单例
    public static final int TYPE_GET = 1;

    private static final Hermes ourInstance = new Hermes();
    private TypeCenter typeCenter;
    private ServiceConnectionManager serviceConnectionManager;

    Gson GSON = new Gson();

    public static Hermes getDefault() {
        return ourInstance;
    }

    private Hermes() {
        serviceConnectionManager = ServiceConnectionManager.getInstance();
        typeCenter = TypeCenter.getInstance();
    }

//------------------------------A进程  服务端----------------------------------------------------------------

    public void register(Class<?> clazz) {
        //将单例保存到另外一个地方，TYPE_CENTER中
        typeCenter.register(clazz);
    }

    //------------------------------B进程  客户端----------------------------------------------------------------
    //客户端连接服务器
    public void connect(Context context, Class<? extends HermesService> service) {
        connectApp(context, null, service);
    }


    //若是在同一个APP，packageName可以为null，不同的APP跨进程，要传入客户端的packageName
    public void connectApp(Context context, String packageName, Class<? extends HermesService> service) {
        serviceConnectionManager.bind(context.getApplicationContext(), packageName, service);
    }

    // 将IUserManager接口进行代理处理，并返回代理。
    public <T> T getInstance(Class<T> clazz, Object... parameters) {    //可变长参数
    //Class<T> clazz, Object... parameters   ====》  Request    ----->Responce
        Responce responce = sendRequest(HermesService.class, clazz, null, parameters);
    //        responce ---》UserManager  不需要 还原     客户端进程压根 UserManager
        //这是因为客户端和服务端在一个APP中，能相互调用
        return getProxy(HermesService.class, clazz);
    }

    //客户端得到单例的代理对象
    //第一个参数：服务端进程，第二个参数：需要获取的单例类
    //第一个<T>：声明一个泛型方法，此方法持有泛型T
    //第二个T：该泛型方法的返回值为T
    private <T> T getProxy(Class<? extends HermesService> service, Class clazz) {
        ClassLoader classLoader = service.getClassLoader();
        /*
        利用Java的反射技术(Java Reflection)，在运行时创建一个实现某些给定接口的新类（也称“动态代理类”）及其实例（对象）,代理的是接口(Interfaces)，
        不是类(Class)，也不是抽象类。

        作用：
        1.一个接口的实现在编译时无法知道，需要在运行时才能实现
        2.面向切面编程：如AOP

        三个参数：
        ClassLoader loader：用哪个类加载器去加载代理对象
        Class<?>[] interfaces：动态代理类需要实现的接口列表
        InvocationHandler h：动态代理方法在执行时，会调用h里面的invoke方法去执行
        return：返回动态创建的代理类
         */
        T proxy = (T) Proxy.newProxyInstance(classLoader, new Class<?>[]{clazz}, new HermesInvocationHandler(service, clazz));
        return proxy;
    }

    private <T> Responce sendRequest(Class<HermesService> hermesServiceClass
            , Class<T> clazz, Method method, Object[] parameters) {
        RequestBean requestBean = new RequestBean();

        //返回类型的全类名
        String className = null;
        if (clazz.getAnnotation(ClassId.class) == null) {
            requestBean.setClassName(clazz.getName());
            requestBean.setResultClassName(clazz.getName());
        } else {
            requestBean.setClassName(clazz.getAnnotation(ClassId.class).value());
            requestBean.setResultClassName(clazz.getAnnotation(ClassId.class).value());
        }
        if (method != null) {
//            方法名统一传方法名+参数名getInstance(java.lang.String)
            requestBean.setMethodName(TypeUtils.getMethodId(method));
        }
//fastjson  ---》  GSON


        RequestParameter[] requestParameters = null;
        if (parameters != null && parameters.length > 0) {
            requestParameters = new RequestParameter[parameters.length];
            for (int i = 0; i < parameters.length; i++) {
                Object parameter = parameters[i];
                String parameterClassName = parameter.getClass().getName();
                String parameterValue = GSON.toJson(parameter);

                RequestParameter requestParameter = new RequestParameter(parameterClassName, parameterValue);
                requestParameters[i] = requestParameter;
            }
        }

        if (requestParameters != null) {
            requestBean.setRequestParameter(requestParameters);
        }

//        请求获取单例 ----》对象 ----------》调用对象的方法
        Request request = new Request(GSON.toJson(requestBean), TYPE_GET);
        return serviceConnectionManager.request(hermesServiceClass, request);


    }


    /**
     * sendObjectRequest将接口中设置的 注解类名，请求的方法名，请求的参数等都封装在requestBean中，
     * 再把requestBean封装在Request中，进行发送给服务端进程处理。
     *
     * @param hermesServiceClass 服务类
     * @param clazz 单例类，注解的类名
     * @param method 方法(getFriend)
     * @param parameters 方法参数
     * @param <T>
     * @return
     */
    public <T> Responce sendObjectRequest(Class<HermesService> hermesServiceClass
            , Class<T> clazz, Method method, Object[] parameters) {
        RequestBean requestBean = new RequestBean();

        //设置class类名
        if (clazz.getAnnotation(ClassId.class) == null) {
            requestBean.setClassName(clazz.getName());
            requestBean.setResultClassName(clazz.getName());
        } else {
            requestBean.setClassName(clazz.getAnnotation(ClassId.class).value());
            requestBean.setResultClassName(clazz.getAnnotation(ClassId.class).value());
        }

        //设置方法，方法名统一传方法名+参数名  getInstance(java.lang.String)
        if (method != null) {
            requestBean.setMethodName(TypeUtils.getMethodId(method));
        }

        //设置参数信息，将参数json化
        RequestParameter[] requestParameters = null;
        if (parameters != null && parameters.length > 0) {
            requestParameters = new RequestParameter[parameters.length];
            for (int i = 0; i < parameters.length; i++) {
                Object parameter = parameters[i];
                String parameterClassName = parameter.getClass().getName();
                String parameterValue = GSON.toJson(parameter);

                RequestParameter requestParameter = new RequestParameter(parameterClassName, parameterValue);
                requestParameters[i] = requestParameter;
            }
        }

        if (requestParameters != null) {
            requestBean.setRequestParameter(requestParameters);
        }

        //封装request对象
        Request request = new Request(GSON.toJson(requestBean), TYPE_NEW);

        //aidl传递
        return serviceConnectionManager.request(hermesServiceClass, request);


    }
}
