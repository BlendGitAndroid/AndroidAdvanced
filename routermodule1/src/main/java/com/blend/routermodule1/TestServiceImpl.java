package com.blend.routermodule1;

import android.util.Log;

import com.blend.routerannotation.Route;
import com.blend.routerbase.TestService;

@Route(path = "/module1/service")
public class TestServiceImpl implements TestService {

    @Override
    public void test() {
        Log.i("Service", "我是Module1模块测试服务通信");
    }
}
