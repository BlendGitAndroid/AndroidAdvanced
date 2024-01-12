package com.blend.routermodule1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.blend.routerannotation.Extra;
import com.blend.routerannotation.Route;
import com.blend.routerbase.TestService;
import com.blend.routercore.BlendRouter;

@Route(path = "/module1/test")
public class RouterModule1MainActivity extends AppCompatActivity {

    @Extra
    String msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_module1_main);

        // 需要注入类信息，然后给msg赋值
        BlendRouter.getInstance().inject(this);
        Log.i("module1", "我是模块1:" + msg);

        TestService testService = (TestService) BlendRouter.getInstance().build("/main/service1")
                .navigation();
        testService.test();

        TestService testService1 = (TestService) BlendRouter.getInstance().build("/main/service2")
                .navigation();
        testService1.test();

        TestService testService2 = (TestService) BlendRouter.getInstance().build("/module1/service")
                .navigation();
        testService2.test();

        TestService testService3 = (TestService) BlendRouter.getInstance().build("/module2/service")
                .navigation();
        testService3.test();
    }

    public void mainJump(View view) {
        BlendRouter.getInstance().build("/main/test").withString("a", "从Module1")
                .navigation(this);
    }

    public void module2Jump(View view) {
        BlendRouter.getInstance().build("/module2/test").withString("msg", "从Module1")
                .navigation(this);
    }
}