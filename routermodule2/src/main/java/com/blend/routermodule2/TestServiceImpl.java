package com.blend.routermodule2;

import android.util.Log;

import com.blend.routerannotation.Route;
import com.blend.routerbase.TestService;

@Route(path = "/module2/service")
public class TestServiceImpl implements TestService {
    @Override
    public void test() {
        Log.i("Service", "我是Module2模块测试服务通信");
    }
}
