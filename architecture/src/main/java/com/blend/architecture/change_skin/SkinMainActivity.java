package com.blend.architecture.change_skin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.blend.architecture.R;
import com.blend.architecture.change_skin.adapter.MyFragmentPagerAdapter;
import com.blend.architecture.change_skin.fragment.MusicFragment;
import com.blend.architecture.change_skin.fragment.RadioFragment;
import com.blend.architecture.change_skin.fragment.VideoFragment;
import com.blend.architecture.change_skin.widget.MyTabLayout;

import java.util.ArrayList;
import java.util.List;


/**
 * 换肤
 * 颜色: colors.xml 配置需要替换的颜色name 为不同的颜色值
 * 图片： 同上
 * 选择器：同上 (如 颜色选择器，皮肤包中的颜色选择器会使用皮肤包中的颜色)
 * 字体：strings.xml 配置 typeface 路径指向 assets 目录下字体文件
 * 自定义View 需要实现SkinViewSupport接口自行实现换肤逻辑(包括support中的View )
 * <p>
 * 换肤的思路：
 * 换肤就是改变View的样式，View的加载是通过InflateLayout类实现的，在View被创建前拿到View的相关信息，就能自己去改变View创建
 * 的逻辑，更改View相关的属性来实现换肤功能。
 * 1.在super.onCreate中，通过AppCompatDelegateImplV9的installViewFactory()方法，调用LayoutInflaterCompat.setFactory2(layoutInflater, this);
 * 进行Factory的赋值。
 * 2.在setContentView中，通过xml解析，最终调用view = mFactory2.onCreateView(parent, name, context, attrs)，因为AppCompatDelegateImplV9实现了Factory2
 * 接口，所有调用的是AppCompatDelegateImplV9中的onCreateView方法。
 * 3.有两种思路，
 * 1)继承AppCompatActivity类中，可以通过LayoutInflaterCompat.setFactory2，通过重写Factory2的onCreateView()方法来设置创建View的逻辑，前提是将“mFactorySet”属性
 * 通过反射置为false。
 * <p>
 * 2)在继承Activity的类中，由于Activity实现了Factory2接口，重写Factory2的方法即可。前提是在Activity的onCreate之前，给Activity设置Factory2.
 * LayoutInflater mInflater = LayoutInflater.from(this);
 * LayoutInflaterCompat.setFactory2(mInflater, this)
 * <p>
 * 本文采用第一种方案。
 * <p>
 * <p>
 * 换肤的具体细节：采集需要换肤的控件-->加载皮肤包-->替换所有需要换肤的控件（使用观察者模式）
 * 1.在应用的Application里面先进行初始化，通过ActivityLifecycleCallbacks注册Activity的回调，加载上次设置的皮肤包资源。
 * 2.每当打开一个新的activity，自动回调onActivityCreated方法中进行每个Activity的换肤，更新状态栏和字体；获得Activity的LayoutInflate，
 * 通过反射将“mFactorySet”置为false，通过LayoutInflaterCompat.setFactory2设置Factory,在其onCreateView方法中进行View的采集；
 * 在换肤过程之前，预先设定好换肤的XML属性集合，首先判断属性设置是不是硬编码（硬编码不支持换肤），若不是，将View的属性值和属性Id一一对应，根据拿到的属性Id,
 * 获取到需要换肤的View的name(colorPrimaryDark)和type(type)，通过Resources.getIdentifier()拿到皮肤包中的相应View的属性Id（R.color.colorPrimaryDark的Id），
 * 进行换肤设置。最后将这个新打开的Activity添加为观察者。
 * 3.点击换肤时，也就是被观察者改变，通过观察者模式，对每一个观察者之前采集的View进行换肤。
 *
 *
 */
public class SkinMainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_skin_main);

        MyTabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager viewPager = findViewById(R.id.viewPager);
        List<Fragment> list = new ArrayList<>();
        list.add(new MusicFragment());
        list.add(new VideoFragment());
        list.add(new RadioFragment());
        List<String> listTitle = new ArrayList<>();
        listTitle.add("音乐");
        listTitle.add("视频");
        listTitle.add("电台");
        MyFragmentPagerAdapter myFragmentPagerAdapter = new MyFragmentPagerAdapter
                (getSupportFragmentManager(), list, listTitle);
        viewPager.setAdapter(myFragmentPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

//        SkinManager.getInstance().updateSkin(this);
    }


    /**
     * 进入换肤
     *
     * @param view
     */
    public void skinSelect(View view) {
        startActivity(new Intent(this, SkinActivity.class));
    }
}
