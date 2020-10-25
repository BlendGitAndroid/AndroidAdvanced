package com.blend.optimization.apk;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.blend.optimization.R;
import com.blend.optimization.apk.encrypt.AES;
import com.blend.optimization.apk.encrypt.RSA;

/**
 * APK加固：
 * PROGUARD的使用与配置：
 * Proguard是一个代码优化和混淆工具。能够提供对Java类文件的压缩、优化、混淆，和预校验。
 * 压缩的步骤是检测并移除未使用的类、字段、方法和属性。
 * 优化的步骤是分析和优化方法的字节码。
 * 混淆的步骤是使用短的毫无意义的名称重命名剩余的类、字段和方法。压缩、优化、混淆使得代码更小，更高效。
 * 混淆后的代码错误栈恢复方法：
 * 1)把错误信息保存到文件
 * 2)使用工具 sdk/tools/groguard/bin/retrace.bat
 * 先配置：-keepattributes SourceFile,LineNumberTable
 * 再执行：retrace.bat  -verbose mappint文件  bug文件
 * <p>
 * <p>
 */
public class ApkMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apk_main);

        envrypt();
    }

    private void envrypt() {
        try {
            RSA.test();

            AES.test();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}