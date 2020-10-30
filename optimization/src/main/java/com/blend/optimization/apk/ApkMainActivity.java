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
 * <p>
 * <p>
 * Android基于dex的加固方案：
 * 原理：在AndroidManifest中指定启动Application为壳Module的Application,生成APK后，将壳Module的AAR文件和加密后的APK中的dex
 * 文件合并，然后重新打包签名。安装应用运行后，通过壳Module的Application来解密dex文件，然后再加载dex。
 * <p>
 * <p>
 * 图片格式及WebP优化方案：
 * 有损压缩：有压缩是对图像本身的改变，会对图像的质量造成伤害，随着压缩次数越来越多，图片的质量会越来越差。如JPG等。
 * 无损压缩：无损压缩是对图像本省的压缩，使图片占用的内存空间变小，并且不会损害图片的质量。如PNG等。
 * 位图：又叫点阵图或像素图。每个点用二进制数据来描述其亮度和颜色，因为这些点是离散的，类似于矩阵，又因为多个像素点的彩色组合成图片，
 * 所以叫点阵图或者位图。如jpg，png，gif等。
 * 矢量图：又叫向量图。由一系列计算机指令来描述和记录的一幅图，一幅图可以解为点、线、面等组成的子图。生成的矢量图文件存储很小，特别适用于
 * 文字设计，图案设计等。如svg。
 * JPEG：有损压缩、体积小、加载快、不支持透明。适用于大的背景图，轮播图。
 * PNG：无损压缩、质量高、体积大、支持透明。适用于透明，色彩比较简单和对比性强的。
 * SVG：文本文件、体积小、不失真、兼容性好。适用于图像简单的矢量图。
 * WebP是一种支持有损压缩和无损压缩的图片文件格式，根据Google的测试，无损压缩后的WebP比PNG文件少了26％的体积，有损压缩后的WebP图片
 * 相比于等效质量指标的JPEG图片减少了25％~34%的体积。
 * 目前京东的商品图及频道推广图都是WebP格式。
 * WebP的优势体现在它具有更优的图像数据压缩算法，能带来更小的图片体积，而且拥有肉眼识别无差异的图像质量；同时具备了无损和有损的压缩模式、
 * Alpha透明以及动画的特性，在JPEG和PNG上的转化效果都相当优秀、稳定和统一。
 * <p>
 * <p>
 * APK瘦身七大步骤：
 * 1.将图片转换成webp格式。AndroidStudio一键转换工具，智能转换，能将大的图片转换成webp，小的就不转换了。
 * 2.去除多语言。resConfig。
 * 3.只保留armeabi-v7a的so动态库。abiFilters。微信也是只保留这个。
 * 4.移除无用资源。remove unused resources命令谨慎使用，因为对于动态获取的资源id，未直接使用R.id.xxx，则这个id会被认为没有使用过。
 * 如int id = getResource().getIdentifier()获取的id。这样的也会被删除。
 * 但是Lint检查。unusedresources命令，对于动态获取资源id也能检查到。
 * 5.开启混淆。ProGuard的三大作用：
 * 压缩：移除未被使用的类、属性、方法等，并且会在优化动作执行之后再次执行（因为优化后可能会再次暴露一些未被使用的类和成员)
 * 优化：优化字节码，并删除未使用的结构。
 * 混淆：将类名、属性名、方法名混淆为难以读懂的字母。
 * 6.开启删除无用资源。shrinkResource = true。用来开启压缩无用资源，也就是没有被引用的文件（经过实测是drawable,layout，实际并不是彻
 * 底删除，而是保留文件名，但是没有内容，等等），但是因为需要知道是否被引用所以需要配合minifyEnabled使用，只有当两者都为true的时候才会起
 * 到真正的删除无效代码和无引用资源的目的。
 * 与4不同的是，比如 某个java类没有用到，被混淆时删除了，而该类引入了layout资源，此时会将这个资源也压缩掉。
 * 配置：raw->keep.xml文件中用于配置严格模式和是否保留文件。
 * //严格模式：删除资源
 * <resources xmlns:tools="http://schemas.android.com/tools"
 * tools:shrinkMode="strict" />
 * //手动保留：
 * <resources xmlns:tools="http://schemas.android.com/tools"
 * tools:keep="@layout/base_*"
 * tools:discard="@layout/unused2" />
 * 7.AndResGuard微信资源压缩方案。AndResGuard是一个缩小APK大小的工具，它的原理类似Java Proguard，但是只针对资源。它会将原本冗长的资
 * 源路径变短，例如将res/drawable/wechat变为r/d/a。
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