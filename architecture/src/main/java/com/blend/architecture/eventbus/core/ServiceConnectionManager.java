package com.blend.architecture.eventbus.core;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;

import com.blend.architecture.MyEventBusService;
import com.blend.architecture.Request;
import com.blend.architecture.Responce;
import com.blend.architecture.eventbus.service.HermesService;

import java.util.concurrent.ConcurrentHashMap;

public class ServiceConnectionManager {
    private static final ServiceConnectionManager ourInstance = new ServiceConnectionManager();
    //    Class  对应的Binder  对象
    private final ConcurrentHashMap<Class<? extends HermesService>, MyEventBusService> mHermesServices =
            new ConcurrentHashMap<Class<? extends HermesService>, MyEventBusService>();

    public static ServiceConnectionManager getInstance() {
        return ourInstance;
    }
    //Class对应的链接对象
    private final ConcurrentHashMap<Class<? extends HermesService>, HermesServiceConnection> mHermesServiceConnections = new ConcurrentHashMap<Class<? extends HermesService>, HermesServiceConnection>();


    private ServiceConnectionManager() {
    }
    public void bind(Context context, String packageName, Class<? extends HermesService> service) {
        HermesServiceConnection connection = new HermesServiceConnection(service);
        mHermesServiceConnections.put(service, connection);
        Intent intent ;
        if (TextUtils.isEmpty(packageName)) {
            intent = new Intent(context, service);
        } else {
            intent = new Intent();
            intent.setClassName(packageName, service.getName());
        }
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    public Responce request(Class<HermesService> hermesServiceClass, Request request) {
        //从缓存中获取binder代理对象，发送请求
        MyEventBusService eventBusService = mHermesServices.get(hermesServiceClass);
        if (eventBusService != null) {
            try {
                Responce responce= eventBusService.send(request);
                return responce;
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    //     接受远端的binder 对象   进程B就可以了通过Binder对象去操作 服务端的 方法
    private class HermesServiceConnection implements ServiceConnection {
        private Class<? extends HermesService> mClass;

        HermesServiceConnection(Class<? extends HermesService> service) {
            mClass = service;
        }
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyEventBusService hermesService = MyEventBusService.Stub.asInterface(service);
            mHermesServices.put(mClass, hermesService);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mHermesServices.remove(mClass);
        }
    }

}
