package com.blend.optimization.startup;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.blend.optimization.R;

/**
 * APP的启动流程：
 * 1.打开电源，引导芯片代码加载引导程序Boot Loader到RAM中去执行。
 * 2.BootLoader将操作系统拉起。
 * 3.Linux内核启动开始系统设置，找到一个init.rc文件启动初始化进程。
 * 4.init进程初始化和启动属性服务，之后开启Zygote进程。
 * 5.Zygote进程开始创建JVM并注册JNI方法，开启SystemServer。
 * 6.启动Binder线程沲和SystemServiceManager,并启动各种服务。
 * 7.AMS启动Launcher。
 * <p>
 * Launcher启动流程：
 * 1.当用手点击一个图标时，就到了这个类public final class Launcher extends Activity，然后去执行onClick(View view)方法，
 * 会把这个应用的相关信息传入，先获取一个intent--->startActivitySafely(v, intent, tag)--》startActivity(v, intent, tag);
 * -->startActivity(intent);
 * 2.startActivity(intent)会开启一个APP进程，会先通知Zygote进程去Fork一个System server，然后调用ActivityThread#main()方法，
 * 3.用attach开启app，再加载application和activity。thread.attach(false);--->mgr.attachApplication(mAppThread)会通过远
 * 端进程去回调创建Application对象，调用Application的onCreate方法，进行APP的初始化工作。
 * <p>
 * 黑白屏问题：当点击打开APP，进行Application的onCreate初始化的时候，这个时候就会有黑屏和白屏问题。
 * 白屏：<style name="AppTheme" parent="Theme.AppCompat.Light">
 * 黑屏：<style name="AppTheme">（在以前的老版本上有效，现在的版本默认使用透明处理了）
 * 思路：找到一个父类name="Platform.AppCompat.Light"中定义了<item name="android:windowBackground">用来控制黑白屏
 * 解决办法：见style文件
 * window属性：windowIsTranslucent是false时，就一定是不透明；是true时，透明度由windowBackground决定。
 * windowDisablePreview：禁止窗口的启动动画，但是设置android:windowIsTranslucent属性一样，如果在MainActivity启动的时候，
 * 有过多复杂的操作，就会出现在手机中点击了应用程序的图标，但过两秒才会打开应用程序不好的卡顿体验效果。
 */
public class AppStartupMainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_startup_main);
    }
}