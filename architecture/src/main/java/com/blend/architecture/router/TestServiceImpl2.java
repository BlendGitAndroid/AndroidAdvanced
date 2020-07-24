package com.blend.architecture.router;

import android.util.Log;

import com.blend.routerannotation.Route;
import com.blend.routerbase.TestService;

@Route(path = "/main/service2")
public class TestServiceImpl2 implements TestService {


    @Override
    public void test() {
        Log.i("Service", "我是app模块测试服务通信2");
    }
}
