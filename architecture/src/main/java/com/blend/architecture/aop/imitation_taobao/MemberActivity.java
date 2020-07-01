package com.blend.architecture.aop.imitation_taobao;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.blend.architecture.R;
import com.blend.architecture.aop.imitation_taobao.util.SharePreferenceUtil;

public class MemberActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menber_acitivity);
    }

    public void login(View view) {
        SharePreferenceUtil.setBooleanSp(SharePreferenceUtil.IS_LOGIN, true, this);
        finish();
    }
}