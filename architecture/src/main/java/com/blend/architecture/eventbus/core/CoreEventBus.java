package com.blend.architecture.eventbus.core;

import android.os.Handler;
import android.os.Looper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class CoreEventBus {

    private static CoreEventBus instance = new CoreEventBus();  //饿汉式

    private Map<Object, List<CoreSubscribeMethod>> cacheMap;
    private Handler mHandler;

    private ExecutorService mExecutorService;

    public static CoreEventBus getDefault() {
        return instance;
    }

    private CoreEventBus() {
        cacheMap = new HashMap<>();
        mHandler = new Handler();
        mExecutorService = Executors.newCachedThreadPool();
    }

    //注册
    public void register(Object subscriber) {
        List<CoreSubscribeMethod> subscribeMethods = cacheMap.get(subscriber);
        //如果已经注册，就不需要注册了
        if (subscribeMethods == null) {
            subscribeMethods = getSubscribeMethods(subscriber);
            cacheMap.put(subscriber, subscribeMethods);
        }
    }

    //遍历能够接收事件的方法
    private List<CoreSubscribeMethod> getSubscribeMethods(Object subscriber) {
        List<CoreSubscribeMethod> list = new ArrayList<>();
        Class<?> aClass = subscriber.getClass();    //使用通配符，Class<?>和Class<Object>一个意思
        //需要subscriber一直到他的父类
        while (aClass != null) {
            String name = aClass.getName();
            //判断父类在哪个包下，如果是系统的，就不需要遍历了
            if (name.startsWith("java.") ||
                    name.startsWith("javax.") ||
                    name.startsWith("android.") ||
                    name.startsWith("androidx.")) {
                break;
            }
            Method[] declaredMethods = aClass.getDeclaredMethods(); //获取本类中的所有方法，包括私有的(private、protected、默认以及public)的方法
            for (Method method : declaredMethods) {

                //检测方法没有注解
                CoreSubscribe annotation = method.getAnnotation(CoreSubscribe.class);   //如果存在这样的注释，则返回指定类型的元素的注释，否则为null
                if (annotation == null) {
                    continue;
                }

                //检测方法不合格
                //返回一个Class对象数组，它们以声明顺序表示由此Method对象表示的方法的形式参数类型。如果底层方法没有参数，则返回长度为0的数组
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length != 1) {
                    throw new RuntimeException("CoreEventBus只能接收一个参数");
                }

                CoreThreadMode coreThreadMode = annotation.threadMode();
                CoreSubscribeMethod subscribeMethod = new CoreSubscribeMethod(method, coreThreadMode, parameterTypes[0]);
                list.add(subscribeMethod);
            }
            aClass = aClass.getSuperclass();
        }
        return list;
    }

    //取消注册
    public void unregister(Object subscriber) {
        List<CoreSubscribeMethod> list = cacheMap.get(subscriber);
        if (list != null) {
            cacheMap.remove(subscriber);
        }
    }

    public void post(final Object object) {
        Set<Object> set = cacheMap.keySet();
        Iterator<Object> iterator = set.iterator();
        while (iterator.hasNext()) {
            //拿到注册类
            final Object next = iterator.next();

            //获取类中所有添加注解的方法
            List<CoreSubscribeMethod> list = cacheMap.get(next);
            for (final CoreSubscribeMethod subscribeMethod : list) {
                //判断这个方法是否应该接收事件
                //isAssignableFrom是从类的继承角度去判断，判断是否是某个类的父类，调用者是父类，参数为本身或者其子类
                //A.isAssignableFrom(B),B是不是A的子类或者子接口或者本身。如果是则返回true，如果不是则返回false。
                if (subscribeMethod.getEventType().isAssignableFrom(object.getClass())) {
                    switch (subscribeMethod.getThreadMode()) {
                        case MAIN:
                            //如果接受方法在主线程执行的情况
                            if (Looper.myLooper() == Looper.getMainLooper()) {
                                invoke(subscribeMethod, next, object);
                            } else {
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        invoke(subscribeMethod, next, object);
                                    }
                                });
                            }
                            break;
                        case ASYNC:
                            //接收方在子线程的情况
                            if (Looper.myLooper() == Looper.getMainLooper()) {
                                mExecutorService.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        invoke(subscribeMethod, next, object);
                                    }
                                });
                            } else {
                                invoke(subscribeMethod, next, object);
                            }
                            break;
                    }
                }
            }
        }
    }


    private void invoke(CoreSubscribeMethod subscribeMethod, Object next, Object object) {
        Method method = subscribeMethod.getMethod();
        try {
            //使用指定的参数调用由此Method类表示的底层方法，个别参数自动展开以匹配原始形式参数，原始参考参数和参考参数都需要进行方法调用转换。
            //调用形式：方法.invoke(类名，参数...)
            //obj - 调用底层方法的对象,这个对象就是类。
            //args - 用于方法调用的参数。
            method.invoke(next, object);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

}
