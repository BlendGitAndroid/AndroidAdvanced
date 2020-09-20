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
 * <p>
 * APP启动时间查看：
 * 1.通过Logcat输入Displayed筛选系统日志，并不过滤信息No Filters。
 * 2.使用adb shell获取应用的启动时间
 * // 其中的AppStartActivity全路径可以省略前面的packageName
 * adb shell am start -W [packageName]/[AppStartActivity全路径]
 * 执行后会得到三个时间：ThisTime、TotalTime和WaitTime，详情如下：
 * ThisTime：最后一个Activity启动耗时。
 * TotalTime：所有Activity启动耗时。
 * WaitTime：AMS启动Activity的总耗时。
 * 一般查看得到的TotalTime，即应用的启动时间，包括创建进程 + Application初始化 + Activity初始化到界面显示的过程。
 * <p>
 * TRACE工具分析代码执行时间：
 * 用法：
 * 1.Debug.startMethodTracing(filePath);
 * 中间为需要统计执行时间的代码
 * Debug.stopMethodTracing();
 * 2.adb pull /storage/emulated/0/app1.trace把文件拉出来分析把pull到电脑上的文件拖到AS中就可以分析了。
 * 优化方案：在Application中初始化的代码尽可能少，耗时操作尽量不要放在Application中初始化。
 * 1.开线程。没建handler、没操作UI、对异步要求不高。
 * 因为此时Handle还没有真正循环起来，肯定不可进行UI操作；对异步要求高，可能会出现空指针异常，因为当你使用的时候，发现还没有初始化完。
 * 2.懒加载。用到的时候再初始化，如网络，数据库操作。
 * <p>
 * 应用启动类型：
 * 1.冷启动：从点击应用图标到UI界面完全显示且用户可操作的全部过程。
 * 启动流程：Click Event -> IPC -> Process.start -> ActivityThread -> bindApplication -> LifeCycle -> ViewRootImpl
 * 2.热启动：因为会从已有的应用进程启动，此时的进程在内存中，所以不会再创建和初始化Application，只会重新创建并初始化Activity。
 * 启动流程：LifeCycle -> ViewRootImpl。
 * ViewRootImpl：ViewRoot是GUI管理系统与GUI呈现系统之间的桥梁。每一个ViewRootImpl关联一个Window，ViewRootImpl最终会通过它的
 * setView方法绑定Window所对应的View，并通过其performTraversals方法对View进行布局、测量和绘制。
 * 3.温启动：温启动包含了在冷启动期间发生的部分操作；同时，它的开销要比热启动高。有许多潜在状态可视为温启动。例如：用户按返回键退出应用后
 * 又重新启动应用。这时进程已在运行，但应用必须通过调用 onCreate() 从头开始重新创建 Activity。
 */
public class AppStartupMainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_startup_main);
    }
}