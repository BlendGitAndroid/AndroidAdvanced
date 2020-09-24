package com.blend.optimization.memory;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.blend.optimization.R;

/**
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
 * 1.数据类型。不要使用比需求更占空间的基本数据类型。
 * 2.循环尽量用foreach,少用iterator，自动装箱尽量少用。
 * 3.数据结构与算法(数组，链表，栈，树，图...)的解度处理。如数据量千级以内可以使用Sparse数组(key为整数)，ArrayMap（key为对象）
 * 性能不如HashMap但节约内存。
 * 4.枚举优化。每一个枚举值都是一个单例对象,在使用它时会增加额外的内存消耗,所以枚举相比与Integer和String会占用更多的内存；
 * 较多的使用 Enum 会增加 DEX 文件的大小,会造成运行时更多的IO开销,使我们的应用需要更多的空间；特别是分dex多的大型APP，枚
 * 举的初始化很容易导致ANR。枚举可以进行改进，使用注解的形式。
 * 5.static和static final的问题，static会由编译器调用clinit方法进行初始化，static final不需要进行初始化工作，打包在dex文件中
 * 可以直接调用，并不会在类初始化申请内存。所以基本数据类型的成员，可以全写成static final。
 * 6.字符串的连接尽量少用加号(+)。
 * 7.同一个方法多次调用，如递归函数，回调函数中new对象,读流直接在循环中new对象等。不要在onMeasure()，onLayout(),onDraw()中去刷新
 * UI（requestLayout，invalid）。
 * 8.避免GC回收将来要重用的对象。使用内存设计模式对象沲+LRU算法。
 */
public class MemoryMainActivity extends AppCompatActivity {

    private static final String TAG = "MemoryMainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_main);

        SHAPE s = new SHAPE();
        s.setShape(SHAPE.CIRCLE | SHAPE.RECTANGLE);
        System.out.println(s.getShape());

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
}