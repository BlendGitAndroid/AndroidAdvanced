package com.blend.architecture.binder;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.blend.architecture.R;

/**
 * Android系统会为每一个应用分配一个UID，具有相同UID的应用才能共享数据。Android为每一个应用分配一个独立的虚拟机，不同虚拟
 * 机在内存分配上有不同的地址空间，这也是不同进程不能共享内存的原因。
 * <p>
 * IPC主要包含三块内容：Serializable接口，Parcelable，和Binder。
 * <p>
 * Binder:是Android中的一个类，实现了IBinder接口。
 * 从IPC角度来说，Binder是Android中的一种跨进程通信方式，也可以理解为一种屋里设备；
 * 从Framework角度来说，Binder是ServiceManager连接各种Manager(ActivityManager,WindowManager)和相应ManagerService的桥梁；
 * 从Android应用层来说，Binder是客户端和服务端进行通信的媒介，当bindService的时候，服务端会返回一个包含了服务端业务调用的Binder对象，
 * 通过这个Binder对象，客户端就可以获取服务端提供的服务或者数据，这里的服务包括普通服务和AIDL服务。
 * <p>
 * 在根目录上.aidl会自动生成.java类，这个类是一个接口，声明了一个内部类Stub，就是一个Binder类。当客户端和服务端位于同一个进程时，
 * 不会走transact，位于两个进程时，走transact由其内部代理类Proxy完成。
 * 下面详细介绍这两个类的每个方法的含义：
 * DESCRIPTOR：Binder的唯一标识，一般为类名。
 * asInterface：用于将服务端的Binder对象转换成客户端所需的AIDL接口类型的对象，这个转换过程是区分进程的，若为同一进程，返回的就是服务端的
 * Stub对象本身，否则返回的是系统封装后的Stub.proxy对象。
 * asBinder：返回当前Binder对象。
 *
 */
public class BinderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_binder);
    }
}
