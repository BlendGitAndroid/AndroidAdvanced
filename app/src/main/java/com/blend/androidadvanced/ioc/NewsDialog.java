package com.blend.androidadvanced.ioc;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.blend.androidadvanced.R;


@ContentView(R.layout.activity_ioc_dialog_main)
public class NewsDialog extends BaseDialog {
    @ViewInject(R.id.dialogBtn)
    Button dialogBtn;

    public NewsDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toast.makeText(getContext(), "dialogBtn " + dialogBtn, Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.dialogBtn)
    public void click(View view) {
        Toast.makeText(getContext(), "  dialog点击啦", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void show() {
        super.show();
        /**
         * 设置宽度全屏，要设置在show的后面
         */
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.gravity = Gravity.BOTTOM;
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().getDecorView().setPadding(0, 0, 0, 0);
        getWindow().setAttributes(layoutParams);
    }
}
