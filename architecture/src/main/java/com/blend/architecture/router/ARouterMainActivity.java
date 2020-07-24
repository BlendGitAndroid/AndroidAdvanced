package com.blend.architecture.router;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.blend.architecture.R;
import com.blend.architecture.router.parceable.TestParcelable;
import com.blend.routerbase.TestService;
import com.blend.routercore.BlendRouter;

import java.util.ArrayList;

/**
 * 模块化：根据不同的关注点，将一个项目的可以共享的部分抽取出来，形成独立的Module，这就是模块化。模块化不只包含公共部分，
 * 当然也可以是业务模块。比如：图片加载模块
 * <p>
 * 组件化：组件化是建立在模块化思想上的一次演进，一个变种。组件化本来就是模块化的概念。核心是模块角色的可转化换性，
 * 在打包时，是library；调试时，是application。组件化的单位是组件
 * <p>
 * 插件化：严格意义来讲，其实也算是模块化的观念。将一个完整的工程，按业务划分为不同的插件，来化整为零，相互配合。
 * 插件化的单位是apk(一个完成的应用)。可以实现apk的动态加载，动态更新，比组件化更灵活。
 */
public class ARouterMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a_router_main);

        try {
            BlendRouter.init(getApplication());
        } catch (Exception e) {
            e.printStackTrace();
        }

        /**
         * 组件服务共享 通信
         */
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

    // 应用内跳转
    public void innerJump(View view) {
        ArrayList<Integer> integers = new ArrayList<Integer>();
        integers.add(1);
        integers.add(2);

        ArrayList<String> strings = new ArrayList<String>();
        strings.add("1");
        strings.add("2");

        ArrayList<TestParcelable> ps = new ArrayList<TestParcelable>();

        TestParcelable testParcelable = new TestParcelable(1, "a");
        TestParcelable testParcelable2 = new TestParcelable(2, "d");
        ps.add(testParcelable);
        ps.add(testParcelable2);

        BlendRouter.getInstance().build("/main/test").withString("a",
                "从MainActivity").withInt("b", 1).withShort("c", (short) 2).withLong("d", 3)
                .withFloat("e", 1.0f).withDouble("f", 1.1).withByte("g", (byte) 1).withBoolean
                ("h", true).withChar("i", '好').withParcelable("j", testParcelable)
                .withStringArray("aa",
                        new String[]{"1", "2"}).withIntArray("bb", new int[]{1, 2}).withShortArray
                ("cc", new short[]{(short) 2, (short) 2}).withLongArray("dd", new long[]{1, 2})
                .withFloatArray("ee", new float[]{1.0f, 1.0f}).withDoubleArray("ff", new
                double[]{1.1, 1.1}).withByteArray("gg",
                new byte[]{(byte) 1, (byte) 1}).withBooleanArray
                ("hh", new boolean[]{true, true}).withCharArray("ii", new char[]{'好', '好'})
                .withParcelableArray("jj", new TestParcelable[]{testParcelable, testParcelable2})
                .withParcelableArrayList("k1", ps).withParcelableArrayList("k2", ps)
                .withStringArrayList("k3", strings).withIntegerArrayList("k4", integers)
                .withInt("hhhhhh", 1)
                .navigation(this, 100);
    }

    // 跳转模块1
    public void module1Jump(View view) {
        BlendRouter.getInstance().build("/module1/test").withString("msg",
                "从MainActivity").navigation();
    }

    // 跳转模块2
    public void module2Jump(View view) {
        BlendRouter.getInstance().build("/module2/test").withString("msg",
                "从MainActivity").navigation();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("Main", requestCode + ":" + resultCode + ":" + data);
    }
}