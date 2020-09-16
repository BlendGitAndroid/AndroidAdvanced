package com.blend.androidadvanced.ioc;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.blend.androidadvanced.R;

/**
 * 记一个坑：R文件在library中不是final的，只有在主工程中是final的
 * 但是注解需要final的，否则报错："Attribute value must be constant"
 */
@ContentView(R.layout.activity_ioc_main)
public class IocMainActivity extends BaseActivity {

    @ViewInject(R.id.app_text)
    private Button textView;
    private Button textView1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_ioc_main);
    }

    @Deprecated
    @OnClick({R.id.app_text, R.id.app_text1})
    public boolean click(View view) {
//        Toast.makeText(this,"---->"+textView,Toast.LENGTH_SHORT).show();
//       textView.setOnClickListener(new View.OnClickListener() {
//           @Override
//           public void onClick(View v) {
//
//           }
//       });

//        textView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                return false;
//            }
//        });
        NewsDialog newsDialog = new NewsDialog(this);
        newsDialog.show();
        return false;
    }

    @OnLongClick({R.id.app_text, R.id.app_text1})
    public boolean longClick(View view) {
        Toast.makeText(this, "长按了", Toast.LENGTH_SHORT).show();
        return true;
    }
}