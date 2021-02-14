package com.blend.architecture.binder;

/**
 * 根Activity的启动过程：
 * 1.Launcher请求AMS的过程。Launcher启动后会将已安装应用程序的快捷图标显示到桌面上，这些应用程序的快捷图标就是启动根
 * Activity的入口，当我们点击某个应用程序的快捷图标时，就会通过Launcher请求AMS来启动该应用程序。
 * 在这里获取AMS是采用AIDL的方式，IActivityManager是AIDL文件，服务端也就是AMS只要继承IActivityManager.Stub类并
 * 实现相应的方法就能进行进程间通信了。
 * 2.AMS到ApplicationThread的调用过程。最后到调用到app.thread，指的是IApplicationThread，它的实现是ActivityThread的内部类
 * ApplicationThread，其中ApplicationThread继承了IApplicationThread.Stub 。app指的是传入的要启动的Activity所在的应用程序
 * 进程，因此，这段代码指的就是要在目标应用程序进程启动Activity。当前代码逻辑运行在AMS所在的进程（SystemServer进程）中，通过
 * ApplicationThread来与应用程序进程进行Binder通信，换句话说， ApplicationThread是AMS所在进程和应用程序进程的通信桥梁。
 * 其实这也是一个Binder通信，这一回应用程序进程成了服务端。
 * 3.ActivityThread启动Activity的过程。因为ApplicationThread是一个Binder，它的调用逻辑运行在Binder线程池中，所以需要使用
 * handle将代码的逻辑切换到主线程中。之后涉及到创建启动Activity的上下文环境，用类加载器创建该Activity实例，创建Application，初始化
 * Activity。
 * ActivityInfo用于存储代码以及AndroidManifes设置的Activity和Receiver节点信息，比如Activity的theme和launchMode 。
 * 要启动的Activity的ComponentName类，在ComponentName类中保存了该Activity的包名和类名。
 * 根据ComponentName 存储的Activity类名，类加载器来创建该Activity的实例。
 * <p>
 * 根Activity的启动涉及到4个进程：Zygote进程，Launcher进程，AMS进程，应用程序进程。
 * 首先Launcher进程向AMS通过Binder机制请求创根Activity，然后AMS通过Socket通信向Zygote进程请求创建应用程序进程，应用程序创建好之后，
 * 通过Binder机制AMS与ActivityThread中的ApplicationThread进行通信。
 * 而普通的Activity则涉及到2个进程。
 * <p>
 * <p>
 * <p>
 * Service的启动过程：
 * 1.ContextImpl到AMS的调用过程。通过调用ContextWrapper的startService方法，最后调动的ContextImpl的startService方法，之后和Activity
 * 一样，都是调用的是AMS中的方法。
 * 2.ActivityThread启动Service。在AMS中会判断想要在哪一个进程中运行，默认是当前进程，最后调用ActivityThread的scheduleCreateService方法。
 * 之后流程和Activity基本一样，获取类加载器并创建Service，创建上下文环境，初始化Service，调用Service的onCreate等。
 * <p>
 * <p>
 * <p>
 * Service的绑定过程：
 * binderServer的过程中会启动Service，之后会进行绑定工作并调用ActivityThread的scheduleBindService，经过handler等操作后，又会调用AMS的
 * 方法，会拿到IServiceConnection，是ServiceConnection的代理，为了解决当前应用程序和Service跨进程通信的问题。在这个IServiceConnection.stub
 * 的实现类中，通过ActivityThread的post方法，将Binder线程池转到当前应用的主线程中，最后调用ServiceConnection的onServiceConnected，这样Service
 * 端的onServiceConnected代码就会被执行，这也是为什么onServiceConnected的第二个参数是Binder，为的就是跨进程通信。
 * <p>
 * <p>
 * <p>
 * BroadCast的注册，发送和接收过程：
 * 1.广播的注册registerReceiver(BroadcastReceiver receiver,IntentFilter filter)。
 * 分别是静态注册和动态注册，静态注册安装时由PMS来完成注册。同样也是在AMS中完成的，有一个ReceiveList用来存储广播接收者，并根据BroadcastFilter
 * 找到对应的广播接收者。
 * 2.广播的发送和接收过程。将动态注册的广播接收者和静态注册的广播接收者按照优先级高低不同存储在不同的列表中，再将这两个列表合并到receivers列表中，
 * 这样receivers列表包含了所有的广播接收者。最后发送广播到这个列表，找到相应的接收者并发送到Binder线程池的服务端，再通过Handle到onReceive方法。
 * <p>
 * <p>
 * <p>
 * ContentProvider的启动过程：
 */
class StartUpProcess {

}
