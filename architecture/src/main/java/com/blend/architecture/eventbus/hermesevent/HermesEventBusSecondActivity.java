package com.blend.architecture.eventbus.hermesevent;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.blend.architecture.R;
import com.blend.architecture.eventbus.core.Hermes;
import com.blend.architecture.eventbus.manager.IUserManager;
import com.blend.architecture.eventbus.service.HermesService;

public class HermesEventBusSecondActivity extends AppCompatActivity {

    IUserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hermes_event_bus_second);
        Hermes.getDefault().connect(this, HermesService.class);

    }

    public void userManager(View view) {
        userManager = Hermes.getDefault().getInstance(IUserManager.class);
    }

    public void getUser(View view) {
        //1、接收从服务端传过来的数据
        Toast.makeText(this, "-----> " + userManager.getFriend().toString(), Toast.LENGTH_SHORT).show();
//        userManager.setFriend(new Friend("jett",20));
    }
}