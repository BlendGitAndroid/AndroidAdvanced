package com.blend.architecture.a_router;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.blend.architecture.R;

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
    }
}