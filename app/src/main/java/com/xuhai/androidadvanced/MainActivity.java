package com.xuhai.androidadvanced;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.xuhai.ui.UiMainActivity;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startActivity(new Intent(this, UiMainActivity.class));
    }
}
