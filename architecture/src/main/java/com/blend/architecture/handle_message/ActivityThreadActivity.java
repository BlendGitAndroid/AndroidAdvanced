package com.blend.architecture.handle_message;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.blend.architecture.R;

/**
 * TODO:
 * 问题：阻塞，挂起，休眠的区别？
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
