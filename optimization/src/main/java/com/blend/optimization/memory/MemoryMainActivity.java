package com.blend.optimization.memory;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;

import com.blend.optimization.R;

import java.lang.ref.WeakReference;

/**
 * 大佬博客：https://juejin.im/post/6844904096541966350#heading-59
 * 常见的内存分析工具：
 * DDMS，MAT，Finder-Activity(MAT上的二次开发)，LeakCanary，LeakInspector
 * <p>
 * Java虚拟机：
 * 运行时数据区域：
 * 线程私有：程序计数器、虚拟机栈、本地方法栈
 * 共享数据区：方法区、堆
 * 内存碎片会造成内存抖动，但是在虚拟机栈中是不会出现内存碎片的，只有在堆中会出现。
 * String存储在常量池中，使用的是char数组？？？那么静态变量呢，字符串常量呢？？？字面量是什么？？
 * 类的对象是存储在堆中，那么该对象的成员变量自然也存储在堆中。
 * <p>
 * GC垃圾回收器：
 * 1.引用计数法(iOS6使用)，缺点是互相引用容易出现计算器永不为0
 * 2.可达性分析法(android中使用)，可作为GC ROOT的对象，虚拟机栈正在运行使用的引用(静态属性、常量、JNI引用的对象)
 * 在这个方法中，GC是需要2次扫描才回收对象，所以可以用finalize去救活丢失引用的对象，但一般不使用。
 * <p>
 * 回收和引用类型的关系：
 * 强：平常使用的
 * 软：内存不足时回收，存放一些重要性不是很强又不能随便让清除的对象，比如图片切换到后台不需要马上显示了。如Glide中。
 * 弱：第一次扫到了，就标记下来，第二次扫到直接回收
 * 虚：幽灵 幻影引用   不对生存造成任何影响，用于跟踪GC的回收通知
 * <p>
 * 内存泄露：一个长生命周期的对象持有一个短生命周期对象的引用。通俗讲就是该回收的对象，因为引用问题没有被回收，最终会产生OOM。
 * <p>
 * 内存分析工具的使用：
 * Android Profiler结合Mat工具分析内存泄露，在Mat工具中，通过柱状图或查询语句查找内存泄露的类，在这个类中找到第一个不是自己
 * 写的语句，之后的调用就可以考虑断开引用链。
 * Profiler还能分析内存抖动，在短时间内通过查看Allocations已分配和Deallocation已回收的数量，若是两者都比较多，则说明会发生
 * 内存抖动，然后通过Allocation Call Stack来查看内存泄露的代码。
 * <p>
 * 处理方案：
 * 第三方SDK和Android源码引发的内存泄露：其目的就是断开引用链
 * 若是能使用第三方库解决的，就用SDK里面提供的方式，比如注册广播，在页面销毁时一定要注意移除广播；若第三库解决不了，
 * 就使用反射的形势，断开引用链就可以了。
 * <p>
 * 内存抖动：内存频繁的分配与回收，分配速度大于回收速度，最终产生OOM。
 * 回收算法：标记清除算法、复制算法、标记压缩算法、分代收集算法。
 * <p>
 * 优化内存良好的编码习惯：
 * 1.数据类型。不要使用比需求更占空间的基本数据类型，能使用Int的就不用Long。
 * 2.循环尽量用foreach，少用iterator，自动装箱尽量少用。
 * 3.数据结构与算法(数组，链表，栈，树，图...)的解度处理。如数据量千级以内可以使用Sparse数组(key为整数)，ArrayMap（key为对象）
 * 性能不如HashMap但节约内存，所以在大型公司中，不会使用HashMap。
 * 4.枚举优化。每一个枚举值都是一个单例对象,在使用它时会增加额外的内存消耗,所以枚举相比与Integer和String会占用更多的内存；
 * 较多的使用 Enum 会增加 DEX 文件的大小,会造成运行时更多的IO开销,使我们的应用需要更多的空间；特别是分dex多的大型APP，枚
 * 举的初始化很容易导致ANR。枚举可以进行改进，使用注解的形式。
 * 5.static和static final的问题，static会由编译器调用clinit方法进行初始化，static final不需要进行初始化工作，打包在dex文件中
 * 可以直接调用，并不会在类初始化申请内存。所以基本数据类型的成员，可以全写成static final。
 * 6.字符串的连接尽量少用加号(+)，若这样进行频繁的操作，可能发生内存抖动。
 * 7.同一个方法多次调用，如递归函数，回调函数中new对象，或者读流直接在循环中new对象等。如不要在onMeasure()，onLayout(),onDraw()中去
 * 刷新UI（requestLayout，invalid）。
 * 8.避免GC回收将来要重用的对象。使用内存设计模式对象沲+LRU算法。
 * 9.尽量不要使用WebView来加载H5界面，这样会发生内存泄露，是无解的。腾讯是使用到WebView的地方都会重新开一个进程。
 * 10.Activity组件造成的内存泄露。
 * 1)非业务需要不要把activity的上下文做参数传递，可以传递application的上下文。
 * 2)不要把和Activity有关联的对象写成static，如private static Button btn;private static Drawable drawable。
 * 3)非静态内部类和匿名内部类会持有activity引用，建议大家单独写个文件，或者内部类通过弱引用来引用外部类。
 * 4)单例模式持有activity引用。两种方式解决：一是使用弱引用；另外一种是使用全局application的Context，类似于1)。
 * 5)handler.postDelayed()问题。如果开启的线程需要传入参数，用弱引接收可解决问题；handler记得清除removeCallbacksAndMessages(null)。
 * 6)AsyncTask造成的内存泄露。这个时候可以使用弱引用，来规避内存泄露。
 * 11.尽量使用IntentService,而不是Service。
 */
