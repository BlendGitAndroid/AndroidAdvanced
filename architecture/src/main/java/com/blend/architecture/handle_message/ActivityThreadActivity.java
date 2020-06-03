package com.blend.architecture.handle_message;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.blend.architecture.R;

/**
 * TODO:Loop会造成CPU挂起，是一种主动的状态。
 * 问题：阻塞，挂起，休眠的区别？
 * 睡眠和挂起是两种行为，阻塞则是一种状态。
 * 挂起：会主动释放CPU，也会主动占用CPU
 * sleep:不释放锁，但是释放CPU，时间到了执行CPU任务
 * 阻塞：被动的释放CPU，但是一旦情况满足，又会继续执行CPU任务
 */
public class ActivityThreadActivity extends AppCompatActivity {

    private static final String TAG = "ActivityThreadActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread);

        MyLooper.prepare();

        final MyHandler myHandler = new MyHandler() {
            @Override
            public void handleMessage(MyMessage message) {
                super.handleMessage(message);
                Log.e(TAG, "handleMessage: " + message.target);
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                MyMessage message = new MyMessage();
                message.what = 1;
                message.obj = "加油！";
                myHandler.sendMessage(message);
            }
        }).start();

        MyLooper.loop();
    }
}
