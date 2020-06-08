package com.blend.architecture.binder;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

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
 * 需要注意的地方：当客户端发起远程请求时，当前线程会被挂起直至服务端进程返回数据，如果一个远程方法时很耗时的，那么不能在UI线程中发起此远程请求；
 * 其次，由于服务端的Binder方法运行在Binder的线程池中，所以Binder不管是否耗时都应该采用同步的方式去实现，因为它已经运行在一个线程中了。
 * <p>
 * 下面详细介绍这两个类的每个方法的含义：
 * DESCRIPTOR：Binder的唯一标识，一般为类名。
 * asInterface：用于将服务端的Binder对象转换成客户端所需的AIDL接口类型的对象，这个转换过程是区分进程的，若为同一进程，返回的就是服务端的
 * Stub对象本身，否则返回的是系统封装后的Stub.proxy对象。
 * asBinder：返回当前Binder对象。
 * onTransact：这个方法运行在服务端的Binder线程池中，当客户端发起夸进程请求时，远程请求会通过系统底层封装后交由此方法来处理。服务端通过code
 * 可以确定客户端所请求的目标方法是什么，接着从data中取出目标方法所需的参数（如果有的话），然后执行目标方法。当目标方法执行完毕后，就向reply中
 * 写入返回值（如果有的话）。若返回false，那么客户端的请求会调用失败。
 * <p>
 * 不提供AIDL文件依然可以实现Binder，之所以提供，系统根据AIDL文件生成java文件的格式是固定的，会为我们自动生成代码。
 * <p>
 * 需要注意：AIDL的包结构在客户端和服务端要保持一致，否则运行会出错，这是因为客户端需要反序列化服务端中和AIDL接口相关的所有类，如果类的完整路径
 * 不一致的话，就无法成功反序列化。
 *
 * 其他的知识点：
 * 1.在AIDL中基于观察者模式注册监听，当要解注册的时候，需要使用RemoteCallbackList来添加监听器。
 * 2.当Binder意外死亡时，需要重连服务。一种是设置DeathRecipient监听，另外一种是onServiceDisconnected中重连远程服务。
 * 3.当有多个AIDL Service时，应该创建Binder连接池：将每个业务模块的Binder请求统一转发到远程Service中去执行，从未避免了重复创建Service的过程。
 */
public class BinderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_binder);
    }
}
