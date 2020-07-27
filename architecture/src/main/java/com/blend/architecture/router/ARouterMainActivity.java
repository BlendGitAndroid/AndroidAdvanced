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
 *
 *
 * <p>
 * 阿里ARouter原理：一个用于帮助Android App进行组件化改造的框架——支持模块间的路由、通信、解耦，基于反射和注解来实现的。
 *
 * 组件化出现的原因：在组件化中，为了业务逻辑的彻底解耦，同时也为了每个module都可以方便的单独运行和调试，上层的各个module不会进行相互依赖
 * (只有在正式联调的时候才会让app壳module去依赖上层的其他组件module)，而是共同依赖于base module，base module中会依
 * 赖一些公共的第三方库和其他配置。那么在上层的各个module中，如何进行通信呢？
 *
 * <p>
 * 出现的问题：传统的Activity之间通信，通过startActivity(intent)，而在组件化的项目中，上层的module没有依赖关系(即便两个module
 * 有依赖关系，也只能是单向的依赖)，那么假如login module中的一个Activity需要启动pay_module中的一个Activity便不能
 * 通过startActivity来进行跳转。那么大家想一下还有什么其他办法呢？ 可能有同学会想到隐式跳转，这当然也是一种解决方法，但
 * 是一个项目中不可能所有的跳转都是隐式的，这样Manifest文件会有很多过滤配置，而且非常不利于后期维护。当然你用反射也可以实
 * 现跳转，但是第一：大量的使用反射跳转对性能会有影响，第二：你需要拿到Activity的类文件，在组件开发的时候，想拿到其他module
 * 的类文件是很麻烦的（因为组件开发的时候组件module之间是没有相互引用的，你只能通过找到类的路径去拿到这个class，显然非常麻烦）
 *
 * <p>
 * 解决办法：通常都会在base_module上层再依赖一个router_module,而这个router_module就是负责各个模块之间服务暴露和页面跳转的。
 *
 * <p>
 * 原理：核心思想跟上面讲解的是一样的，我们在代码里加入的@Route注解，会在编译时期通过apt生成一些存储path和activityClass映射
 * 关系的类文件，然后app进程启动的时候会拿到这些类文件，把保存这些映射关系的数据读到内存里(保存在map里)，然后在进行路由跳转的时
 * 候，通过build()方法传入要到达页面的路由地址，ARouter会通过它自己存储的路由表找到路由地址对应的
 * Activity.class(activity.class = map.get(path))，然后new Intent()，当调用ARouter的withString()方法它的内部会
 * 调用intent.putExtra(String name, String value)，调用navigation()方法，它的内部会调用startActivity(intent)进行
 * 跳转，这样便可以实现两个相互没有依赖的module顺利的启动对方的Activity了。
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