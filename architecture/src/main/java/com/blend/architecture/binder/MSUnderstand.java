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
 * <p>
 * <p>
 * Window机制：
 * 认为Window和View是不一样的，View是绘制在Window上的，Window是一个抽象虚拟的概念。
 * <p>
 * Window，PhoneWindow：这个类是一个View的顶级窗口，给View包裹上一层DicorView，是View的容器，是View的载体，显示View应该是什么样的布局。
 * 比如DecorView中的布局结构，会根据requestWindowFeature的不同而不同(如有title和没有title的布局)，将View的显示抽离出Activity。
 * <p>
 * WindowManager，WindowManagerImpl：管理PhoneWindow，因为PhoneWindow其实是DecorView的容器，DecorView也就是View树，
 * 也就是管理View树怎么显示的，比如这个View树应该在什么位置，是否有焦点等，并没有把Window正在创建出来，如设置View树的显示次序，
 * 控制Window的显示和触摸逻辑，设置软键盘的弹出策略等。
 * 虽然它里面也有View的add，remove，update，但是它不是真正的操作，真正的操作都是交给WindowManagerGlobal。
 * 也可以把这个View树理解成Window。
 * <p>
 * WindowManagerGlobal：单例的，每一个应用都有一个WindowManagerGlobal，里面保存View，ViewRootImpl，ParamLayout3个List，创建出WindowSession。
 * 是真正封装Window的添加(add)，删除(remove)和更新的(update)逻辑的。是用来管理一个应用的全部View。
 * <p>
 * ViewRootImpl：通过WindowSession负责与WMS通信，比如创建出Window；第二个作用是负责View的绘制。
 * 是Window和View之间沟通的桥梁，为什么这么说呢？
 * 添加：WindowManagerGlobal创建出ViewRootImpl后，通过ViewRootImpl与WMS创建出Window，然后通知View的绘制。
 * 更新：WMS需要修改View，WMS通过windowStatus存储了viewRootImpl的相关信息，这样如果WMS需要修改view，直接通过viewRootImpl就可以修改view了。
 * 可以看到所有的Window添加，更新都是先通过ViewRootImpl与WMS交互，然后WMS通过后再进行View的绘制更改。
 *
 * <p>
 * WMS：真正管理Window的类，它对每一个应用创建一个Session，通过这个Session的addToDisplay通知需要添加窗口，底层会通过
 * SurfaceFlinger来触发硬件绘制显示。WMS是用来控制Window创建显示的，WindowManager是用来管理Window显示位置，次序的。
 */
class MSUnderstand {
}
