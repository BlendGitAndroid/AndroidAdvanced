package com.blend.architecture.eventbus.hermesevent;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.blend.architecture.R;
import com.blend.architecture.eventbus.core.Hermes;
import com.blend.architecture.eventbus.manager.UserManager;
import com.blend.architecture.eventbus.model.Friend;

/**
 * Hermes是之前饿了么开源的一套专门用于跨进程通信的框架，基于aidl进行封装，但是在使用的时候完全不用考虑aidl。
 * github:https://github.com/736870598/SunHermes
 *
 * 使用流程：
 *  1.假设A进程为主进程，B进程为其他进程。
 *  2.在A进程和B进行中必须要有一个完全相同的接口。该接口主要提供给B进程使用。
 *  3.A进程中要有一个单例类实现该接口。
 *  4.在B进程中该接口类上面要加上注解 @ClassId("实现类的全路径")
 *  5.A进程中接口的实现类必须是单例的，而且获取单例方法名必须是：getInstance()
 *
 * Hermes的思路：
 * 1.A进程保存单例类信息到键值对集合中，分别保存类和方法。
 * 2.B进程连接到跨进程服务。
 * 3.B进程传入单例类接口，在B进程中通过动态代理拿到单例类的实例；并把这个单例类接口的全类名（通过注解的方式写在接口定义上），方法和参数，进行GSON序列化，通过AIDL传递给A进程
 * 4.A进程通过GSON反序列化后，根据全类名，通过反射得到单例类的实例，并通过反射调用相关的方法后得到结果，通过AIDL传递给B进程完成跨进程调用
 *
 * 为什么要使用单例？
 * 因为使用单例，反射后得到的还是单例，就能调用之前使用这个单例存储的值。
 *
 * 动态代理的原理？
 *
 */
public class HermesEventBusMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hermes_event_bus_main);

        //服务端注册，保存类和方法，将消息保存下来
        Hermes.getDefault().register(UserManager.class);

        UserManager.getInstance().setFriend(new Friend("xuhai", 18));

    }

    public void change(View view) {
        startActivity(new Intent(this, HermesEventBusSecondActivity.class));
    }

    public void getPerson(View view) {
        Toast.makeText(this, UserManager.getInstance().getFriend().toString(), Toast.LENGTH_SHORT).show();
    }

}