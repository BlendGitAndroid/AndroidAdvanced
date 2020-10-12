package com.blend.optimization.bigview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.blend.optimization.R;

import java.io.IOException;
import java.io.InputStream;

/**
 * Android中对于长图，巨图的加载
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