package com.blend.architecture.eventbus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.blend.architecture.R;
import com.blend.architecture.eventbus.core.CoreEventBusMainActivity;
import com.blend.architecture.eventbus.model.AsyncMessage;
import com.blend.architecture.eventbus.model.BackgroundMessage;
import com.blend.architecture.eventbus.model.MainMessage;
import com.blend.architecture.eventbus.model.PostingMessage;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * EventBus:主要就是四个方法：创建EventBus，注册，解注册，事件分发
 * <p>
 * 创建EventBus：使用双重检验单例模式和建造者模式，初始化一个EventBusBuilder来分别初始化EventBus的一些配置，主要配置是：
 * subscriptionsByEventType：key->订阅的事件，value->订阅这个事件的所有订阅者集合
 * typesBySubscriber：key->订阅者对象，这个订阅者订阅的事件结合
 * stickyEvents ：粘性事件，key->粘性事件的class对象，value->事件对象，调用postSticky方法的时候添加
 * 三个主要的事件线程：mainThreadPoster，backgroundPoster，asyncPoster
 * <p>
 * 注册：先从缓存里面去查找该类中订阅的所有方法，若没有，则通过反射的方式拿到该类所有的方法，然后依次遍历，先是判断是否只有一个参数，
 * 是否有注解，通过校验是否添加该方法（双重校验（1.允许一个类有多个参数相同的订阅方法。2.子类继承并重写了父类的订阅方法，那么只会
 * 把子类的订阅方法添加到订阅者列表，父类的方法会忽略。）），最后实例化SubscriberMethod（通过方法名，事件类型，model，优先级，是
 * 否粘性事件）。最后根据拿到的subscriberMethods列表，依次遍历subscribe方法，填充进subscriptionsByEventType集合和typesBySubscriber集合，
 * 并设置优先级。若有粘性事件，则分发粘性事件，通过依次遍历找到event（事件），post此事件给当前订阅者。
 * <p>
 * 解注册：typesBySubscriber中和subscriptionsByEventType去除掉。
 * <p>
 * 事件分发：通过ThreadLocal的currentPostingThreadState获取当前线程的状态并添加分发事件（保证在不同的线程中都拥有一份线程独立的
 * PostingThreadState对象，这样不同的线程就是同时分发），判断若是不处于分发状态，调用postSingleEvent进行逐条分发，在该方法中进行
 * 是否支持其父类/父接口事件继承分发。之后从subscriptionsByEventType中获取所有的Subscription列表，然后通过postToSubscription
 * 方法根据不同的threadMode，通过反射完成分发。
 * <p>
 * 使用到的设计模式：单例模式、建造者模式、观察者模式
 * <p>
 * 优点：简化组件之间的通信方式，实现解耦让业务代码更加简洁，可以动态设置事件处理线程以及优先级，调度灵活
 * 缺点：每个事件都必须自定义一个事件类，造成事件类太多，无形中加大了维护成本，导致了当接受者过多或相同参数时很难理清消息流，无法进行进程间通信
 */
public class EventBusMainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "EventBusMainActivity";

    private Button btnMain, btnBackground, btnAsync, btnPosting, btn1, btnCoreEventBus;
    private TextView tv_desc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_bus_main);
        EventBus.getDefault().register(this);

        btnCoreEventBus = findViewById(R.id.btnCoreEventBus);
        btnMain = findViewById(R.id.btnMain);
        btnBackground = findViewById(R.id.btnBackground);
        btnAsync = findViewById(R.id.btnAsync);
        btnPosting = findViewById(R.id.btnPosting);
        btn1 = findViewById(R.id.btn1);
        tv_desc = findViewById(R.id.tv_desc);

        btnMain.setOnClickListener(this);
        btnBackground.setOnClickListener(this);
        btnAsync.setOnClickListener(this);
        btnPosting.setOnClickListener(this);
        btn1.setOnClickListener(this);
        btnCoreEventBus.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnMain) {
            EventBus.getDefault().post(new MainMessage("MainMessage"));
        } else if (id == R.id.btnBackground) {
            EventBus.getDefault().post(new BackgroundMessage("BackgroundMessage"));
        } else if (id == R.id.btnAsync) {
            EventBus.getDefault().post(new AsyncMessage("AsyncMessage"));
        } else if (id == R.id.btnPosting) {
            EventBus.getDefault().postSticky(new PostingMessage("PostingMessage"));
        } else if (id == R.id.btn1) {
            Intent intent = new Intent(EventBusMainActivity.this, EventBusSecondActivity.class);
            startActivity(intent);
        } else if (id == R.id.btnCoreEventBus) {
            startActivity(new Intent(EventBusMainActivity.this, CoreEventBusMainActivity.class));
        }
    }

    //主线程中执行
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMainEventBus(MainMessage msg) {
        Log.e(TAG, msg.message);
        tv_desc.setText(msg.message);
    }

    //发送Object事件
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMainEventObject(Object msg) {
        Log.e(TAG, msg.toString());
    }

    //后台线程
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onBackgroundEventBus(BackgroundMessage msg) {
        Log.e(TAG, msg.message);
    }

    //异步线程
    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onAsyncEventBus(AsyncMessage msg) {
        Log.e(TAG, msg.message);
    }

    //默认情况，和发送事件在同一个线程
    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onPostEventBus(PostingMessage msg) {
        Log.e(TAG, msg.message);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}