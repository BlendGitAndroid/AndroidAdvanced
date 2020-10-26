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
 * 各种算法总结：
 * 1)不可逆摘要算法。如MD5(MD5信息摘要算法)，HMAC(密钥相关的哈希运算消息认证码)，SHA1(安全散列算法)。在网络中一般用于报文摘要，
 * 用来验证数据的完整性。开发中比较常用的场景就是用户密码加密。
 * 2)对称加密算法。如DES、3DES、AES128。3DES(即Triple DES)是DES向AES过渡的加密算法，它使用3条56位的密钥对数据进行三次加密。
 * 是DES的一个更安全的变形。它以DES为基本模块，通过组合分组方法设计出分组加密算法。比起最初的DES，3DES更为安全。密钥长度默认为
 * 168位，还可以选择128位。AES 高级数据加密标准，能够有效抵御已知的针对DES算法的所有攻击，默认密钥长度为128位，还可以供选择192位，
 * 256位。
 * 3)非对称加密算法。如RSA。加密来看：公钥加密私钥解密；数字签名验证来看：私钥签名，公钥验证。
 * 网络中：
 * 在网络中，报文摘要主要验证数据的完整性；数字签名是对发送的报文的摘要进行签名，用于验证数据的来源可靠性。
 * 数字签名的过程：A向B发送信件，先使用HASH函数生成信件的摘要(digest)，然后使用其私钥对摘要加密生成数字签名；B收到后，使用A的公钥
 * 解密得到信件的HASH值，然后对信件本身再使用HASH函数，两个摘要进行对比来验证信件的完整性。
 * 保证公钥的合法性，需要CA认证。CA会生成一个把其实体身份和公钥绑定的证书，这个证书包含公钥和其实体的全剧唯一身份标识。
 * 假如A向B通信，并且也向B发送其CA签名的证书；B收到后，使用CA的公钥来核对A证书的合法性和提取A的公钥。
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