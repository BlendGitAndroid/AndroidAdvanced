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