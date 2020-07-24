package com.blend.architecture.router;

import android.util.Log;

import com.blend.routerannotation.Route;
import com.blend.routerbase.TestService;

@Route(path = "/main/service1")
public class TestServiceImpl1 implements TestService {


    @Override
    public void test() {
        Log.i("Service", "我是app模块测试服务通信1");
    }
}
