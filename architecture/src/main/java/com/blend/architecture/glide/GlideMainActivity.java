package com.blend.architecture.glide;

import android.content.Intent;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.LruCache;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.blend.architecture.R;
import com.blend.architecture.glide.glide.Glide;
import com.blend.architecture.glide.glide.request.RequestOptions;

import java.io.File;

/**
 *
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
    }

    public void toNext(View view) {
        startActivity(new Intent(this, GlideSecondActivity.class));
    }
}