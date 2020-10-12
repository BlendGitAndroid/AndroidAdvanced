package com.blend.optimization.bitmapmanage;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.blend.optimization.R;

/**
 * 图片管理，这只是一个思路，具体还要看Glide细节。
 * Bitmap内存管理，主要是先对图片进行缩放，减少内存，接着是对图片的内存进行管理，利用Lru算法，设计复用池，减少频繁的内存开销。
 * 图片的内存管理主要是内存，磁盘，网络三部分。
 * <p>
 * 为什么复用池设计成使用软引用？
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