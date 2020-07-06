package com.blend.architecture.eventbus.service;

import android.annotation.Nullable;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import com.blend.architecture.MyEventBusService;
import com.blend.architecture.eventbus.Request;
import com.blend.architecture.eventbus.Responce;
import com.blend.architecture.eventbus.core.Hermes;
import com.blend.architecture.eventbus.response.InstanceResponceMake;
import com.blend.architecture.eventbus.response.ObjectResponceMake;
import com.blend.architecture.eventbus.response.ResponceMake;

/**
 * 服务端代码
 */
public class HermesService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private MyEventBusService.Stub mBinder = new MyEventBusService.Stub() {
        @Override
        public Responce send(Request request) throws RemoteException {
//            对请求参数进行处理，生成Responce结果返回
            ResponceMake responceMake = null;
            switch (request.getType()) {   //根据不同的类型，产生不同的策略
                case Hermes.TYPE_GET://获取单例
                    responceMake = new InstanceResponceMake();
                    break;
                case Hermes.TYPE_NEW:
                    responceMake = new ObjectResponceMake();
                    break;
            }

            return responceMake.makeResponce(request);
        }
    };
}
