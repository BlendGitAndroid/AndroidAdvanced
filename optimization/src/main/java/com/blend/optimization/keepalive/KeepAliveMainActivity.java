package com.blend.optimization.keepalive;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.blend.optimization.R;

/**
 * Android进程保活：
 * LMK引进的原因：进程的启动分冷启动和热启动，当用户退出某一个进程的时候，并不会真正的将进程退出，而是将这个进程放到后台，以便下次启动的时候可以马上启动起来，
 * 这个过程名为热启动，这也是Android的设计理念之一。这个机制会带来一个问题，每个进程都有自己独立的内存地址空间，随着应用打开数量的增多,系统已使用
 * 的内存越来越大，就很有可能导致系统内存不足。为了解决这个问题，系统引入LowMemoryKiller(简称LMK)管理所有进程，根据一定策略来kill某个进程并释
 * 放占用的内存，保证系统的正常运行。
 * 原理：所有应用进程都是从zygote孵化出来的，记录在AMS中mLruProcesses列表中，由AMS进行统一管理，AMS中会根据进程的状态更新进程对应的oom_adj值，这个值会
 * 通过文件传递到kernel中去，kernel有个低内存回收机制，在内存达到一定阀值时会触发清理oom_adj值高的进程腾出更多的内存空间。
 * 内存阈值在不同的手机上不一样，一旦低于该值,Android便开始按顺序关闭进程. 因此Android开始结束优先级最低的空进程，即当可用内存小于180MB(46080)
 * <p>
 * <p>
 * 随着 Android 系统变得越来越完善，单单通过自己拉活自己逐渐变得不可能了；因此后面的所谓「保活」基本上是：
 * 1.提升自己进程的优先级，让系统不要轻易弄死自己；
 * 2.App之间互相结盟，一个兄弟死了其他兄弟把它拉起来。
 * 3.加入厂商白名单。
 * 4.让用户加入白名单中。
 * <p>
 * <p>
 * 提升进程优先级方案：
 * 1.利用Activity提升权限。主要解决第三方应用及系统管理工具在检测到锁屏事件后一段时间（一般为5分钟以内）内会杀死后台进程，已达到省电的目的问题。
 * 但是在Android P之后后台都作了限制后该方案无效。测试7.0前的版本稳定。但是不建议使用。没有销毁掉1像素Activity时候会产生严重的体验问题。
 * 2.利用前台Service提升权限。Android 中 Service 的优先级为4，通过 setForeground 接口可以将后台 Service 设置为前台 Service，使进程的优
 * 先级由4提升为2。
 * 对于 API level < 18 ：调用startForeground(ID， new Notification())，发送空的Notification ，图标则不会显示。对于 API level >= 18：
 * 在需要提优先级的service A启动一个InnerService，两个服务同时startForeground，且绑定同样的 ID。Stop 掉InnerService ，这样通知栏图标即被
 * 移除。这方案实际利用了Android前台service的漏洞。
 * 该方案适用范围：7.1.1系统以下，8.0后的系统通知栏API的变更以及前台服务的变更导致通知栏常驻，造成用户感知。
 * 3.后台播放无声音乐。
 * <p>
 * <p>
 * 进程死后拉活方案：
 * 1.利用系统广播拉活。如网络变化，屏幕亮灭，锁屏解锁等。在发生特定系统事件时，系统会发出广播，通过在 AndroidManifest 中静态注册对应的广播监听器，
 * 即可在发生响应事件时拉活。但是从android 7.0开始，对广播进行了限制，而且在8.0更加严格。
 * 2.全家桶拉活，也是利用自己的内置广播拉活。
 * 3.系统Service机制拉活。但是某些ROM 系统不会拉活。并且经过测试，Service 第一次被异常杀死后很快被重启，第二次会比第一次慢，第三次又会比前一次慢，
 * 一旦在短时间内 Service 被杀死4-5次，则系统不再拉起。
 * 4.双进程拉活。8.0之后不行了。
 * 5.JobScheduler拉活。由于厂商定制，JobService在5.0,5.1,6.0作用很大，7.0时候有一定影响（可以在电源管理中给APP授权）
 * 6.推送拉活。根据终端不同，在小米手机（包括 MIUI）接入小米推送、华为手机接入华为推送。
 * <p>
 * <p>
 * 加入白名单：
 * 从 Android 6.0 开始，系统为了省电增加了休眠模式，系统待机一段时间后，会杀死后台正在运行的进程。但系统会有一个后台运行白名单，
 * 白名单里的应用将不会受到影响，在原生系统下，通过「设置」 - 「电池」 - 「电池优化」 - 「未优化应用」，可以看到这个白名单。
 */
public class KeepAliveMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keep_alive_main);

        addWhiteList();
    }

    //加入白名单
    private void addWhiteList() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!isIgnoringBatteryOptimizations()) {
                requestIgnoreBatteryOptimizations();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean isIgnoringBatteryOptimizations() {
        boolean isIgnoring = false;
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (powerManager != null) {
            isIgnoring = powerManager.isIgnoringBatteryOptimizations(getPackageName());
        }
        return isIgnoring;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void requestIgnoreBatteryOptimizations() {
        try {
            Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, 101);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK) {
            if (isHuawei()) {
                goHuaweiSetting();
            }
        } else {
            Toast.makeText(this, "没有授权", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 厂商判断
     */
    public boolean isHuawei() {
        if (Build.BRAND == null) {
            return false;
        } else {
            return Build.BRAND.toLowerCase().equals("huawei") || Build.BRAND.toLowerCase().equals("honor");
        }
    }

    /**
     * 跳转华为手机管家的启动管理页
     */
    private void goHuaweiSetting() {
        try {
            showActivity("com.huawei.systemmanager",
                    "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity");
        } catch (Exception e) {
            showActivity("com.huawei.systemmanager",
                    "com.huawei.systemmanager.optimize.bootstart.BootStartActivity");
        }
    }

    /**
     * 跳转到指定应用的首页
     */
    private void showActivity(@NonNull String packageName) {
        Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);
        startActivity(intent);
    }

    /**
     * 跳转到指定应用的指定页面
     */
    private void showActivity(@NonNull String packageName, @NonNull String activityDir) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(packageName, activityDir));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


}