package com.blend.ui.nested_scrolling;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.blend.ui.R;

public class NestedScrollingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nested_scrolling);
        Toolbar toolbar = findViewById(R.id.behaviorToolbar);
        setSupportActionBar(toolbar);
    }
}
