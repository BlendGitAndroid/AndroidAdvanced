package com.blend.architecture.glide;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.LruCache;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.blend.architecture.R;
import com.blend.architecture.glide.glide.Glide;
import com.blend.architecture.glide.glide.request.RequestOptions;

import java.io.File;

/**
 * 1.Glide中印象最深的是什么？内存缓存(Lru和弱引用)和磁盘缓存机制。
 * 2.Glide图片写入的顺序和读取的顺序是什么？写：弱引用、Lru、磁盘；读：Lru、弱引用、磁盘。
 * 3.Glide中图片复用池是怎么设计的？
 * 4.Glide中内存溢出的处理有哪些？内存占用问题？内存优化问题？
 * 5.加载一张高像素的图片（1920*1080），其内部是如何处理的，图片是怎么压缩的；缩略图是怎么处理的。
 * 6.Glide中用的到设计模式？加载不同的资源：策略模式；
 * 7.叫你设计一款图片加载库，你会考虑哪些？缓存、复用池、多种图片加载方式、性能。
 */
public class GlideMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glide_main);

        ImageView iv = findViewById(R.id.iv);
        ImageView iv1 = findViewById(R.id.iv1);
        ImageView iv2 = findViewById(R.id.iv2);

        new LruCache<String, String>(10) {
            @Override
            protected int sizeOf(@NonNull String key, @NonNull String value) {
                return super.sizeOf(key, value);
            }

            @Override
            protected void entryRemoved(boolean evicted, @NonNull String key, @NonNull String oldValue, @Nullable String newValue) {
                super.entryRemoved(evicted, key, oldValue, newValue);
            }
        };

//        Bitmap.Config.RGB_565

        Glide.with(this).load("https://ss1.bdstatic" +
                ".com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=2669567003," +
                "3609261574&fm=27&gp=0.jpg22222222asads")
                .apply(new RequestOptions().error(R.drawable.ic_launcher_background).placeholder
                        (R.mipmap.ic_launcher).override(500, 500))
                .into(iv);


        Glide.with(this).load(Environment.getExternalStorageDirectory().getPath() + "/glide1.jpg")
                .into(iv1);
        Glide.with(this).load(new File(Environment.getExternalStorageDirectory().getPath() + "/glide2.jpg")).into(iv2);

        //调用原生的Glide加载图片
        // com.bumptech.glide.Glide.with(this)
        //         .load("https://tse3-mm.cn.bing.net/th/id/OIP.Gzze2RWjGPoKUivyJQvTrQHaE7?pid=Api&rs=1")
        //         .into(iv2);
    }

    public void toNext(View view) {
        startActivity(new Intent(this, GlideSecondActivity.class));
    }
}