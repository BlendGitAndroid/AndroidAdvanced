package com.blend.ui.MenuDrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.blend.ui.R;

public class MenuDrawerLayoutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_drawer_layout);
    }

    public void test(View view) {
        TextView tv = (TextView) view;
        Toast.makeText(this, "调用" + tv.getText() + "菜单", Toast.LENGTH_SHORT).show();
    }
}
