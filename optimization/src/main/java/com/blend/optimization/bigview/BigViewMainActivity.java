package com.blend.optimization.bigview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.blend.optimization.R;

import java.io.IOException;
import java.io.InputStream;

/**
 * Android中对于长图，巨图的加载
 * 思路：自定义View
 * 1.设置图片需要加载的区域，进行长宽的缩放
 * 2.Bitmap设置成复用，设置BitmapRegionDecoder区域解码器，这样在这个区域中的Bitmap就能复用内存，减少内存的开销
 * 3.设置滑动和触摸监听，在onScroll方法中进行图片的滑动，通过invalidate()不断调用onDraw方法，进行图片的加载。
 */
public class BigViewMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_big_view_main);
        BigView bigView = findViewById(R.id.bigViewBg);
        InputStream is = null;
        try {
            //加载图片
            is = getAssets().open("big.png");
            bigView.setImage(is);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}