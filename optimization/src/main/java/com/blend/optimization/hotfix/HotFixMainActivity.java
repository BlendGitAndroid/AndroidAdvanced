package com.blend.optimization.hotfix;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.blend.optimization.R;

/**
 * 热修复背景：
 * 1)刚发布的版本出现了严重的Bug，这就需要去解决Bug、测试打包重新发布，这会耗费大量的人力和物力，代价比较大。
 * 2)已经更正了此前发布版本的Bug，如果下个版本是大版本，那么两个版本之间间隔时间会很长，这样要等到下个大版本发布再修复Bug，
 * 而之前版本的Bug会长期的影响用户。
 * 3)版本升级率不高，并且需要长时间来完成版本迭代，前版本的Bug就会一直影响不升级的用户，如微信，一年很少升级。
 * 4)有一些小但是很重要的功能需要在短时间内完成版本迭代，如节日活动。
 * <p>
 * <p>
 * 正常开发的缺点：
 * 1)重新发布版本代价太大。
 * 2)用户下载安装成本太大。
 * 3)bug修复不及时，用户体验太差。
 * <p>
 * <p>
 * 热修复优点：
 * 1)无需重新发布新版本，省时省力。
 * 2)用户无感知修复，也无需下载最新应用，代价小。
 * 3)修复成功率高，把损失降到最低。
 * <p>
 * <p>
 * Dex分包：
 * 65536限制：应用的方法数超过了最大数65536个。因为DVM Bytecode的限制，DVM指令集的方法调用指令invoke-kind索引为16bits，最多能引用65535个方法。
 * LinearAlloc限制：在安装应用时可能会提示INSTALL_FAILED_DEXOPT，产生的原因就是LinearAlloc限制，DVM中的LinearAlloc是一个固定的缓存区，当方法
 * 数超出缓存区的大小时会报错。
 * 为了解决65536限制和LinearAlloc限制，从而产生了Dex分包机制。
 * Dex分包方案主要做的是在打包时将应用代码分成多个Dex，将应用启动时必须用到的类和这些类的直接引用类放到主Dex中，其它代码放到次Dex中。
 * 当应用启动时先加载主Dex，等到应用启动后再动态地加载次Dex，从而缓解了主Dex的65536限制和LinearAlloc限制
 * <p>
 * <p>
 * 热修复插桩的原理：
 * 越靠前的dex优先被系统使用，基于类级别的复用。代码中Hook了ClassLoader.pathList.dexElements[]，
 * 因为ClassLoader的findClass是通过遍历dexElements[]中的dex来寻找类的。
 * <p>
 * <p>
 * Tinker思路：
 * 1.创建BaseDexClassLoader子类DexClassLoader。
 * 2.加载修复好的hotfix.dex(一般从服务器上下载)。
 * 3.将自己的dex和系统的dexElements进行合并(要将修复好的dex索引为0)。
 * 4.利用反射技术，赋值给系统的pathList。
 * <p>
 * <p>
 * 热修复的未来：
 * 热修复是能够让开发者和用户双赢的。不仅厂商能快速迭代更新app，使功能尽快上线，而且热更新过程用户无感知，节省大量更新时间，提高用户体验。
 * 更重要的能保证app的功能稳定，bug能及时修复。
 * <p>
 * <p>
 * 热修复8.0和9.0的区别：
 * 冷启动和实时修复：
 * 增量更新和差分包：
 * 应用分身原理：
 * 热修复时混淆：
 * 修复so和资源文件：
 */
public class HotFixMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hot_fix_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            if (checkSelfPermission(perms[0]) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(perms, 200);
            }
        }
    }

    public void jump(View view) {
        startActivity(new Intent(this, SecondBugActivity.class));
    }
}