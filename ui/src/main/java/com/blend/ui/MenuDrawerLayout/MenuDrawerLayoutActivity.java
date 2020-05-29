package com.blend.ui.MenuDrawerLayout;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.blend.ui.R;

/**
 * 目前，三种状况结合完成自定义菜单定制，需要遵循职责分明原则
 * 自定义控件类型：
 * 1.继承
 * 继承ViewGroup：需要自己处理ViewGroup的测量，布局过程，还需要处理子View的测量和布局过程。
 * 继承特定的View：比如TextView，不需要自己支持wrap_content和padding。
 * 继承特定的ViewGroup：比如LinearLayout，不需要自己处理ViewGroup的测量和布局过程。
 * <p>
 * 2.自己绘制，需要重写onDraw方法，用于实现一些不规则的效果，即这种效果不方便通过布局的组合方式来达到，采用这种方式，
 * 需要自己支持wrap_content，并且padding也需要自己处理。
 * <p>
 * 3.组合
 * <p>
 * 本次的个性化侧滑菜单思路：
 * 1.背景绘制
 * 2.线性布局
 * 3.层叠(一般使用RelativeLayout和FrameLayout)
 * 4.事件分发(使用DrawerLayout进行事件分发)
 */
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
