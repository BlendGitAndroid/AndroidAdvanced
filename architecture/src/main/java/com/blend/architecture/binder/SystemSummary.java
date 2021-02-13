package com.blend.architecture.binder;

/**
 * Android Framework提供的主要组件：
 * 1.Activity Manager(活动管理器)：管理各个应用程序的声明周期，以及常用的导航回退功能。
 * 2.Package Manager(包管理器)：管理所有安装在Android系统中的应用程序。
 * 3.Window Manager(窗口管理器)：管理所有开启的窗口程序。
 * <p>
 * <p>
 * Init进程启动过程：
 * 1.init进程是android系统中用户空间的第一个进程，进程号为1，创建Zygote和属性服务等。
 * Android系统启动前几步：
 * 1.启动电源以及系统启动。
 * 2.引导程序Bootloader。
 * 3.Linux 内核启动。
 * 4.init 进程启动。主要用来初始化和启动属性服务，也用来启动Zygote进程。
 * <p>
 * <p>
 * Zygote进程：DVM和ART，应用程序进程以及运行系统的关键服务的SystemService进程都是由Zygote进程创建的，我们也将它称为孵化器。
 * 它通过fock（复制进程）的形式来创建应用程序进程和SystemServer进程，由于Zygote进程在启动时会创建DVM或者ART，因此通过fock而
 * 创建的应用程序进程和System Server进程可以在内部获取一个DVM或者ART的实例副本。
 * Zygote的main方法主要做了4 件事：
 * 1.创建一个Server端的Socket 。创建一个Server端的Socket，这个name为“zygote”的Socket用于等待ActivityManagerService请求
 * Zygote来创建新的应用程序进程。
 * 2.预加载类和资掘。
 * 3.启动SystemServer进程。SystemService主要用于创建系统服务，我们熟知的AMS，WMS和PMS都是由它来创建的。SystemServer进程被创建后，
 * 主要做了如下工作：
 * 1)启动Binder线程池，这样就可以与其他进程进行通信。
 * 2)创建SystemServiceManager，其用于对系统的服务进行创建、启动和生命周期管理。
 * 3)启动各种系统服务。AMS,WMS和PMS。
 * 4.等待AMS请求创建新的应用程序进程。
 * <p>
 * <p>
 * Launch进程：系统启动的最后一步是启动一个应用程序用来显示系统中已经安装的应用程序，这个应用程序就叫作Launcher。Launcher在启动过程
 * 中会请求PackageManagerService返回系统中已经安装的应用程序的信息，并将这些信息封装成一个快捷图标列表显示在系统屏幕上，这样用户可以
 * 通过点击这些快捷图标来启动相应的应用进程。
 * <p>
 * <p>
 * <p>
 * 应用程序进程启动过程：
 * 要想启动一个应用程序，首先要保证这个应用程序所需要的应用程序进程已经启动。AMS在启动应用程序时会检查这个应用程序需要的应用程序进程是
 * 否存在，不存在就会请求Zygote进程启动需要的应用程序进程。我们知道在Zygote的Java框架层会创建一个Sever端的Socket，这个Socket用来
 * 等待AMS请求Zygote来创建新的应用程序进程。Zygote进程通过fock 身创建应用程序进程，这样应用程序进程就会获得Zygote进程在启动时创建
 * 的虚拟机实例。当然，在应用程序进程创建过程中除了获取虚拟机实例外，还创建了Binder线程地和消息循环，这样运行在应用进程中的应用程序就
 * 可以方便地使用Binder进行进程间通信以及处理消息了。
 * 1.AMS通过Socket发送启动应用程序进程请求。
 * 2.Zygote接收请求并创建应用程序进程。并在这个过程中通过反射获取ActivityThread实例，并反射动态调用main方法，应用程序进程就进入了
 * ActivityThread的main方法中，应用程序进程就创建完成了并且运行了主线程的管理类。
 * <p>
 * Binder线程池启动过程：在应用程序进程创建过程中会启动Binder线程池，并且创建一个Binder主线程，并将当前线程注册到Binder驱动中，
 * 新创建的应用程序就支持Binder进程间通信了。之后可能还会创建Binder线程，那都是普通线程，一样可以进程间通信。
 * <p>
 * 消息循环创建过程：这个消息循环就是在ActivityThread的main方法中创建的，也就是创建Looper。
 */
class SystemSummary {
}