public class MemoryMainActivity extends AppCompatActivity {

    private static final String TAG = "MemoryMainActivity";

    private MyHandler handler = null;
    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_main);

        //枚举的替代方案
        enumReplace();

        //缓冲池
        cachePool();

        //单例引用
        singleton();

        //Handle造成的内存泄露
        handle();

        //AsyncTask造成的内存泄露，将Activity变弱引用的关系，在GC时就会被回收
        new MyAsyncTask2(MemoryMainActivity.this).execute();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    static class MyHandler extends Handler {

        private WeakReference<MemoryMainActivity> m;    //如果一个类只有弱引用引用时，才会被回收

        public MyHandler(MemoryMainActivity activity) {
            m = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MemoryMainActivity activity = m.get();
            if (activity != null) {
                activity.mButton.setText("aa");
            }
        }
    }

    //这个类单独写个文件，然后使用弱引用
    static class MyAsyncTask2 extends AsyncTask {

        private WeakReference<MemoryMainActivity> activity;

        public MyAsyncTask2(MemoryMainActivity activity) {
            this.activity = new WeakReference<>(activity);
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            return doSomeing();
        }

        private Object doSomeing() {
            //做了很多事后
            return new Object();
        }
    }

    private void handle() {
        handler = new MyHandler(this);
        handler.postDelayed(new MyRunnable(this), 5000);
    }

    static class MyRunnable implements Runnable { //这里的runnable应该也要写成弱引用的方式

        private WeakReference<MemoryMainActivity> memoryActivity;

        public MyRunnable(MemoryMainActivity activity) {
            memoryActivity = new WeakReference<>(activity);
        }

        @Override
        public void run() {
            if (memoryActivity.get() != null) {
                memoryActivity.get().mButton.setText("");
            }
        }
    }

    private void singleton() {
        MemorySingleton.getInstance().setCallback(new MemorySingleton.Callback() {
            @Override
            public void callback() {

            }
        });
    }

    private void cachePool() {
        MyObjectPool pool = new MyObjectPool(2, 4);
        Object o1 = null;
        Object o2 = null;
        Object o3 = null;
        Object o4 = null;
        // Object o5 = null;
        try {
            o1 = pool.acquire();
            o2 = pool.acquire();
            o3 = pool.acquire();
            o4 = pool.acquire();
            // o5 = pool.acquire();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.e(TAG, "Blend: " + o1.hashCode());
        Log.e(TAG, "Blend: " + o2.hashCode());
        Log.e(TAG, "Blend: " + o3.hashCode());
        Log.e(TAG, "Blend: " + o4.hashCode());
        // Log.e(TAG, "Blend: " + o5.hashCode());
    }

    private void enumReplace() {
        SHAPE s = new SHAPE();
        s.setShape(SHAPE.CIRCLE | SHAPE.RECTANGLE);
        System.out.println(s.getShape());
    }
}