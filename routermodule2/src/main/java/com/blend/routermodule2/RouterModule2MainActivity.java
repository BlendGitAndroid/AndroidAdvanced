package com.blend.routermodule2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.blend.routerannotation.Extra;
import com.blend.routerannotation.Route;
import com.blend.routerbase.TestService;
import com.blend.routercore.BlendRouter;

@Route(path = "/module2/test")
public class RouterModule2MainActivity extends AppCompatActivity {

    @Extra
    String msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_router_module_main);

        BlendRouter.getInstance().inject(this);
        Log.i("module2", "我是模块2:" + msg);

        //当处于组件模式的时候
        if (BuildConfig.isModule) {
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
    }

    public void mainJump(View view) {
        if (BuildConfig.isModule) {
            BlendRouter.getInstance().build("/main/test").withString("a",
                    "从Module2").navigation(this);
        } else {
            Toast.makeText(this, "当前处于组件模式,无法使用此功能",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void module1Jump(View view) {
        BlendRouter.getInstance().build("/module1/test").withString("msg",
                "从Module2").navigation(this);
    }
}
