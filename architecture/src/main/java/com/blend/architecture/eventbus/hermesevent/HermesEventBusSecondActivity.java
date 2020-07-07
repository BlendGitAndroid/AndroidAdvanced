package com.blend.architecture.eventbus.hermesevent;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.blend.architecture.R;
import com.blend.architecture.eventbus.core.Hermes;
import com.blend.architecture.eventbus.manager.IUserManager;
import com.blend.architecture.eventbus.service.HermesService;

/**
 * 在真正的使用过程中，客户端和服务端使用Hermes库进行通信，因此只有一套AIDL文件存在库中
 */
public class HermesEventBusSecondActivity extends AppCompatActivity {

    IUserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hermes_event_bus_second);
        //连接服务器，进行binder连接
        Hermes.getDefault().connect(this, HermesService.class);

    }

    public void userManager(View view) {
        //得到对象
        userManager = Hermes.getDefault().getInstance(IUserManager.class);
    }

    public void getUser(View view) {
        //接收从服务端传过来的数据
        Toast.makeText(this, "-----> " + userManager.getFriend().toString(), Toast.LENGTH_SHORT).show();
//        userManager.setFriend(new Friend("jett",20));
    }
}