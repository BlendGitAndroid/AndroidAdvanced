package com.blend.architecture.change_skin;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * setContentView解析：
 * 1)在Activity中，setContentView最终会调用PhoneWindow的setContentView，Window的创建在ActivityThread.performLaunchActivity
 * 的attach中。在PhoneWindow的setContentView方法中，创建Decor，第一步根据generateLayout(Decor)生成mContentParent，在这个方法中，获取
 * styleable来对window设置相关的属性，都是调用requestFeature，注意方法中if(mContentParentExplicitlySet)，mContentParentExplicitlySet = true
 * 在setContentView末尾进行设置，即requestsFeature方法必须在setContentView方法之前。再根据不同的Feature加载不同的DecorView的XML，
 * 以screen_simple.xml为例子，其本身是一个LinearLayout,包含上下两部分：标题栏ViewStub区域和内容区域id为content的FrameLayout，最终这个content表示的
 * 区域赋值给mContentParent；第二步通过mLayoutInflater.inflate(layoutResID, mContentParent)设置布局，如果是merge标签，参数root不为null且attachToRoot
 * 为true，表示merge内容必须要添加到root容器，否则抛出异常；若是include标签，不能作为根节点，否则抛出异常。
 * 2）在AppCompatActivity中，setContentView最终会调用AppCompatDelegateImplV9的setContentView，ensureSubDecor会创建SubDecor(与DecorView类似)，
 * 将原来的DecorView中的window属性替换成兼容属性，并将DecorView中id为content的FrameLayout替换为SubDecor中的ContentFrameLayout。
 * 3）最终View的创建都会先调用createViewFromTag方法，在createViewFromTag方法会依次判断mFactory2、mFactory、mPrivateFactory是否为 null，也就是会依次
 * 根据 mFactory2、mFactory、mPrivateFactory来创建View对象。Factory和Factory2都属于LayoutInflater的内部接口，核心方法是onCreateView来创建View，这
 * 一特性为我们提供了View创建过程的Hack机会，例如替换某个View类型，动态换肤、View复用等，若以上条件都不满足，则最终调用createView通过根据全类名通过反射来生成View。
 *
 * 注意：LayoutInflater 在 View 对象的创建过程使用了大量反射，如果某个布局界面内容又较复杂，该过程耗时是不容忽视的。更极端的情况可能是某个 View 的创建过程需要执行4次，
 * 例如 SurfaceView，因为系统默认遍历规则依次为 android/weight、android/webkit 和 android/app，但是由于 SurfaceView 属于 android/view 目录下，故此时需要第4次
 * loadClass 才可以正确加载，这个效率会有多差
 *
 */
public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity(new Intent(this, SkinMainActivity.class));
        finish();
    }
}
