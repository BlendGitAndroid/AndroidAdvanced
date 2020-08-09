package com.example.taoplugin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.pluginstand.PayInterfaceBroadcast;


public class MyReceiver extends BroadcastReceiver implements PayInterfaceBroadcast {
    @Override
    public void attach(Context context) {
        Toast.makeText(context, "-----绑定上下文成功-----", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "-----插件收到广播-----", Toast.LENGTH_SHORT).show();
    }
}
