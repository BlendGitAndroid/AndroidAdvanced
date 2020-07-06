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

    private Context sContext;

    private Hermes() {
        serviceConnectionManager = ServiceConnectionManager.getInstance();
        typeCenter = TypeCenter.getInstance();
    }

//------------------------------A进程----------------------------------------------------------------

    public void register(Class<?> clazz) {
//保存  到另外一个单例地方
        typeCenter.register(clazz);
    }

    //------------------------------B进程----------------------------------------------------------------
    public void connect(Context context, Class<? extends HermesService> service) {
        connectApp(context, null, service);
    }

    public void init(Context context) {
        sContext = context.getApplicationContext();
    }

    public void connectApp(Context context, String packageName, Class<? extends HermesService> service) {
        init(context);
        serviceConnectionManager.bind(context.getApplicationContext(), packageName, service);

    }

    //主要防止方法重载  单例对象 是不需要
    public <T> T getInstance(Class<T> clazz, Object... parameters) {
//Class<T> clazz, Object... parameters   ====》  Request    ----->Responce
        Responce responce = sendRequest(HermesService.class, clazz, null, parameters);
//        responce ---》UserManager  不需要 还原     客户端进程压根 UserManager
        return getProxy(HermesService.class, clazz);
    }


    private <T> T getProxy(Class<? extends HermesService> service, Class clazz) {
        ClassLoader classLoader = service.getClassLoader();
        T proxy = (T) Proxy.newProxyInstance(classLoader, new Class<?>[]{clazz}, new HermesInvocationHandler(service, clazz));
        return proxy;
    }

    private <T> Responce sendRequest(Class<HermesService> hermesServiceClass
            , Class<T> clazz, Method method, Object[] parameters) {
        RequestBean requestBean = new RequestBean();

        String className = null;
        if (clazz.getAnnotation(ClassId.class) == null) {
//            当
            requestBean.setClassName(clazz.getName());
            requestBean.setResultClassName(clazz.getName());
        } else {
//            返回类型的全类名
            requestBean.setClassName(clazz.getAnnotation(ClassId.class).value());
            requestBean.setResultClassName(clazz.getAnnotation(ClassId.class).value());
        }
        if (method != null) {
//            方法名 统一   传   方法名+参数名  getInstance(java.lang.String)
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


    public <T> Responce sendObjectRequest(Class<HermesService> hermesServiceClass
            , Class<T> clazz, Method method, Object[] parameters) {
        RequestBean requestBean = new RequestBean();

        String className = null;
        if (clazz.getAnnotation(ClassId.class) == null) {
//            当
            requestBean.setClassName(clazz.getName());
            requestBean.setResultClassName(clazz.getName());
        } else {
//            返回类型的全类名
            requestBean.setClassName(clazz.getAnnotation(ClassId.class).value());
            requestBean.setResultClassName(clazz.getAnnotation(ClassId.class).value());
        }
        if (method != null) {
//            方法名 统一   传   方法名+参数名  getInstance(java.lang.String)
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
        Request request = new Request(GSON.toJson(requestBean), TYPE_NEW);
        return serviceConnectionManager.request(hermesServiceClass, request);


    }
}
