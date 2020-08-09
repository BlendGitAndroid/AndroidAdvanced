package com.blend.architecture.plug_in.hook;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.blend.architecture.R;

public class SecondActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hook_second);
    }
}
