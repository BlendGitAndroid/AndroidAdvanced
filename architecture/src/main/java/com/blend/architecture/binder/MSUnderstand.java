package com.blend.architecture.binder;

/**
 * AMS在Android系统中扮演很重要的角色，主要负责系统中四大组件的启动、切换、调度及应用进程的管理和调度等工作，其职责与操作系统中的进程管理
 * 和调度模块相类似。当发起进程启动或者组件启动时，都会通过Binder通信机制将请求传递给AMS，AMS再做统一处理。Activity的栈管理就是在AMS中。
 * 在Android代码中通过ActivityManager的getService方法通过Binder机制得到IActivityManager，这个IActivityManager就是AMS在本地的代理。
 * <p>
 * <p>
 * WindowManager:Window是一个抽象类，具体的实现类为PhoneWindow，它对View进行管理。WindowManager是一个接口类，继承自接口ViewManager，
 * 从名称就知道它是用来管理Window的，它的实现类为WindowManagerImpl。如果我们想要对Window (View)进行添加、更新和删除操作就可以使用WindowManager,
 * WindowManager会将具体的工作交由WMS来处理，WindowManager和WMS通过Binder来进行跨进程通信，WMS作为系统服务有很多API是不会暴露给WindowManager的，
 * 这一点与ActivityManager和AMS的关系有些类似。
 */
class MSUnderstand {
}
