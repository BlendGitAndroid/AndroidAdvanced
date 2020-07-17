package com.blend.skincore;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.blend.skincore.logger.L;
import com.blend.skincore.utils.SkinThemeUtils;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

/**
 * 如何获取换肤的View，利用LayoutInflate内部接口Factory2提供的onCreateView方法获取需要换肤的View。
 * 1.首先在AppCompatActivity中，通过getDelegate()方法获取AppCompatDelegate实例，并调用其setContentView方法，AppCompatDelegate
 * 对象的建立，是根据不同的SDK版本创建的，继承结构是AppCompatDelegate(抽象)->AppCompatDelegateImplBase(抽象)->AppCompatDelegateImplV9(实现)->
 * AppCompatDelegateImplV11（实现）等。
 * <p>
 * 2.getDelegate().setContentView(layoutResID);具体调用的是AppCompatDelegateImplV9中的setContentView，一直调用到LayoutInflate的createViewFromTag，
 * 首先会通过mFactory2不为null调用onCreateView方法，如果返回的View为null，然后根据-1 == name.indexOf('.')判断是否是全限定名称，若不是全限定名，自动加上"android.view."
 * 前缀，；两者最后都调用createView方法，根据反射生成相应的View。
 * <p>
 * 3.那么2)中的mFactory2是什么？它是什么时候赋值的呢？
 * 通过super.onCreate(savedInstanceState);调用到AppCompatDelegateImplV9中的installViewFactory()方法，最终调用到LayoutInflate的setFactory2方法中，到这里我们知道了
 * LayoutInflater的成员变量mFactory2就是AppCompatDelegateImplV9对象（AppCompatDelegateImplV9实现LayoutInflater.Factory2接口）。
 * 那么在createViewFromTag方法中调用的mFactory2.onCreateView，实际上调用的是AppCompatDelegateImplV9中的onCreateView。那么最终调用的mAppCompatViewInflater.createView
 * 生成系统内置的View，不是系统的View则返回null，走到2中的逻辑。
 * <p>
 * 总结：Layout资源文件的加载是通过LayoutInflater.Factory2的onCreateView方法实现的。也就是如果我们自己定义一个实现了LayoutInflater.Factory2接口的类并实现onCreateView方法，
 * 在该方法中保存需要换肤的View，最后给换肤的View设置插件中的资源。
 */
public class SkinLayoutInflaterFactory implements LayoutInflater.Factory2, Observer {
    private static final String[] mClassPrefixList = {
            "android.widget.",
            "android.view.",
            "android.webkit."
    };
    //记录对应View的构造函数
    private static final Map<String, Constructor<? extends View>> mConstructorMap
            = new HashMap<>();
    private static final Class<?>[] mConstructorSignature = new Class[]{
            Context.class, AttributeSet.class};

    // 当选择新皮肤后需要替换View与之对应的属性
    // 页面属性管理器
    private SkinAttribute skinAttribute;

    private Activity activity;

    public SkinLayoutInflaterFactory(Activity activity, Typeface typeface) {
        this.activity = activity;
        skinAttribute = new SkinAttribute(typeface);
    }


    /**
     * 创建对应布局并返回
     *
     * @param parent  当前TAG 父布局
     * @param name    在布局中的TAG 如:TextView, android.support.v7.widget.Toolbar
     * @param context 上下文
     * @param attrs   对应布局TAG中的属性 如: android:text android:src
     * @return View    null则由系统创建
     */
    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        //换肤就是在需要时候替换 View的属性(src、background等)
        //所以这里创建 View,从而修改View属性
        View view = createViewFromTag(name, context, attrs);
        if (null == view) {
            view = createView(name, context, attrs);
        }
        if (null != view) {
            L.e(String.format("检查[%s]:" + name, context.getClass().getName()));
            //加载属性
            skinAttribute.load(view, attrs);
        }
        return view;
    }


    private View createViewFromTag(String name, Context context, AttributeSet
            attrs) {
        //如果包含 . 则不是SDK中的view 可能是自定义view包括support库中的View
        if (-1 != name.indexOf('.')) {
            return null;
        }
        for (int i = 0; i < mClassPrefixList.length; i++) {
            View view = createView(mClassPrefixList[i] +
                    name, context, attrs);
            if (view != null) {
                return view;
            }
        }
        return null;
    }

    private View createView(String name, Context context, AttributeSet
            attrs) {
        Constructor<? extends View> constructor = findConstructor(context, name);
        try {
            return constructor.newInstance(context, attrs);
        } catch (Exception e) {
        }
        return null;
    }

    private Constructor<? extends View> findConstructor(Context context, String name) {
        Constructor<? extends View> constructor = mConstructorMap.get(name);
        if (null == constructor) {
            try {
                Class<? extends View> clazz = context.getClassLoader().loadClass
                        (name).asSubclass(View.class);
                constructor = clazz.getConstructor(mConstructorSignature);
                mConstructorMap.put(name, constructor);
            } catch (Exception e) {
            }
        }
        return constructor;
    }


    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return null;
    }

    @Override
    public void update(Observable o, Object arg) {
        SkinThemeUtils.updateStatusBarColor(activity);
        Typeface typeface = SkinThemeUtils.getSkinTypeface(activity);
        skinAttribute.setTypeface(typeface);
        skinAttribute.applySkin();
    }
}
