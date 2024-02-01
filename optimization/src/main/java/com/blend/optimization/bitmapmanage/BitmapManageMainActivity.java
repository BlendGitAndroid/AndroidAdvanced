package com.blend.optimization.bitmapmanage;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.blend.optimization.R;

/**
 * 图片管理，这只是一个思路，具体还要看Glide细节。Glide中对于图片的优化是怎么样的，关键在于into里面。
 * Bitmap内存管理，主要是先对图片进行缩放，减少内存，接着是对图片的内存进行管理，利用Lru算法，设计复用池，减少频繁的内存开销。
 * 图片的内存管理主要是内存，磁盘，网络三部分。
 * 图片内存： 取决于像素点的数据格式和分辨率
 * scale = 设备屏幕密度/drawable目录设定的屏幕密度
 * 图片内存大小 = int(图片长度 * scale + 0.5f) * int(图片宽度 * scale) * 单位像素占字节数
 * 1.减少像素点大小，使用RGB_565，但这种格式不支持透明度
 * 2.降低分辨率inSampleSize
 * 3.使用Bitmap内存复用inMutable。
 * <p>
 * 为什么复用池设计成使用软引用？ 当一个对象只被软引用引用时，就能被GC回收。
 * Bitmap注意事项：
 * 1.及时回收分配的Bitmap内存，尤其是在加载很多图片的时候，不然很容易出现oom。
 * 2.如果在Activity中加载Bitmap，在其onStop或者onDestroy时调用Bitmap.recycle()方法释放native层分配的内存，避免内存泄露。
 * 3.在创建Bitmap时很容易抛出异常，所以创建Bitmap时尽量加上try-catch。
 * 4.缓存、复用Bitmap对象。
 * 5.加载Bitmap时进行采样 (压缩图片)。
 * <p>
 *  统一域名优化：
 * 在Android图片优化中，统一图片域名优化是指将应用中使用的图片资源统一存放在一个或少数几个域名下，以优化图片加载和网络请求的性能。
 * 通常情况下，一个应用中可能使用了多个不同的域名来加载图片资源，这些域名可能来自不同的CDN（内容分发网络）或图片存储服务提供商。每次加载图片
 * 时，都需要建立新的网络连接，进行DNS解析和建立TCP连接，这会带来一定的性能开销。
 * 通过统一图片域名，将所有图片资源存放在同一个域名下，可以实现以下优化效果：
 * 1. 减少网络连接次数：每个域名都需要建立一次网络连接，通过统一图片域名，可以减少不同域名的网络连接次数，从而降低网络请求的开销。
 * 2. 复用网络连接：当应用使用同一个域名加载多个图片资源时，可以复用已建立的网络连接，减少TCP连接的建立和断开，提高网络请求的效率。
 * 3. 增加缓存效果：当多个图片资源都存放在同一个域名下时，可以共享相同域名的缓存策略，提高图片的缓存命中率，减少网络请求的次数。
 * 4. 提高CDN的效果：通过统一图片域名，可以更好地利用CDN的缓存机制，提高图片的加载速度和用户体验。
 * 需要注意的是，统一图片域名优化应该综合考虑网络请求的并发性、域名解析的时间、CDN的缓存策略等因素，并根据具体的应用场景进行合理的配置和调整。
 */
public class BitmapManageMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bitmap_manage_main);

        ImageCache.getInstance().init(this, Environment.getExternalStorageDirectory() + "/Blend");

        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(new ImageAdapter(this));

        //假设是从网络上来的，这里没有使用复用池
//        BitmapFactory.Options options=new BitmapFactory.Options();
//        //如果要复用，需要设计成异变
//        options.inMutable=true;
//        Bitmap bitmap=BitmapFactory.decodeResource(getResources(),R.mipmap.wyz_p,options);
//        for(int i=0;i<100;i++){
//            options.inBitmap=bitmap;
//            bitmap=BitmapFactory.decodeResource(getResources(),R.mipmap.wyz_p,options);
//        }

    }

}